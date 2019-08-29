package com.summit.util;

import cn.hutool.core.date.DateTime;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.summit.common.entity.RestfulEntityBySummit;
import com.summit.common.entity.UserInfo;
import com.summit.common.web.filter.UserContextHolder;
import com.summit.constants.CommonConstants;
import com.summit.dao.entity.AccCtrlProcess;
import com.summit.dao.entity.AccCtrlRealTimeEntity;
import com.summit.dao.entity.AccessControlInfo;
import com.summit.dao.entity.Alarm;
import com.summit.dao.entity.CameraDevice;
import com.summit.dao.entity.FileInfo;
import com.summit.dao.entity.LockInfo;
import com.summit.dao.repository.AccCtrlProcessDao;
import com.summit.dao.repository.AccCtrlRealTimeDao;
import com.summit.dao.repository.AccessControlDao;
import com.summit.dao.repository.AlarmDao;
import com.summit.dao.repository.LockInfoDao;
import com.summit.entity.AccCtrlRealTimeInfo;
import com.summit.entity.BackLockInfo;
import com.summit.entity.LockRequest;
import com.summit.exception.ErrorMsgException;
import com.summit.sdk.huawei.model.AccCtrlStatus;
import com.summit.sdk.huawei.model.AlarmStatus;
import com.summit.sdk.huawei.model.FaceInfo;
import com.summit.sdk.huawei.model.LcokProcessResultType;
import com.summit.sdk.huawei.model.LockProcessMethod;
import com.summit.sdk.huawei.model.LockProcessType;
import com.summit.sdk.huawei.model.LockStatus;
import com.summit.service.AccCtrlProcessService;
import com.summit.service.AccCtrlRealTimeService;
import com.summit.service.AccessControlService;
import com.summit.service.CameraDeviceService;
import com.summit.service.impl.NBLockServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.ArrayList;
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
    @Autowired
    private AlarmDao alarmDao;
    @Autowired
    private AccCtrlRealTimeDao accCtrlRealTimeDao;
    @Autowired
    private AccCtrlRealTimeService accCtrlRealTimeService;

    /**
     * 根据开锁操作返回状态更新相应门禁和锁状态、实时信息状态
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

            //同时更新门禁实时信息
            AccCtrlRealTimeEntity accCtrlRealTimeEntity = new AccCtrlRealTimeEntity();
            accCtrlRealTimeEntity.setAccCtrlStatus(status);
            accCtrlRealTimeEntity.setLockStatus(status);
            accCtrlRealTimeEntity.setUpdatetime(new Date());
            accCtrlRealTimeDao.update(accCtrlRealTimeEntity, new UpdateWrapper<AccCtrlRealTimeEntity>().eq("lock_code", lockCode));
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
            //休眠0.2秒再查询状态
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        RestfulEntityBySummit back = null;
        try {
            back = nbLockServiceImpl.toQueryLockStatus(lockRequest);
        } catch (Exception e) {
            if(e instanceof ErrorMsgException)
                log.error(((ErrorMsgException) e).getErrorMsg());
            else log.error("开锁失败,{}",e.getMessage());
            return null;
        }
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
        if(CommonUtil.isEmptyStr(lockId))
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
        if(CommonUtil.isEmptyStr(lockCode))
            return null;
        List<LockInfo> lockInfos = lockInfoDao.selectList(new QueryWrapper<LockInfo>().eq("lock_code", lockCode));
        if(CommonUtil.isEmptyList(lockInfos))
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
        //设置锁信息
        accCtrlProcess.setLockCode(lockCode);
        accCtrlProcess.setLockId(lockId);
        accCtrlProcess.setProcessMethod(LockProcessMethod.INTERFACE_BY.getCode());
        accCtrlProcess.setProcessTime(new Date());
        accCtrlProcess.setProcessType(LockProcessType.UNLOCK.getCode());
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
            accessControlInfo.setAccessControlId(accCtrlPro.getAccessControlId());
            accessControlInfo.setAccessControlName(accCtrlPro.getAccessControlName());
            accCtrlProcess.setAccessControlName(accCtrlPro.getAccessControlName());
            accCtrlProcess.setAccessControlId(accCtrlPro.getAccessControlId());
            accCtrlProcess.setGender(accCtrlPro.getGender());
            //设置设备信息
            accCtrlProcess.setDeviceId(accCtrlPro.getDeviceId());
            accCtrlProcess.setDeviceIp(accCtrlPro.getDeviceIp());
            accCtrlProcess.setDeviceType(accCtrlPro.getDeviceType());
        }else {
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
                    if(CommonUtil.isEmptyStr(content))
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
        try {
            accCtrlProcessService.insertAccCtrlProcess(accCtrlProcess);
            log.info("门禁操作记录入库成功");
        } catch (Exception e) {
            log.error("门禁操作记录入库失败");
        }

        AccCtrlRealTimeEntity accCtrlRealTimeEntity = getAccCtrlRealTimeEntity(accCtrlProcess);
        try {
            accCtrlRealTimeService.insertOrUpdate(accCtrlRealTimeEntity);
            log.info("门禁实时信息入库成功");
        } catch (Exception e) {
            log.error("门禁实时信息入库失败");
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

    /**
     * 通过门禁操作记录id查询告警
     * @param accCtrlProId 门禁操作记录id
     * @return 门禁操作记录id对应告警
     */
    public Alarm getAlarmByAccCtrlProId(String accCtrlProId){
        if (CommonUtil.isEmptyStr(accCtrlProId)) {
            return null;
        }
        List<Alarm> alarms = alarmDao.selectList(new QueryWrapper<Alarm>().eq("acc_ctrl_pro_id", accCtrlProId));
        if(CommonUtil.isEmptyList(alarms)){
            return null;
        }
        return alarms.get(0);
    }

    /**
     * 根据门禁信息列表查询组装门禁实时信息列表
     * @param accessControlInfos 门禁信息列表
     * @return 门禁实时信息列表
     */
    public List<AccCtrlRealTimeInfo> getLockRealTimeInfo(List<AccessControlInfo> accessControlInfos) {
        if (CommonUtil.isEmptyList(accessControlInfos))
            return null;
        List<AccCtrlRealTimeInfo> accCtrlRealTimeInfos = new ArrayList<>();
        for (AccessControlInfo accCtrl : accessControlInfos){
            if(accCtrl == null)
                continue;
            String accessControlId = accCtrl.getAccessControlId();
            AccCtrlRealTimeInfo accCtrlRealTimeInfo = new AccCtrlRealTimeInfo();
            if(accessControlId != null){
                accCtrlRealTimeInfo.setAccessControlId(accessControlId);
                accCtrlRealTimeInfo.setAccessControlName(accCtrl.getAccessControlName());
                accCtrlRealTimeInfo.setAccCtrlStatus(accCtrl.getStatus());
                accCtrlRealTimeInfo.setLockId(accCtrl.getLockId());
                accCtrlRealTimeInfo.setLockCode(accCtrl.getLockCode());
                accCtrlRealTimeInfo.setLongitude(accCtrl.getLongitude());
                accCtrlRealTimeInfo.setLatitude(accCtrl.getLatitude());
//                accCtrlRealTimeInfo.setLockCode(accCtrl.getLockCode());
                List<AccCtrlProcess> accCtrlProcesses = accCtrlProcessService.selectAccCtrlProcessByAccCtrlId(accessControlId,null);

                if(CommonUtil.isEmptyList(accCtrlProcesses)){
                    continue;
                }
                //取最新的一条操作记录(dao sql已排好序)
                AccCtrlProcess accCtrlProcess = accCtrlProcesses.get(0);
                if(accCtrlProcess != null){
                    String userName = accCtrlProcess.getUserName();
                    //门禁记录中操作的具体摄像头
                    accCtrlRealTimeInfo.setDeviceIp(accCtrlProcess.getDeviceIp());
                    accCtrlRealTimeInfo.setDeviceType(accCtrlProcess.getDeviceType());
                    accCtrlRealTimeInfo.setName(userName);
                    String accCtrlProId = accCtrlProcess.getAccCtrlProId();
                    if(accCtrlProId != null){
                        accCtrlRealTimeInfo.setAccCtrlProId(accCtrlProId);
                        Alarm alarm = alarmDao.selectByAccCtrlProId(accCtrlProId, null);
                        if(alarm != null){
                            accCtrlRealTimeInfo.setAlarmId(alarm.getAlarmId());
                        }
                    }

                    accCtrlRealTimeInfo.setGender(accCtrlProcess.getGender());
                    Date birthday = accCtrlProcess.getBirthday();
                    try {
                        if(birthday != null)
                            accCtrlRealTimeInfo.setBirthday(CommonConstants.dateFormat.format(birthday));
                    } catch (Exception e) {
                        log.error("生日格式有误");
                    }
                    accCtrlRealTimeInfo.setProvince(accCtrlProcess.getProvince());
                    accCtrlRealTimeInfo.setCity(accCtrlProcess.getCity());
                    accCtrlRealTimeInfo.setCardId(accCtrlProcess.getCardId());
                    accCtrlRealTimeInfo.setCardType(accCtrlProcess.getCardType());
                    accCtrlRealTimeInfo.setFaceMatchRate(accCtrlProcess.getFaceMatchRate());
                    accCtrlRealTimeInfo.setFaceLibName(accCtrlProcess.getFaceLibName());
                    accCtrlRealTimeInfo.setFaceLibType(accCtrlProcess.getFaceLibType());
                    try {
                        if(accCtrlProcess.getProcessTime() != null)
                            accCtrlRealTimeInfo.setPicSnapshotTime(CommonConstants.timeFormat.format(accCtrlProcess.getProcessTime()));
                    } catch (Exception e) {
                        log.error("操作时间格式有误");
                    }
                    FileInfo facePanorama = accCtrlProcess.getFacePanorama();
                    if(facePanorama != null){
                        accCtrlRealTimeInfo.setFacePanoramaUrl(facePanorama.getFilePath());
                    }
                    FileInfo facePic = accCtrlProcess.getFacePic();
                    if(facePic != null){
                        accCtrlRealTimeInfo.setFacePicUrl(facePic.getFilePath());
                    }
                }
            }
            accCtrlRealTimeInfos.add(accCtrlRealTimeInfo);
        }
        return accCtrlRealTimeInfos;
    }

    /**
     * 通过门禁操作信息对象组装门禁实时信息对象以进行入库操作
     * @param accCtrlProcess  门禁操作信息对象
     * @return 门禁实时信息对象
     */
    public AccCtrlRealTimeEntity getAccCtrlRealTimeEntity(AccCtrlProcess accCtrlProcess) {

        String accessControlId;
        if (accCtrlProcess == null || (accessControlId = accCtrlProcess.getAccessControlId()) == null)
            return null;

        AccessControlInfo accessControlInfo = accessControlDao.selectById(accessControlId);
        AccCtrlRealTimeEntity accCtrlRealTimeEntity = new AccCtrlRealTimeEntity();
        //插入时需要
        Date processTime = accCtrlProcess.getProcessTime();
        accCtrlRealTimeEntity.setPicSnapshotTime(processTime);
        AccCtrlRealTimeEntity realTimeEntity = accCtrlRealTimeDao.selectRealTimeInfoByAccCtrlId(accessControlId, null);
        if(realTimeEntity != null && realTimeEntity.getAccCrtlRealTimeId() != null){
            accCtrlRealTimeEntity.setAccCrtlRealTimeId(realTimeEntity.getAccCrtlRealTimeId());
//            accCtrlRealTimeEntity.setPicSnapshotTime(realTimeEntity.getPicSnapshotTime());
        }
        String accCtrlProId = accCtrlProcess.getAccCtrlProId();
        accCtrlRealTimeEntity.setAccCtrlProId(accCtrlProId);
        accCtrlRealTimeEntity.setAccessControlId(accessControlId);
        accCtrlRealTimeEntity.setAccessControlName(accCtrlProcess.getAccessControlName());
        accCtrlRealTimeEntity.setLockId(accCtrlProcess.getLockId());
        accCtrlRealTimeEntity.setLockCode(accCtrlProcess.getLockCode());

        //设置门禁状态、经度、纬度需要查询门禁
        if(accessControlInfo != null){
            accCtrlRealTimeEntity.setAccCtrlStatus(accessControlInfo.getStatus());
            accCtrlRealTimeEntity.setLongitude(accessControlInfo.getLongitude());
            accCtrlRealTimeEntity.setLatitude(accessControlInfo.getLatitude());
        }
//        accCtrlRealTimeEntity.setLockStatus();
        accCtrlRealTimeEntity.setDevId(accCtrlProcess.getDeviceId());
        accCtrlRealTimeEntity.setDeviceIp(accCtrlProcess.getDeviceIp());
        accCtrlRealTimeEntity.setDeviceType(accCtrlProcess.getDeviceType());
        accCtrlRealTimeEntity.setName(accCtrlProcess.getUserName());
        accCtrlRealTimeEntity.setGender(accCtrlProcess.getGender());
        accCtrlRealTimeEntity.setBirthday(accCtrlProcess.getBirthday());
        accCtrlRealTimeEntity.setProvince(accCtrlProcess.getProvince());
        accCtrlRealTimeEntity.setCity(accCtrlProcess.getCity());
        accCtrlRealTimeEntity.setCardType(accCtrlProcess.getCardType());
        accCtrlRealTimeEntity.setCardId(accCtrlProcess.getCardId());
        accCtrlRealTimeEntity.setFaceMatchRate(accCtrlProcess.getFaceMatchRate());
        accCtrlRealTimeEntity.setFaceLibName(accCtrlProcess.getFaceLibName());
        accCtrlRealTimeEntity.setFaceLibType(accCtrlProcess.getFaceLibType());

        String facePanoramaUrl = accCtrlProcess.getFacePanoramaUrl();
        if(!CommonUtil.isEmptyStr(facePanoramaUrl)){
            accCtrlRealTimeEntity.setFacePanoramaUrl(facePanoramaUrl);
        }else{
            FileInfo facePanorama = accCtrlProcess.getFacePanorama();
            if(facePanorama != null){
                accCtrlRealTimeEntity.setFacePanoramaUrl(facePanorama.getFilePath());
            }
        }
        String facePicUrl = accCtrlProcess.getFacePicUrl();
        if(!CommonUtil.isEmptyStr(facePicUrl)){
            accCtrlRealTimeEntity.setFacePicUrl(facePicUrl);
        }else{
            FileInfo facePic = accCtrlProcess.getFacePic();
            if(facePic != null){
                accCtrlRealTimeEntity.setFacePicUrl(facePic.getFilePath());
            }
        }
//        accCtrlRealTimeEntity.setFaceMatchUrl();

//        accCtrlRealTimeEntity.setAlarmCount();

        if(accCtrlProId != null){
            accCtrlRealTimeEntity.setAccCtrlProId(accCtrlProId);
            Alarm alarm = alarmDao.selectByAccCtrlProId(accCtrlProId, null);
            if(alarm != null){
                accCtrlRealTimeEntity.setAlarmId(alarm.getAlarmId());
            }
        }
        accCtrlRealTimeEntity.setUpdatetime(new Date());

        return accCtrlRealTimeEntity;
    }
}
