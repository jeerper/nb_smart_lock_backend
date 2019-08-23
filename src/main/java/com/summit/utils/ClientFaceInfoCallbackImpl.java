package com.summit.utils;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.io.FileUtil;
import cn.hutool.system.SystemUtil;
import com.summit.MainAction;

import com.summit.common.entity.RestfulEntityBySummit;
import com.summit.constants.CommonConstants;
import com.summit.dao.entity.AccCtrlProcess;
import com.summit.dao.entity.AccessControlInfo;
import com.summit.dao.entity.Alarm;
import com.summit.dao.entity.CameraDevice;
import com.summit.dao.entity.FileInfo;
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
import com.summit.util.AccCtrlProcessUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.text.ParseException;
import java.util.Date;

@Slf4j
@Component
public class ClientFaceInfoCallbackImpl implements ClientFaceInfoCallback {

    @Autowired
    private NBLockServiceImpl unLockService;
    @Autowired
    private AlarmService alarmService;
    @Autowired
    private CameraDeviceService cameraDeviceService;
    @Autowired
    private AccCtrlProcessService accCtrlProcessService;
    @Autowired
    private AccCtrlProcessUtil accCtrlProcessUtil;

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
                                    Integer status = accCtrlProcessUtil.getLockStatus(lockRequest);
                                    if(status != null){
                                        if(status == LockStatus.UNLOCK.getCode()){
                                            processResult = backLockInfoType;
                                        }else if(status == LockStatus.LOCK_CLOSED.getCode()){
                                            processResult = LcokProcessResultType.ERROR.getCode();
                                            failReason = "未知";
                                        }
                                        accCtrlProcessUtil.toUpdateAccCtrlAndLockStatus(status,lockCode);
                                    }
                                }else{
                                    processResult = backLockInfoType;
                                    failReason = content;
                                }
                            }else{
                                log.error("开锁时返回记录为空");
                                failReason = "开锁时返回记录为空";
                                //TODO  查询调用查询锁状态接口，根据查到状态改变锁和门禁状态，
                                Integer status = accCtrlProcessUtil.getLockStatus(lockRequest);
                                if(status != null ){
                                    if(status == LockStatus.UNLOCK.getCode()){
                                        processResult = LcokProcessResultType.SUCCESS.getCode();
                                    }else if(status == LockStatus.LOCK_CLOSED.getCode()){
                                        processResult = LcokProcessResultType.ERROR.getCode();
                                        failReason = "未知";
                                    }
                                    accCtrlProcessUtil.toUpdateAccCtrlAndLockStatus(status,lockCode);
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

                AccCtrlProcess accCtrlProcess = accCtrlProcessUtil.getAccCtrlProcess(faceInfo, type, facePanoramaFile, facePicFile, processResult, failReason);

                try {
                    accCtrlProcessService.insertAccCtrlProcess(accCtrlProcess);
                    log.info("门禁操作记录信息入库成功");
                } catch (Exception e) {
                    log.error("门禁操作记录信息入库失败");
                }
                //如果是告警类型需要同时插入告警表
                if ("Alarm".equals(type)) {
                    Alarm alarm = accCtrlProcessUtil.getAlarm(accCtrlProcess);
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


}
