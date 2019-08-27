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
import com.summit.entity.LockRequest;
import com.summit.sdk.huawei.model.CardType;
import com.summit.sdk.huawei.model.FaceInfo;
import com.summit.sdk.huawei.model.FaceLibType;
import com.summit.sdk.huawei.model.Gender;
import com.summit.service.AlarmService;
import com.summit.service.CameraDeviceService;
import com.summit.service.LockRecordService;
import com.summit.util.AccCtrlProcessUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class test {
    @Autowired
    private LockRecordService lockRecordService;
    @Autowired
    private AlarmService alarmService;
    @Autowired
    private CameraDeviceService cameraDeviceService;
    @Autowired
    private AccCtrlProcessUtil accCtrlProcessUtil;

    @Test
    public  void testn() throws IOException {
        LockRequest lockRequest = new LockRequest();
        lockRequest.setTerminalNum("NB100002");
        accCtrlProcessUtil.getLockStatus(lockRequest);
        System.out.println("success");
    }

    @Test
    public  void test() throws IOException {

        FaceInfo faceInfo = new FaceInfo();
        faceInfo.setName("王五");
        faceInfo.setGender(Gender.PU_MALE);
        faceInfo.setBirthday("1994-01-01");
        faceInfo.setProvince("陕西");
        faceInfo.setCity("西安");
        faceInfo.setCardType(CardType.IDENTITY);
        faceInfo.setCardId("555");
        faceInfo.setFaceLibType(FaceLibType.FACE_LIB_ALARM);
        faceInfo.setFaceLibName(FaceLibType.FACE_LIB_ALARM.getFaceLibTypeDescription());
        faceInfo.setFaceMatchRate(0.8f);
        String deviceIp = "127.0.0.1";
        faceInfo.setDeviceIp(deviceIp);
        DateTime shotTime = new DateTime(new Date().getTime());
        faceInfo.setPicSnapshotTime(shotTime);

        int len = 1024*1024 *10;
        byte[] facePanorama = new byte[len];
        String pathPanorama = getClass().getClassLoader().getResource("img/test01.jpg").getPath();
        FileInputStream fisPanorama = new FileInputStream(pathPanorama);
        fisPanorama.read(facePanorama);
        faceInfo.setFacePanorama(facePanorama);

        byte[] facePic = new byte[len];
        String pathPic = getClass().getClassLoader().getResource("img/test02.jpg").getPath();
        FileInputStream fisPic = new FileInputStream(pathPic);
        fisPic.read(facePanorama);
        faceInfo.setFacePic(facePic);



        String type;
        if (faceInfo.getFaceLibType() == FaceLibType.FACE_LIB_ALARM) {
            log.debug("开始报警!!!!！！！！");
            type = "Alarm";
        } else {
            type = "Unlock";
        }

        String snapshotTime1 = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss").format(new Date());
        String snapshotTime = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String picturePathFacePanorama = new StringBuilder()
                .append(SystemUtil.getUserInfo().getCurrentDir())
                .append(File.separator)
                .append(MainAction.SnapshotFileName)
                .append(File.separator)
                .append(deviceIp)
                .append(File.separator)
                .append(snapshotTime1)
                .append(File.separator)
                .append("Alarm")
                .append(File.separator)
                .append(snapshotTime)
                .append("_FacePanorama.jpg")
                .toString();

        String picturePathFacePic = new StringBuilder()
                .append(SystemUtil.getUserInfo().getCurrentDir())
                .append(File.separator)
                .append(MainAction.SnapshotFileName)
                .append(File.separator)
                .append(deviceIp)
                .append(File.separator)
                .append(snapshotTime1)
                .append(File.separator)
                .append("Alarm")
                .append(File.separator)
                .append(snapshotTime)
                .append("_FacePic.jpg")
                .toString();


        FileInfo facePanoramaFile = new FileInfo(snapshotTime + "_FacePanorama.jpg", picturePathFacePanorama, "人脸全景图");
        FileInfo facePicFile = new FileInfo(snapshotTime + "_FacePic.jpg", picturePathFacePic, "人脸识别抠图");

        FileUtil.writeBytes(faceInfo.getFacePanorama(), picturePathFacePanorama);
        FileUtil.writeBytes(faceInfo.getFacePic(), picturePathFacePic);

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
//                log.info("锁操作告警信息入库成功");
            }else{
                log.error("锁操作告警信息入库失败");
            }
        }


    }

    private Alarm getAlarm(LockProcess lockProcess) {
        Alarm alarm = new Alarm();
        alarm.setAccCtrlProId(lockProcess.getProcessId());
        alarm.setAlarmTime(lockProcess.getProcessTime());
        alarm.setAlarmStatus(1);
        return alarm;
    }

    private LockProcess getLockProcess(FaceInfo faceInfo, String type, FileInfo facePanoramaFile, FileInfo facePicFile) {
        LockProcess lockProcess = new LockProcess();
        String deviceIp = faceInfo.getDeviceIp();
        lockProcess.setDeviceIp(deviceIp);
        lockProcess.setUserName(faceInfo.getName());
//        lockProcess.setFacePanoramaId(facePanoramaFile.getFileId());
//        lockProcess.setFacePicId(facePicFile.getFileId());
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
        lockProcess.setLockInfo(lockInfo);
        return lockProcess;
    }
}
