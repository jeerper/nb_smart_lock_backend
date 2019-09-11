package com.summit.utils;

import cn.hutool.core.io.FileUtil;
import cn.hutool.system.SystemUtil;
import com.summit.MainAction;
import com.summit.common.entity.RestfulEntityBySummit;
import com.summit.common.util.ApplicationContextUtil;
import com.summit.constants.CommonConstants;
import com.summit.dao.entity.AccCtrlProcess;
import com.summit.dao.entity.AccCtrlRealTimeEntity;
import com.summit.dao.entity.AccessControlInfo;
import com.summit.dao.entity.Alarm;
import com.summit.dao.entity.CameraDevice;
import com.summit.dao.entity.FileInfo;
import com.summit.entity.BackLockInfo;
import com.summit.entity.LockRequest;
import com.summit.exception.ErrorMsgException;
import com.summit.sdk.huawei.callback.ClientFaceInfoCallback;
import com.summit.sdk.huawei.model.CameraUploadType;
import com.summit.sdk.huawei.model.FaceInfo;
import com.summit.sdk.huawei.model.FaceLibType;
import com.summit.sdk.huawei.model.LockProcessResultType;
import com.summit.service.AccCtrlProcessService;
import com.summit.service.AccCtrlRealTimeService;
import com.summit.service.AccessControlService;
import com.summit.service.AlarmService;
import com.summit.service.CameraDeviceService;
import com.summit.service.FaceInfoAccCtrlService;
import com.summit.service.impl.NBLockServiceImpl;
import com.summit.util.AccCtrlProcessUtil;
import com.summit.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.Date;
import java.util.List;

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
    @Autowired
    private AccCtrlRealTimeService accCtrlRealTimeService;
    @Autowired
    private AccessControlService accessControlService;
    @Autowired
    private FaceInfoAccCtrlService faceInfoAccCtrlService;

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
                CameraUploadType type;
                if (faceInfo.getFaceLibType() == FaceLibType.FACE_LIB_ALARM) {
                    log.debug("开始报警!!!!！！！！");
                    type = CameraUploadType.Alarm;
                } else {
                    type = CameraUploadType.Unlock;
                }
                String deviceIp = faceInfo.getDeviceIp();
                Date date = new Date();
                String snapshotTime = CommonUtil.snapshotTimeFormat.get().format(date);
                String snapshotDate = CommonUtil.dateFormat.get().format(date);
                String picturePathFacePanorama = new StringBuilder()
                        .append(SystemUtil.getUserInfo().getCurrentDir())
                        .append(File.separator)
                        .append(MainAction.SnapshotFileName)
                        .append(File.separator)
                        .append(deviceIp)
                        .append(File.separator)
                        .append(snapshotTime)
                        .append(File.separator)
                        .append(type.getCode())
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
                        .append(type.getCode())
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
                        .append(type.getCode())
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
                        .append(type.getCode())
                        .append(CommonConstants.URL_SEPARATOR)
                        .append(snapshotDate)
                        .append(CommonConstants.FACE_PIC_SUFFIX)
                        .toString();

                FileInfo facePanoramaFile = new FileInfo(snapshotTime + CommonConstants.FACE_PANORAMA_SUFFIX, facePanoramaUrl, "人脸全景图");
                FileInfo facePicFile = new FileInfo(snapshotTime + CommonConstants.FACE_PIC_SUFFIX, facePicUrl, "人脸识别抠图");

                FaceLibType faceLibType = faceInfo.getFaceLibType();
                //开锁处理UUID
                String unlockProcessUuid=null;
                //处理结果
                LockProcessResultType processResult = null;
                //失败原因
                String failReason = null;
                if (faceLibType.equals(FaceLibType.FACE_LIB_WHITE)) {
                    //通过对应的摄像头IP找到对应的锁信息
                    CameraDevice cameraDevice = cameraDeviceService.selectDeviceByIpAddress(deviceIp);
                    if(cameraDevice == null){
                        log.debug("没有找到ip地址为{}的摄像头记录", deviceIp);
                        return;
                    }
                    String lockCode = cameraDevice.getLockCode();
                    if(lockCode==null){
                        log.debug("没有找到ip地址为{}的摄像头对应的锁编号", deviceIp);
                        return;
                    }
                    //根据锁编码查询门禁信息
                    AccessControlInfo accessControlInfo = accessControlService.selectAccCtrlByLockCode(lockCode);
                    if (accessControlInfo == null) {
                        log.debug("未找到锁编号为{}对应的门禁信息", lockCode);
                        return;
                    }
                    //根据人脸名称和门禁ID查询是否有该门禁的人脸授权信息
                    int count = faceInfoAccCtrlService.selectAccCtrlIdByUserNameAndAccessControlId(faceInfo.getName(),
                            accessControlInfo.getAccessControlId());
                    //此人脸有对应门禁权限，则进行开锁，否则更新门禁为告警状态，并记录开锁结果和原因
                    if (count > 0) {
                        LockRequest lockRequest = new LockRequest(null, lockCode, faceInfo.getName(), null,null);
                        RestfulEntityBySummit result = null;
                        //调用开锁接口
                        result = unLockService.toUnLock(lockRequest);

                        BackLockInfo backLockInfo = result.getData() == null ? null : (BackLockInfo) result.getData();
                        if (backLockInfo != null) {
                            //开锁接口返回的结果
                            String backLockInfoType = backLockInfo.getType();
                            String content = backLockInfo.getContent();
                            log.debug("rmid={},type={},content={},objx={},time={}",
                                    backLockInfo.getRmid(), backLockInfoType, content, backLockInfo.getObjx(),
                                    backLockInfo.getTime());

                            if ("error".equals(backLockInfoType)) {
                                failReason = content;
                            }
                            //下发开锁指令后返回的状态码
                            processResult= LockProcessResultType.codeOf(backLockInfo.getObjx());
                            //用于查询开锁状态的命令UUID
                            unlockProcessUuid=backLockInfo.getRmid();

                        } else {
                            //开锁接口调用失败
                            log.debug("开锁接口调用失败");
                            failReason = "开锁接口调用失败";
                            processResult = LockProcessResultType.Failure;
                        }
                    } else {
                        log.debug("用户{}没有打开门禁{}的权限", faceInfo.getName(), accessControlInfo.getAccessControlId());
                        processResult =LockProcessResultType.Failure;
                        failReason = "用户" + faceInfo.getName() + "没有打开门禁" + accessControlInfo.getAccessControlId() + "的权限";
                        //todo:考虑问题,是否要重复更新:门禁,实时,锁信息表
//                        accCtrlProcessUtil.toUpdateAccCtrlAndLockStatus(AccCtrlStatus.ALARM.getCode(), lockCode);
                        type = CameraUploadType.Alarm;
                    }
                }

                FileUtil.writeBytes(faceInfo.getFacePanorama(), picturePathFacePanorama);
                FileUtil.writeBytes(faceInfo.getFacePic(), picturePathFacePic);

                AccCtrlProcess accCtrlProcess = accCtrlProcessUtil.getAccCtrlProcess(faceInfo, type, facePanoramaFile, facePicFile,unlockProcessUuid, processResult, failReason);

                //在事务控制下插入门禁操作记录、门禁实时信息、告警
                ApplicationContextUtil.getBean(ClientFaceInfoCallbackImpl.class).insertData(type, accCtrlProcess);
            }
        } catch (Exception e) {
            log.error("摄像头上报信息处理异常", e);
        }

    }

    /**
     * 判断刷脸人有无开锁权限
     *
     * @param accessControlIds 人脸关联的所以门禁的id
     * @param accessControlId  本次刷脸设备对应的门禁id
     * @return 是否具有开锁权限
     */
    private boolean haveAccCtrlAuth(List<String> accessControlIds, String accessControlId) {
        if (CommonUtil.isEmptyList(accessControlIds)) {
            return false;
        }
        return accessControlIds.contains(accessControlId);
    }

    //加入事务控制
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public void insertData(CameraUploadType type, AccCtrlProcess accCtrlProcess) {

        try {
            //插入门禁操作记录并更新门禁信息表和锁信息表(门禁信息表和锁信息表中都含有门禁状态)
            accCtrlProcessService.insertAccCtrlProcess(accCtrlProcess);
            log.info("门禁操作记录信息入库成功");
        } catch (Exception e) {
            log.error("门禁操作记录信息入库失败");
            throw new ErrorMsgException("门禁操作记录信息入库失败");
        }

        //如果是告警类型需要同时插入告警表
        Alarm alarm = null;
        if (CameraUploadType.Alarm==type) {
            alarm = accCtrlProcessUtil.getAlarm(accCtrlProcess);
            try {
                alarmService.insertAlarm(alarm);
                log.info("门禁操作告警信息入库成功");
            } catch (Exception e) {
                log.error("门禁操作告警信息入库失败");
                throw new ErrorMsgException("门禁操作告警信息入库失败");
            }
        }
        //获取实时状态信息
        AccCtrlRealTimeEntity accCtrlRealTimeEntity = accCtrlProcessUtil.getAccCtrlRealTimeEntity(accCtrlProcess);
        try {
            if (alarm != null) {
                accCtrlRealTimeEntity.setAlarmId(alarm.getAlarmId());
            }
            accCtrlRealTimeService.insertOrUpdate(accCtrlRealTimeEntity);
            log.info("门禁实时信息数据库操作成功");
        } catch (Exception e) {
            log.error("门禁实时信息数据库操作失败");
            throw new ErrorMsgException("门禁实时信息数据库操作失败");
        }
    }


}
