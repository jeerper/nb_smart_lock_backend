package com.summit.utils;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.io.FileUtil;
import cn.hutool.system.SystemUtil;
import com.summit.MainAction;

import com.summit.dao.entity.Alarm;
import com.summit.dao.entity.CameraDevice;
import com.summit.dao.entity.FileInfo;
import com.summit.dao.entity.LockInfo;
import com.summit.dao.entity.LockProcess;
import com.summit.sdk.huawei.callback.ClientFaceInfoCallback;
import com.summit.sdk.huawei.model.FaceInfo;
import com.summit.sdk.huawei.model.FaceLibType;
import com.summit.service.AlarmService;
import com.summit.service.CameraDeviceService;
import com.summit.service.LockRecordService;
import com.summit.service.impl.NBLockServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.text.SimpleDateFormat;

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

    private  SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");

    @Override
    public void invoke(Object object) {
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

            String snapshotTime = faceInfo.getPicSnapshotTime().toString("yyyy-MM-dd_HH.mm.ss");
            String picturePathFacePanorama = new StringBuilder()
                    .append(SystemUtil.getUserInfo().getCurrentDir())
                    .append(File.separator)
                    .append(MainAction.SnapshotFileName)
                    .append(File.separator)
                    .append(faceInfo.getDeviceIp())
                    .append(File.separator)
                    .append(faceInfo.getPicSnapshotTime().toString("yyyy-MM-dd"))
                    .append(File.separator)
                    .append(type)
                    .append(File.separator)
                    .append(snapshotTime)
                    .append("_FacePanorama.jpg")
                    .toString();

            String picturePathFacePic = new StringBuilder()
                    .append(SystemUtil.getUserInfo().getCurrentDir())
                    .append(File.separator)
                    .append(MainAction.SnapshotFileName)
                    .append(File.separator)
                    .append(faceInfo.getDeviceIp())
                    .append(File.separator)
                    .append(faceInfo.getPicSnapshotTime().toString("yyyy-MM-dd"))
                    .append(File.separator)
                    .append(type)
                    .append(File.separator)
                    .append(snapshotTime)
                    .append("_FacePic.jpg")
                    .toString();

            FileInfo facePanoramaFile = new FileInfo(snapshotTime + "_FacePanorama.jpg", picturePathFacePanorama, "人脸全景图");
            FileInfo facePicFile = new FileInfo(snapshotTime + "_FacePic.jpg", picturePathFacePic, "人脸识别抠图");

            LockProcess lockProcess = getLockProcess(faceInfo,type,facePanoramaFile,facePicFile);
            if(lockRecordService.insertLockProcess(lockProcess) != -1){
                log.info("锁操作记录信息入库成功");
            }else{
                log.error("锁操作记录信息入库失败");
            }
            //如果是告警类型需要同时插入告警表
            if("Alarm".equals(type)){
                Alarm alarm = getAlarm(lockProcess);
                if(alarmService.insertAlarm(alarm) != -1){
                    log.info("锁操作告警信息入库成功");
                }else{
                    log.error("锁操作告警信息入库失败");
                }
            }

            FileUtil.writeBytes(faceInfo.getFacePanorama(), picturePathFacePanorama);
            FileUtil.writeBytes(faceInfo.getFacePic(), picturePathFacePic);


//            log.debug("名字:" + faceInfo.getName());
//            log.debug("性别:{}", faceInfo.getGender().getGenderDescription());
//            log.debug("生日:" + faceInfo.getBirthday());
//            log.debug("省级:" + faceInfo.getProvince());
//            log.debug("地市:" + faceInfo.getCity());
//            log.debug("证件类型:{}", faceInfo.getCardType().getCardTypeDescription());
//            log.debug("证件号:" + faceInfo.getCardId());
//            log.debug("人脸匹配率:{}%", faceInfo.getFaceMatchRate());
//            log.debug("名单库名称:{}", faceInfo.getFaceLibName());
//            log.debug("名单库类型:{}", faceInfo.getFaceLibType().getFaceLibTypeDescription());
//            FaceLibType faceLibType = faceInfo.getFaceLibType();
//            if(faceLibType.equals(FaceLibType.FACE_LIB_WHITE)){
//                RestfulEntityBySummit result = unLockService.toUnLock(new LockRequest("NB100002", "张三"));
//                LockInfo lockInfo = result.getData() == null ? null : (LockInfo)result.getData();
//                log.info("rmid={},type={},content={},objx={},time={}" ,
//                        lockInfo.getRmid(),lockInfo.getType(),lockInfo.getContent(),lockInfo.getObjx(),lockInfo.getTime());
//            }

        }


    }

    private Alarm getAlarm(LockProcess lockProcess) {
        Alarm alarm = new Alarm();
        alarm.setProcessId(lockProcess.getProcessId());
        alarm.setAlarmTime(lockProcess.getProcessTime());
        alarm.setAlarmStatus(1);
        return alarm;
    }

    private LockProcess getLockProcess(FaceInfo faceInfo, String type, FileInfo facePanoramaFile, FileInfo facePicFile) {
        LockProcess lockProcess = new LockProcess();
        String deviceIp = faceInfo.getDeviceIp();
        lockProcess.setDeviceIp(deviceIp);
        lockProcess.setUserName(faceInfo.getName());
        lockProcess.setFacePanoramaId(facePanoramaFile.getFileId());
        lockProcess.setFacePicId(facePicFile.getFileId());
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
            lockInfo.setStatus(1);
            lockProcess.setProcessType(1);
            lockProcess.setProcessResult("success");

        }else if("Alarm".equals(type)){
            lockInfo.setStatus(3);
            lockProcess.setProcessType(3);
            lockProcess.setProcessResult("fail");
            lockProcess.setFailReason("匹配度过低");

        }else{
            //关锁
            lockProcess.setProcessType(2);
            lockProcess.setProcessResult("success");
            lockInfo.setStatus(2);

        }

        return lockProcess;
    }


}
