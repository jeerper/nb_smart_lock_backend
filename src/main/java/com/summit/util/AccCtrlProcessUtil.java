package com.summit.util;

import cn.hutool.core.date.DateTime;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.summit.common.entity.RestfulEntityBySummit;
import com.summit.common.entity.UserInfo;
import com.summit.common.web.filter.UserContextHolder;
import com.summit.constants.CommonConstants;
import com.summit.dao.entity.AccCtrlProcess;
import com.summit.dao.entity.AccessControlInfo;
import com.summit.dao.entity.Alarm;
import com.summit.dao.entity.CameraDevice;
import com.summit.dao.entity.FileInfo;
import com.summit.dao.entity.LockInfo;
import com.summit.dao.repository.AccCtrlProcessDao;
import com.summit.dao.repository.AccessControlDao;
import com.summit.dao.repository.LockInfoDao;
import com.summit.entity.BackLockInfo;
import com.summit.entity.LockRequest;
import com.summit.sdk.huawei.model.AccCtrlStatus;
import com.summit.sdk.huawei.model.AlarmStatus;
import com.summit.sdk.huawei.model.FaceInfo;
import com.summit.sdk.huawei.model.LcokProcessResultType;
import com.summit.sdk.huawei.model.LockProcessMethod;
import com.summit.sdk.huawei.model.LockProcessType;
import com.summit.sdk.huawei.model.LockStatus;
import com.summit.service.AccCtrlProcessService;
import com.summit.service.AccessControlService;
import com.summit.service.CameraDeviceService;
import com.summit.service.impl.NBLockServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
public class AccCtrlProcessUtil {
    @Autowired
    private NBLockServiceImpl nbLockServiceImpl;
    @Autowired
    private AccessControlDao accessControlDao;
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

    /**
     * 根据开锁操作返回状态更新相应门禁和锁状态
     * @param status 开锁操作返回状态
     * @param lockCode 当前摄像头对应锁编号
     */
    public void toUpdateAccCtrlAndLockStatus(Integer status, String lockCode) {
        if(status == null)
            return;
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
    public Integer getLockStatus(LockRequest lockRequest) {
        try {
            //休眠半秒再查询状态
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        RestfulEntityBySummit back = nbLockServiceImpl.toQueryLockStatus(lockRequest);
        Object backData = back.getData();
        if((backData instanceof BackLockInfo)){
            return ((BackLockInfo) backData).getObjx();
        }
        return null;
    }

    /**
     * 根据锁id获取锁编号
     * @return 锁编号
     */
    public String getLockCodeById(String lockId){
        if(lockId == null || "".equals(lockId))
            return null;
        LockInfo lockInfo = lockInfoDao.selectById(lockId);
        String lockCode = null;
        if(lockInfo != null){
            lockCode = lockInfo.getLockCode();
        }
        return lockCode;
    }

    /**
     * 根据锁编号获取锁id
     * @return 锁id
     */
    public String getLockIdByCode(String lockCode){
        if(lockCode == null || "".equals(lockCode))
            return null;
        List<LockInfo> lockInfos = lockInfoDao.selectList(new QueryWrapper<LockInfo>().eq("lock_code", lockCode));
        if(lockInfos == null || lockInfos.isEmpty())
            return null;
        LockInfo lockInfo = lockInfos.get(0);
        String lockId = null;
        if(lockInfo != null){
            lockId = lockInfo.getLockId();
        }
        return lockId;
    }

    /**
     * 查询调用查询锁状态接口，插入一条锁操作记录，根据返回结果改变锁、门禁状态
     * @param backData 开锁时返回结果
     * @param lockRequest 请求参数
     */
    public void toInsertAndUpdateData(Object backData, LockRequest lockRequest) {
        if(lockRequest == null){
            return;
        }
        String lockId = lockRequest.getLockId();
        String lockCode = lockRequest.getTerminalNum();
        if(lockId == null && lockCode == null){
            return;
        }
        if(lockCode == null){
            lockCode = getLockCodeById(lockId);
        }
        if(lockId == null){
            lockId = getLockIdByCode(lockCode);
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
     * 根据人脸识别结果信息及其他信息组装需要入库的门禁操作记录信息
     * @param faceInfo 识别的人脸信息
     * @param type 人脸识别结果类型
     * @param facePanoramaFile 人脸全景图对象
     * @param facePicFile 人脸识别抠图对象
     * @param processResult 门禁操作结果返回码
     * @param failReason 人脸识别或门禁操作识别原因
     * @return 组装好的门禁操作记录信息对象
     */
    public AccCtrlProcess getAccCtrlProcess(FaceInfo faceInfo, String type, FileInfo facePanoramaFile, FileInfo facePicFile, String processResult, String failReason) {
        AccCtrlProcess accCtrlProcess = new AccCtrlProcess();
        String deviceIp = faceInfo.getDeviceIp();
        accCtrlProcess.setDeviceIp(deviceIp);
        accCtrlProcess.setProcessMethod(LockProcessMethod.FACE_RECOGNITION.getCode());
        //先设一默认值防止报错
        accCtrlProcess.setLockCode("");
        accCtrlProcess.setUserName(faceInfo.getName());
        if(faceInfo.getGender() != null)
            accCtrlProcess.setGender(faceInfo.getGender().getGenderCode());
        try {
            if(faceInfo.getBirthday() != null)
                accCtrlProcess.setBirthday(CommonConstants.dateFormat.parse(faceInfo.getBirthday()));
        } catch (ParseException e) {
            log.error("生日格式有误");
        }
        accCtrlProcess.setProvince(faceInfo.getProvince());
        accCtrlProcess.setCity(faceInfo.getCity());
        if(faceInfo.getCardType() != null)
            accCtrlProcess.setCardType(faceInfo.getCardType().getCardTypeCode());
        accCtrlProcess.setCardId(faceInfo.getCardId());
        accCtrlProcess.setFaceMatchRate(faceInfo.getFaceMatchRate());
        accCtrlProcess.setFaceLibName(faceInfo.getFaceLibName());
        if(faceInfo.getFaceLibType() != null)
            accCtrlProcess.setFaceLibType(faceInfo.getFaceLibType().getFaceLibTypeCode());
        accCtrlProcess.setFacePanorama(facePanoramaFile);
        accCtrlProcess.setFacePic(facePicFile);
        DateTime picSnapshotTime = faceInfo.getPicSnapshotTime();
        accCtrlProcess.setProcessTime(picSnapshotTime);
//        LockInfo lockInfo = new LockInfo();
        AccessControlInfo accessControlInfo = new AccessControlInfo();

        CameraDevice device = cameraDeviceService.selectDeviceByIpAddress(deviceIp);

        if(device != null){
            String lockCode = device.getLockCode();
            String lockId = device.getLockId();
            String deviceType = device.getType();
            AccessControlInfo acInfo = accessControlService.selectAccCtrlByLockCode(lockCode);
            if(acInfo != null){
                String accessControlId = acInfo.getAccessControlId();
                accCtrlProcess.setAccessControlId(accessControlId);
                String accessControlName = acInfo.getAccessControlName();
                accCtrlProcess.setAccessControlName(accessControlName);
                accessControlInfo.setAccessControlId(accessControlId);
                accessControlInfo.setAccessControlName(accessControlName);
            }
            String devId = device.getDevId();
            accCtrlProcess.setLockId(lockId);
            accCtrlProcess.setLockCode(lockCode);
            accCtrlProcess.setDeviceId(devId);
            accCtrlProcess.setDeviceType(deviceType);
            accessControlInfo.setLockId(lockId);
            accessControlInfo.setLockCode(lockCode);
            accessControlInfo.setUpdatetime(picSnapshotTime == null ? null : picSnapshotTime.toJdkDate());
        }
        if("Unlock".equals(type)){
            //改为在开锁真正成功后更新状态
//            accessControlInfo.setStatus(AccCtrlStatus.OPEN.getCode());
            accCtrlProcess.setProcessType(LockProcessType.UNLOCK.getCode());
            accCtrlProcess.setProcessResult(processResult);
            //刷脸成功后开锁操作也有可能失败,成功则failReason=null
            accCtrlProcess.setFailReason(failReason);
        }else if("Alarm".equals(type)){
            accessControlInfo.setStatus(AccCtrlStatus.ALARM.getCode());
            accCtrlProcess.setProcessType(LockProcessType.LOCK_ALARM.getCode());
            accCtrlProcess.setProcessResult(LcokProcessResultType.ERROR.getCode());
            accCtrlProcess.setFailReason("匹配度过低");

        }else{
            //关锁
            accessControlInfo.setStatus(AccCtrlStatus.CLOSED.getCode());
            accCtrlProcess.setProcessType(LockProcessType.CLOSE_LOCK.getCode());
            accCtrlProcess.setProcessResult("success");
        }
        accCtrlProcess.setAccessControlInfo(accessControlInfo);
        return accCtrlProcess;
    }

    /**
     * 根据门禁操作记录信息组装告警信息入库信息对象
     * @param accCtrlProcess 门禁操作记录对象
     * @return 门禁告警对象
     */
    public Alarm getAlarm(AccCtrlProcess accCtrlProcess) {
        Alarm alarm = new Alarm();
        alarm.setAccCtrlProId(accCtrlProcess.getAccCtrlProId());
        alarm.setAlarmTime(accCtrlProcess.getProcessTime());
        alarm.setAlarmStatus(AlarmStatus.UNPROCESSED.getCode());
        alarm.setDescription(accCtrlProcess.getFailReason());
        alarm.setUpdatetime(new Date());
        return alarm;
    }

}
