package com.summit.controller;


import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.summit.common.entity.RestfulEntityBySummit;
import com.summit.common.entity.UserInfo;
import com.summit.common.web.filter.UserContextHolder;
import com.summit.constants.CommonConstants;
import com.summit.dao.entity.AccCtrlProcess;
import com.summit.dao.entity.AccessControlInfo;
import com.summit.dao.entity.LockInfo;
import com.summit.dao.repository.AccessControlDao;
import com.summit.dao.repository.LockInfoDao;
import com.summit.entity.BackLockInfo;
import com.summit.entity.LockRequest;
import com.summit.entity.ReportParam;
import com.summit.sdk.huawei.model.AccCtrlStatus;
import com.summit.sdk.huawei.model.LcokProcessResultType;
import com.summit.sdk.huawei.model.LockProcessMethod;
import com.summit.sdk.huawei.model.LockStatus;
import com.summit.service.AccCtrlProcessService;
import com.summit.service.impl.NBLockServiceImpl;
import com.summit.util.HttpClient;
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
    private HttpClient httpClient;
    @Autowired
    private NBLockServiceImpl nbLockServiceImpl;
    @Autowired
    private LockInfoDao lockInfoDao;
    @Autowired
    private AccessControlDao accessControlDao;
    @Autowired
    private AccCtrlProcessService accCtrlProcessService;

    @PostMapping(value = "/queryLockStatus")
    public RestfulEntityBySummit queryLockStatus(@RequestBody LockRequest lockRequest){
        return nbLockServiceImpl.toQueryLockStatus(lockRequest);
    }

    @PostMapping(value = "/unLock")
    public RestfulEntityBySummit unLock(@RequestBody LockRequest lockRequest){
        RestfulEntityBySummit result = nbLockServiceImpl.toUnLock(lockRequest);
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
        String lockId = null;
        String lockCode = lockRequest.getTerminalNum();
        if(lockCode == null){
            lockId = lockRequest.getLockId();
            if(lockId == null){
                return;
            }
            LockInfo lockInfo = lockInfoDao.selectById(lockId);
            if(lockInfo == null){
                return;
            }
            lockCode = lockInfo.getLockCode();
            if(lockCode == null){
                return;
            }
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
        //设置操作结果及失败原因
        BackLockInfo backLockInfo = null;
        if(backData == null){
            accCtrlProcess.setProcessResult(LcokProcessResultType.ERROR.getCode());
            accCtrlProcess.setFailReason("未知");
        }else{
            if((backData instanceof BackLockInfo)){
                backLockInfo = (BackLockInfo) backData;
                String type = backLockInfo.getType();

                Integer status = getLockStatus(lockRequest);
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
                        toUpdateAccCtrlAndLockStatus(status,lockCode);
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

    /**
     * 根据开锁操作返回状态更新相应门禁和锁状态
     * @param status 开锁操作返回状态
     * @param lockCode 当前摄像头对应锁编号
     */
    private void toUpdateAccCtrlAndLockStatus(Integer status, String lockCode) {
        AccCtrlStatus accCtrlStatus =  AccCtrlStatus.codeOf(status);
        LockStatus lockStatus = LockStatus.codeOf(status);
        //状态不合法则不更新
        if(accCtrlStatus != null){
            AccessControlInfo accessControlInfo = new AccessControlInfo();
            accessControlInfo.setStatus(status);
            accessControlInfo.setUpdatetime(new Date());
            //直接根据锁编号更新门禁状态
            accessControlDao.update(accessControlInfo, new UpdateWrapper<AccessControlInfo>().eq("lock_code", lockCode));
        }
        //状态不合法则不更新
        if(lockStatus != null){
            LockInfo lockInfo = new LockInfo();
            lockInfo.setStatus(lockStatus.getCode());
            lockInfo.setUpdatetime(new Date());
            lockInfoDao.update(lockInfo, new UpdateWrapper<LockInfo>().eq("lock_code", lockCode));
        }
    }

    /**
     * 查询开锁状态
     * @param lockRequest 请求参数
     * @return 开锁状态
     */
    private Integer getLockStatus(LockRequest lockRequest) {
        RestfulEntityBySummit back = nbLockServiceImpl.toQueryLockStatus(lockRequest);
        Object backData = back.getData();
        if((backData instanceof BackLockInfo)){
            return ((BackLockInfo) backData).getObjx();
        }
        return null;
    }

}
