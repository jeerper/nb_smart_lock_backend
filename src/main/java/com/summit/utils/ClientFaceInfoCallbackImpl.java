package com.summit.utils;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.io.FileUtil;
import cn.hutool.system.SystemUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.summit.MainAction;

import com.summit.common.entity.RestfulEntityBySummit;
import com.summit.constants.CommonConstants;
import com.summit.dao.entity.AccCtrlProcess;
import com.summit.dao.entity.AccessControlInfo;
import com.summit.dao.entity.Alarm;
import com.summit.dao.entity.CameraDevice;
import com.summit.dao.entity.FileInfo;
import com.summit.dao.entity.LockInfo;
import com.summit.dao.entity.LockProcess;
import com.summit.dao.repository.AccessControlDao;
import com.summit.dao.repository.LockInfoDao;
import com.summit.entity.BackLockInfo;
import com.summit.entity.LockRequest;
import com.summit.sdk.huawei.callback.ClientFaceInfoCallback;
import com.summit.sdk.huawei.model.AccCtrlStatus;
import com.summit.sdk.huawei.model.AlarmStatus;
import com.summit.sdk.huawei.model.FaceInfo;
import com.summit.sdk.huawei.model.FaceLibType;
import com.summit.sdk.huawei.model.LcokProcessResultType;
import com.summit.sdk.huawei.model.LockProcessMethod;
import com.summit.sdk.huawei.model.LockProcessType;
import com.summit.sdk.huawei.model.LockStatus;
import com.summit.service.AccCtrlProcessService;
import com.summit.service.AccessControlService;
import com.summit.service.AlarmService;
import com.summit.service.CameraDeviceService;
import com.summit.service.LockRecordService;
import com.summit.service.impl.NBLockServiceImpl;
import com.summit.util.LockAuthCtrl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
public class ClientFaceInfoCallbackImpl implements ClientFaceInfoCallback {

    @Autowired
    private NBLockServiceImpl unLockService;
    @Autowired
    private LockRecordService lockRecordService;
    @Autowired
    private AlarmService alarmService;
    @Autowired
    private CameraDeviceService cameraDeviceService;
    @Autowired
    private AccCtrlProcessService accCtrlProcessService;
    @Autowired
    private AccessControlService accessControlService;
    @Autowired
    private AccessControlDao accessControlDao;
    @Autowired
    private LockInfoDao lockInfoDao;


    @Override
    public void invoke(Object object) {
        try {
            if (object instanceof FaceInfo) {
                FaceInfo faceInfo = (FaceInfo) object;

                log.debug("============客户端调用开始=============");
                log.debug("设备IP:" + faceInfo.getDeviceIp());
                if (faceInfo.getFacePanorama() == null) {
                    log.debug("人脸全景为空!!!!!!!!!");
                }
                String type;
                if (faceInfo.getFaceLibType() == FaceLibType.FACE_LIB_ALARM) {
                    log.debug("开始报警!!!!！！！！");
                    type = "Alarm";
                } else {
                    type = "Unlock";
                }
                String deviceIp = faceInfo.getDeviceIp();
                String snapshotTime = CommonConstants.snapshotTimeFormat.format(new Date());
                String snapshotDate = CommonConstants.dateFormat.format(new Date());
                String picturePathFacePanorama = new StringBuilder()
                        .append(SystemUtil.getUserInfo().getCurrentDir())
                        .append(File.separator)
                        .append(MainAction.SnapshotFileName)
                        .append(File.separator)
                        .append(deviceIp)
                        .append(File.separator)
                        .append(snapshotTime)
                        .append(File.separator)
                        .append(type)
                        .append(File.separator)
                        .append(snapshotDate)
                        .append(CommonConstants.FACE_PANORAMA_SUFFIX)
                        .toString();

                String picturePathFacePic = new StringBuilder()
                        .append(SystemUtil.getUserInfo().getCurrentDir())
                        .append(File.separator)
                        .append(MainAction.SnapshotFileName)
                        .append(File.separator)
                        .append(deviceIp)
                        .append(File.separator)
                        .append(snapshotTime)
                        .append(File.separator)
                        .append(type)
                        .append(File.separator)
                        .append(snapshotDate)
                        .append(CommonConstants.FACE_PIC_SUFFIX)
                        .toString();

                String facePanoramaUrl = new StringBuilder()
                        .append(MainAction.SnapshotFileName)
                        .append(CommonConstants.URL_SEPARATOR)
                        .append(deviceIp)
                        .append(CommonConstants.URL_SEPARATOR)
                        .append(snapshotTime)
                        .append(CommonConstants.URL_SEPARATOR)
                        .append(type)
                        .append(CommonConstants.URL_SEPARATOR)
                        .append(snapshotDate)
                        .append(CommonConstants.FACE_PANORAMA_SUFFIX)
                        .toString();

                String facePicUrl = new StringBuilder()
                        .append(MainAction.SnapshotFileName)
                        .append(CommonConstants.URL_SEPARATOR)
                        .append(deviceIp)
                        .append(CommonConstants.URL_SEPARATOR)
                        .append(snapshotTime)
                        .append(CommonConstants.URL_SEPARATOR)
                        .append(type)
                        .append(CommonConstants.URL_SEPARATOR)
                        .append(snapshotDate)
                        .append(CommonConstants.FACE_PIC_SUFFIX)
                        .toString();

                FileInfo facePanoramaFile = new FileInfo(snapshotTime + CommonConstants.FACE_PANORAMA_SUFFIX, facePanoramaUrl, "人脸全景图");
                FileInfo facePicFile = new FileInfo(snapshotTime + CommonConstants.FACE_PIC_SUFFIX, facePicUrl, "人脸识别抠图");

                FaceLibType faceLibType = faceInfo.getFaceLibType();
                String processResult = null;
                String failReason = null;
                if(faceLibType.equals(FaceLibType.FACE_LIB_WHITE)){
                    CameraDevice cameraDevice = cameraDeviceService.selectDeviceByIpAddress(deviceIp);
                    if(cameraDevice != null) {
                        String lockCode = cameraDevice.getLockCode();
                        if (lockCode != null) {
                            LockRequest lockRequest = new LockRequest(null, lockCode, faceInfo.getName(),null);
                            RestfulEntityBySummit result = unLockService.toUnLock(lockRequest);
                            BackLockInfo backLockInfo = result.getData() == null ? null : (BackLockInfo) result.getData();
                            if(backLockInfo != null){
                                String backLockInfoType = backLockInfo.getType();
                                String content = backLockInfo.getContent();
                                log.debug("rmid={},type={},content={},objx={},time={}",
                                        backLockInfo.getRmid(), backLockInfoType, content, backLockInfo.getObjx(), backLockInfo.getTime());

                                if(LcokProcessResultType.SUCCESS.getCode().equalsIgnoreCase(backLockInfoType)){
                                    //查询调用查询锁状态接口，若查到状态变为开锁，则改变锁、门禁状态为打开，否则不改变状态
                                    Integer status = getLockStatus(lockRequest);
                                    if(status != null){
                                        if(status == LockStatus.UNLOCK.getCode()){
                                            processResult = backLockInfoType;
                                        }else if(status == LockStatus.LOCK_CLOSED.getCode()){
                                            processResult = LcokProcessResultType.ERROR.getCode();
                                            failReason = "未知";
                                        }
                                        toUpdateAccCtrlAndLockStatus(status,lockCode);
                                    }
                                }else{
                                    processResult = backLockInfoType;
                                    failReason = content;
                                }
                            }else{
                                log.error("开锁时返回记录为空");
                                failReason = "开锁时返回记录为空";
                                //TODO  查询调用查询锁状态接口，根据查到状态改变锁和门禁状态，
                                Integer status = getLockStatus(lockRequest);
                                if(status != null ){
                                    if(status == LockStatus.UNLOCK.getCode()){
                                        processResult = LcokProcessResultType.SUCCESS.getCode();
                                    }else if(status == LockStatus.LOCK_CLOSED.getCode()){
                                        processResult = LcokProcessResultType.ERROR.getCode();
                                        failReason = "未知";
                                    }
                                    toUpdateAccCtrlAndLockStatus(status,lockCode);
                                }
                            }
                        }else{
                            log.error("没有找到ip地址为{}的摄像头对应的锁编号",deviceIp);
                            failReason = "没有找到ip地址为" + deviceIp + "的摄像头对应的锁编号";
                        }
                    }else {
                        log.error("没有找到ip地址为{}的摄像头记录",deviceIp);
                        failReason = "没有找到ip地址为" + deviceIp + "的摄像头记录";
                    }

                }
                FileUtil.writeBytes(faceInfo.getFacePanorama(), picturePathFacePanorama);
                FileUtil.writeBytes(faceInfo.getFacePic(), picturePathFacePic);

                AccCtrlProcess accCtrlProcess = getAccCtrlProcess(faceInfo, type, facePanoramaFile, facePicFile, processResult, failReason);

                try {
                    accCtrlProcessService.insertAccCtrlProcess(accCtrlProcess);
                    log.info("门禁操作记录信息入库成功");
                } catch (Exception e) {
                    log.error("门禁操作记录信息入库失败");
                }
                //如果是告警类型需要同时插入告警表
                if ("Alarm".equals(type)) {
                    Alarm alarm = getAlarm(accCtrlProcess);
                    try {
                        alarmService.insertAlarm(alarm);
                        log.info("锁操作告警信息入库成功");
                    } catch (Exception e) {
                        log.error("锁操作告警信息入库失败");
                    }
                }
            }
        }catch (Exception e){
            log.error("摄像头上报信息处理异常",e);
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
            //直接根据锁编号更新门禁状态
            accessControlDao.update(accessControlInfo, new UpdateWrapper<AccessControlInfo>().eq("lock_code", lockCode));
        }
        //状态不合法则不更新
        if(lockStatus != null){
            LockInfo lockInfo = new LockInfo();
            lockInfo.setStatus(lockStatus.getCode());
            lockInfoDao.update(lockInfo, new UpdateWrapper<LockInfo>().eq("lock_code", lockCode));
        }
    }

    /**
     * 查询开锁状态
     * @param lockRequest 请求参数
     * @return 开锁状态
     */
    private Integer getLockStatus(LockRequest lockRequest) {
        RestfulEntityBySummit back = unLockService.toQueryLockStatus(lockRequest);
        Object backData = back.getData();
        if((backData instanceof BackLockInfo)){
            return ((BackLockInfo) backData).getObjx();
        }
        return null;
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
    private AccCtrlProcess getAccCtrlProcess(FaceInfo faceInfo, String type, FileInfo facePanoramaFile, FileInfo facePicFile, String processResult, String failReason) {
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
//            accessControlInfo.setStatus(AccCtrlStatus.ALARM.getCode());
            accCtrlProcess.setProcessType(LockProcessType.LOCK_ALARM.getCode());
            accCtrlProcess.setProcessResult(LcokProcessResultType.ERROR.getCode());
            accCtrlProcess.setFailReason("匹配度过低");

        }else{
            //关锁
//            accessControlInfo.setStatus(AccCtrlStatus.CLOSED.getCode());
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
    private Alarm getAlarm(AccCtrlProcess accCtrlProcess) {
        Alarm alarm = new Alarm();
        alarm.setAccCtrlProId(accCtrlProcess.getAccCtrlProId());
        alarm.setAlarmTime(accCtrlProcess.getProcessTime());
        alarm.setAlarmStatus(AlarmStatus.UNPROCESSED.getCode());
        alarm.setDescription(accCtrlProcess.getFailReason());
        alarm.setUpdatetime(new Date());
        return alarm;
    }

}
