package com.summit.utils;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.io.FileUtil;
import cn.hutool.system.SystemUtil;
import com.summit.MainAction;

import com.summit.common.entity.RestfulEntityBySummit;
import com.summit.constants.CommonConstants;
import com.summit.dao.entity.Alarm;
import com.summit.dao.entity.CameraDevice;
import com.summit.dao.entity.FileInfo;
import com.summit.dao.entity.LockInfo;
import com.summit.dao.entity.LockProcess;
import com.summit.entity.BackLockInfo;
import com.summit.entity.LockRequest;
import com.summit.sdk.huawei.callback.ClientFaceInfoCallback;
import com.summit.sdk.huawei.model.AlarmStatus;
import com.summit.sdk.huawei.model.FaceInfo;
import com.summit.sdk.huawei.model.FaceLibType;
import com.summit.sdk.huawei.model.LcokProcessResultType;
import com.summit.sdk.huawei.model.LockProcessType;
import com.summit.sdk.huawei.model.LockStatus;
import com.summit.service.AlarmService;
import com.summit.service.CameraDeviceService;
import com.summit.service.LockRecordService;
import com.summit.service.impl.NBLockServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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
                            RestfulEntityBySummit result = unLockService.toUnLock(new LockRequest(lockCode, faceInfo.getName()));
                            BackLockInfo backLockInfo = result.getData() == null ? null : (BackLockInfo) result.getData();
                            if(backLockInfo != null){
                                String backLockInfoType = backLockInfo.getType();
                                String content = backLockInfo.getContent();
                                log.debug("rmid={},type={},content={},objx={},time={}",
                                        backLockInfo.getRmid(), backLockInfoType, content, backLockInfo.getObjx(), backLockInfo.getTime());
                                processResult = backLockInfoType;
                                if(!LcokProcessResultType.SUCCESS.getCode().equals(backLockInfoType)){
                                    failReason = content;
                                }
                            }else{
                                log.error("开锁时返回记录为空");
                                failReason = "开锁时返回记录为空";
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

                LockProcess lockProcess = getLockProcess(faceInfo, type, facePanoramaFile, facePicFile, processResult, failReason);
                if (lockRecordService.insertLockProcess(lockProcess) != CommonConstants.UPDATE_ERROR) {
                    log.info("锁操作记录信息入库成功");
                } else {
                    log.error("锁操作记录信息入库失败");
                }
                //如果是告警类型需要同时插入告警表
                if ("Alarm".equals(type)) {
                    Alarm alarm = getAlarm(lockProcess);
                    if (alarmService.insertAlarm(alarm) != CommonConstants.UPDATE_ERROR) {
                        log.info("锁操作告警信息入库成功");
                    } else {
                        log.error("锁操作告警信息入库失败");
                    }
                }
            }
        }catch (Exception e){
            log.error("摄像头上报信息处理异常",e);
        }

    }

    /**
     * 根据锁操作记录信息组装告警信息入库信息对象
     * @param lockProcess 锁操作记录对象
     * @return 锁告警对象
     */
    private Alarm getAlarm(LockProcess lockProcess) {
        Alarm alarm = new Alarm();
        alarm.setProcessId(lockProcess.getProcessId());
        alarm.setAlarmTime(lockProcess.getProcessTime());
        alarm.setAlarmStatus(AlarmStatus.UNPROCESSED.getCode());
        return alarm;
    }

    /**
     * 根据人脸识别结果信息及其他信息组装需要入库的锁操作信息
     * @param faceInfo 识别的人脸信息
     * @param type 人脸识别结果类型
     * @param facePanoramaFile 人脸全景图对象
     * @param facePicFile 人脸识别抠图对象
     * @param processResult 锁操作结果返回码
     * @param failReason 人脸识别或锁操作识别原因
     * @return 组装好的锁操作记录信息对象
     */
    private LockProcess getLockProcess(FaceInfo faceInfo, String type, FileInfo facePanoramaFile, FileInfo facePicFile, String processResult,String failReason) {
        LockProcess lockProcess = new LockProcess();
        String deviceIp = faceInfo.getDeviceIp();
        lockProcess.setDeviceIp(deviceIp);
        lockProcess.setUserName(faceInfo.getName());
        if(faceInfo.getGender() != null)
            lockProcess.setGender(faceInfo.getGender().getGenderCode());
        try {
            if(faceInfo.getBirthday() != null)
                lockProcess.setBirthday(CommonConstants.dateFormat.parse(faceInfo.getBirthday()));
        } catch (ParseException e) {
            log.error("生日格式有误");
        }
        lockProcess.setProvince(faceInfo.getProvince());
        lockProcess.setCity(faceInfo.getCity());
        if(faceInfo.getCardType() != null)
            lockProcess.setCardType(faceInfo.getCardType().getCardTypeCode());
        lockProcess.setCardId(faceInfo.getCardId());
        lockProcess.setFaceMatchRate(faceInfo.getFaceMatchRate());
        lockProcess.setFaceLibName(faceInfo.getFaceLibName());
        if(faceInfo.getFaceLibType() != null)
            lockProcess.setFaceLibType(faceInfo.getFaceLibType().getFaceLibTypeCode());
        lockProcess.setFacePanorama(facePanoramaFile);
        lockProcess.setFacePic(facePicFile);
        DateTime picSnapshotTime = faceInfo.getPicSnapshotTime();
        lockProcess.setProcessTime(picSnapshotTime);
        LockInfo lockInfo = new LockInfo();
        CameraDevice device = cameraDeviceService.selectDeviceByIpAddress(deviceIp);

        if(device != null){
            String lockCode = device.getLockCode();
            lockProcess.setLockCode(lockCode);
            lockInfo.setLockCode(lockCode);

            lockInfo.setUpdatetime(picSnapshotTime == null ? null : picSnapshotTime.toJdkDate());
        }
        if("Unlock".equals(type)){
            lockInfo.setStatus(LockStatus.UNLOCK.getCode());
            lockProcess.setProcessType(LockProcessType.UNLOCK.getCode());
            lockProcess.setProcessResult(processResult);
            //刷脸成功后开锁操作也有可能失败,成功则failReason=null
            lockProcess.setFailReason(failReason);
        }else if("Alarm".equals(type)){
            lockInfo.setStatus(LockStatus.LOCK_ALARM.getCode());
            lockProcess.setProcessType(LockProcessType.LOCK_ALARM.getCode());
            lockProcess.setProcessResult(LcokProcessResultType.ERROR.getCode());
            lockProcess.setFailReason("匹配度过低");

        }else{
            //关锁
            lockProcess.setProcessType(LockProcessType.CLOSE_LOCK.getCode());
            lockProcess.setProcessResult("success");
            lockInfo.setStatus(LockStatus.LOCK_CLOSED.getCode());

        }
        lockProcess.setLockInfo(lockInfo);
        return lockProcess;
    }


}
