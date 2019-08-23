package com.summit.controller;


import com.summit.common.entity.ResponseCodeEnum;
import com.summit.common.entity.RestfulEntityBySummit;
import com.summit.common.entity.UserInfo;
import com.summit.common.util.ResultBuilder;
import com.summit.common.web.filter.UserContextHolder;
import com.summit.constants.CommonConstants;
import com.summit.dao.entity.AccCtrlProcess;
import com.summit.dao.entity.AccessControlInfo;
import com.summit.dao.repository.AccCtrlProcessDao;
import com.summit.dao.repository.LockInfoDao;
import com.summit.entity.BackLockInfo;
import com.summit.entity.LockRequest;
import com.summit.entity.ReportParam;
import com.summit.sdk.huawei.model.LcokProcessResultType;
import com.summit.sdk.huawei.model.LockProcessMethod;
import com.summit.sdk.huawei.model.LockStatus;
import com.summit.service.AccCtrlProcessService;
import com.summit.service.AccessControlService;
import com.summit.service.CameraDeviceService;
import com.summit.service.impl.NBLockServiceImpl;
import com.summit.util.AccCtrlProcessUtil;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@Slf4j
@Api(tags = "NB智能锁操作接口")
@RestController
@RequestMapping("/nbLock")
public class NBLockController {


    @Autowired
    private NBLockServiceImpl nbLockServiceImpl;
    @Autowired
    private LockInfoDao lockInfoDao;
    @Autowired
    private AccCtrlProcessService accCtrlProcessService;
    @Autowired
    private AccCtrlProcessDao accCtrlProcessDao;
    @Autowired
    private AccessControlService accessControlService;
    @Autowired
    private CameraDeviceService cameraDeviceService;
    @Autowired
    private AccCtrlProcessUtil accCtrlProcessUtil;

    @PostMapping(value = "/queryLockStatus")
    public RestfulEntityBySummit queryLockStatus(@RequestBody LockRequest lockRequest){
        return nbLockServiceImpl.toQueryLockStatus(lockRequest);
    }

    @PostMapping(value = "/unLock")
    public RestfulEntityBySummit unLock(@RequestBody LockRequest lockRequest){
        RestfulEntityBySummit result;
        boolean isUnLock;
        if(lockRequest == null){
            log.error("请求参数为空");
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9993,"请求参数为空", null);

        }
        result = nbLockServiceImpl.toUnLock(lockRequest);
        //查询调用查询锁状态接口，插入一条锁操作记录，改变锁、门禁状态
        toInsertAndUpdateData(result.getData(),lockRequest);
        return result;
    }



    @PostMapping(value = "/safeReport")
    public RestfulEntityBySummit safeReport(@RequestBody ReportParam reportInfo){
        return nbLockServiceImpl.toSafeReport(reportInfo);
    }


    /**
     * 查询调用查询锁状态接口，插入一条锁操作记录，根据返回结果改变锁、门禁状态
     * @param backData 开锁时返回结果
     * @param lockRequest 请求参数
     */
    private void toInsertAndUpdateData(Object backData, LockRequest lockRequest) {
        if(lockRequest == null){
            return;
        }
        String lockId = lockRequest.getLockId();
        String lockCode = lockRequest.getTerminalNum();
        if(lockId == null && lockCode == null){
            return;
        }
        if(lockCode == null){
            lockCode = accCtrlProcessUtil.getLockCodeById(lockId);
        }
        if(lockId == null){
            lockId = accCtrlProcessUtil.getLockIdByCode(lockCode);
        }

        //组装门禁历史对象，保存门禁操作历史
        AccCtrlProcess accCtrlProcess = new AccCtrlProcess();
        //设置锁信息(通过接口开锁无设备信息)
        accCtrlProcess.setLockCode(lockCode);
        accCtrlProcess.setLockId(lockId);
        accCtrlProcess.setProcessMethod(LockProcessMethod.INTERFACE_BY.getCode());
        accCtrlProcess.setProcessTime(new Date());
        UserInfo userInfo = UserContextHolder.getUserInfo();
        if(userInfo != null){
            accCtrlProcess.setUserName(userInfo.getName());
            String sex = userInfo.getSex();
            if(sex != null){
                Integer gender = null;
                try {
                    gender = Integer.parseInt(sex);
                } catch (NumberFormatException e) {
                    log.error("sex {} is illegal",sex);
                }
                accCtrlProcess.setGender(gender);
            }
        }
        accCtrlProcess.setProcessMethod(LockProcessMethod.INTERFACE_BY.getCode());
        //设置对应门禁信息
        AccessControlInfo accessControlInfo = new AccessControlInfo();
        String accCtrlProId = lockRequest.getAccCtrlProId();
        AccCtrlProcess accCtrlPro = null;
        if(accCtrlProId != null){
            accCtrlPro = accCtrlProcessDao.selectById(accCtrlProId);
        }
        if (accCtrlPro != null){
            accCtrlProcess.setLockCode(accCtrlPro.getLockCode());
            accCtrlProcess.setLockId(accCtrlPro.getLockId());
            accessControlInfo.setAccessControlId(accCtrlPro.getAccessControlId());
            accessControlInfo.setAccessControlName(accCtrlPro.getAccessControlName());
            accCtrlProcess.setAccessControlName(accCtrlPro.getAccessControlName());
            accCtrlProcess.setAccessControlId(accCtrlPro.getAccessControlId());
            accCtrlProcess.setGender(accCtrlPro.getGender());
            //todo 设置设备信息
            accCtrlProcess.setDeviceId(accCtrlPro.getDeviceId());
            accCtrlProcess.setDeviceIp(accCtrlPro.getDeviceIp());
            accCtrlProcess.setDeviceType(accCtrlPro.getDeviceType());
        }else {
            accCtrlProcess.setLockId(lockId);
            accCtrlProcess.setLockCode(lockCode);
            AccessControlInfo acInfo = accessControlService.selectAccCtrlByLockCode(lockCode);
            if(acInfo != null){
                String accessControlId = acInfo.getAccessControlId();
                accCtrlProcess.setAccessControlId(accessControlId);
                String accessControlName = acInfo.getAccessControlName();
                accCtrlProcess.setAccessControlName(accessControlName);
                accessControlInfo.setAccessControlId(accessControlId);
                accessControlInfo.setAccessControlName(accessControlName);
            }
        }

        accCtrlProcess.setAccessControlInfo(accessControlInfo);
        //设置操作结果及失败原因
        BackLockInfo backLockInfo = null;
        if(backData == null){
            accCtrlProcess.setProcessResult(LcokProcessResultType.ERROR.getCode());
            accCtrlProcess.setFailReason("未知");
        }else{
            if((backData instanceof BackLockInfo)){
                backLockInfo = (BackLockInfo) backData;
                String type = backLockInfo.getType();

                Integer status = accCtrlProcessUtil.getLockStatus(lockRequest);
                if(LcokProcessResultType.SUCCESS.getCode().equalsIgnoreCase(type)
                        && status != null && status == LockStatus.UNLOCK.getCode()){
                    accCtrlProcess.setProcessResult(LcokProcessResultType.SUCCESS.getCode());
                }else{
                    accCtrlProcess.setProcessResult(LcokProcessResultType.ERROR.getCode());
                    String content = backLockInfo.getContent();
                    if(content == null || "".equals(content))
                        accCtrlProcess.setFailReason("未知");
                    else
                        accCtrlProcess.setFailReason(content);
                }
                if(status != null){
                    if(status == LockStatus.UNLOCK.getCode() || status == LockStatus.LOCK_CLOSED.getCode()){
                        //改变锁、门禁状态为当前状态
                        accCtrlProcessUtil.toUpdateAccCtrlAndLockStatus(status,lockCode);
                    }
                }

            }else{
                accCtrlProcess.setProcessResult(LcokProcessResultType.ERROR.getCode());
                accCtrlProcess.setFailReason("未知");
            }
        }
        if(accCtrlProcessService.insertAccCtrlProcess(accCtrlProcess) != CommonConstants.UPDATE_ERROR){
            log.info("门禁操作记录入库成功");
        }else{
            log.error("门禁操作记录入库失败");
        }
    }


}
