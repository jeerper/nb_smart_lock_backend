package com.summit.utils;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.system.SystemUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.summit.MainAction;
import com.summit.constants.CommonConstants;
import com.summit.dao.entity.AccCtrlProcess;
import com.summit.dao.entity.AccCtrlRealTimeEntity;
import com.summit.dao.entity.AccessControlInfo;
import com.summit.dao.entity.Alarm;
import com.summit.dao.entity.CameraDevice;
import com.summit.dao.entity.FileInfo;
import com.summit.dao.repository.AccCtrlProcessDao;
import com.summit.dao.repository.UnlockCommandQueueDao;
import com.summit.sdk.huawei.callback.ClientFaceInfoCallback;
import com.summit.sdk.huawei.model.CameraUploadType;
import com.summit.sdk.huawei.model.FaceInfo;
import com.summit.sdk.huawei.model.FaceLibType;
import com.summit.sdk.huawei.model.LockProcessResultType;
import com.summit.sdk.huawei.model.LockProcessType;
import com.summit.service.*;
import com.summit.service.impl.NBLockServiceImpl;
import com.summit.util.AccCtrlProcessUtil;
import com.summit.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
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
    @Autowired
    private UnlockCommandQueueDao unlockCommandQueueDao;
    @Autowired
    private AccCtrlProcessDao accCtrlProcessDao;
    @Autowired
    AddAccCtrlprocessService addAccCtrlprocessService;
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
                    type = CameraUploadType.Illegal_Alarm;
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

                //处理结果
                LockProcessResultType processResult = null;
                //失败原因
                String failReason = null;
                if (faceLibType.equals(FaceLibType.FACE_LIB_WHITE)) {
                    //通过对应的摄像头IP找到对应的锁信息
                    CameraDevice cameraDevice = cameraDeviceService.selectDeviceByIpAddress(deviceIp);
                    if (cameraDevice == null) {
                        log.debug("没有找到ip地址为{}的摄像头记录", deviceIp);
                        return;
                    }
                    String lockCode = cameraDevice.getLockCode();
                    if (lockCode == null) {
                        log.debug("没有找到ip地址为{}的摄像头对应的锁编号", deviceIp);
                        return;
                    }
                    //根据锁编码查询门禁信息
                    AccessControlInfo accessControlInfo = accessControlService.selectAccCtrlByLockCode(lockCode);
                    if (accessControlInfo == null) {
                        log.debug("未找到锁编号为{}对应的门禁信息", lockCode);
                        return;
                    }
                    IPage<AccCtrlProcess> accCtrlProcessPage = accCtrlProcessDao.selectPage(new Page<>(1, 1), Wrappers.<AccCtrlProcess>lambdaQuery()
                            .eq(AccCtrlProcess::getAccessControlId, accessControlInfo.getAccessControlId())
                            .eq(AccCtrlProcess::getUserName, faceInfo.getName())
                            .eq(AccCtrlProcess::getProcessType, LockProcessType.UNLOCK.getCode())
                            .in(AccCtrlProcess::getProcessResult,
                                    LockProcessResultType.Success.getCode(),
                                    LockProcessResultType.NotResponse.getCode(),
                                    LockProcessResultType.WaitSendCommand.getCode(),
                                    LockProcessResultType.CommandSuccess.getCode()
                            )
                            .orderByDesc(AccCtrlProcess::getProcessTime)
                    );
                    List<AccCtrlProcess> accCtrlProcessList = accCtrlProcessPage.getRecords();
                    if (accCtrlProcessList.size() != 0) {
                        AccCtrlProcess accCtrlProcess = accCtrlProcessList.get(0);
                        long differMinute = DateUtil.between(accCtrlProcess.getProcessTime(), new Date(), DateUnit.MINUTE);
                        if (differMinute <= 2) {
                            log.debug("同一门禁同一人脸不能在2分钟内开锁多次");
                            return;
                        }
                    }
                    //根据人脸名称和门禁ID查询是否有该门禁的人脸授权信息
                    int count = faceInfoAccCtrlService.selectAccCtrlIdByUserNameAndAccessControlId(faceInfo.getName(),
                            accessControlInfo.getAccessControlId());
                    //此人脸有对应门禁权限，则进行开锁，否则更新门禁为告警状态，并记录开锁结果和原因
                    if (count > 0) {
                        //设置开锁状态为待发送开锁指令
                        processResult = LockProcessResultType.WaitSendCommand;
                    } else {
                        log.debug("用户{}没有打开门禁{}的权限", faceInfo.getName(), accessControlInfo.getAccessControlId());
                        processResult = LockProcessResultType.Failure;
                        failReason = "用户" + faceInfo.getName() + "没有打开门禁" + accessControlInfo.getAccessControlName() + "的权限";
                        type = CameraUploadType.Illegal_Alarm;
                    }
                }

                FileUtil.writeBytes(faceInfo.getFacePanorama(), picturePathFacePanorama);
                FileUtil.writeBytes(faceInfo.getFacePic(), picturePathFacePic);

//                AccCtrlProcess accCtrlProcess = accCtrlProcessUtil.getAccCtrlProcess(faceInfo, type, facePanoramaFile, facePicFile, processResult,
//                        failReason);
//
//                //在事务控制下插入门禁操作记录、门禁实时信息、告警
//                ApplicationContextUtil.getBean(ClientFaceInfoCallbackImpl.class).insertData(type, accCtrlProcess);
            }
        } catch (Exception e) {
            log.error("摄像头上报信息处理异常", e);
        }

    }

    @Transactional(rollbackFor = {Exception.class})
    public void insertData(CameraUploadType type, AccCtrlProcess accCtrlProcess) throws Exception {
        try {
            log.info("门禁操作记录信息入库");
            //插入门禁操作记录并更新门禁信息表和锁信息表(门禁信息表和锁信息表中都含有门禁状态)
            accCtrlProcessService.insertAccCtrlProcess(type,accCtrlProcess);
            //获取实时状态信息
            AccCtrlRealTimeEntity accCtrlRealTimeEntity = accCtrlProcessUtil.getAccCtrlRealTimeEntity(type,accCtrlProcess);
            //如果是告警类型需要同时插入告警表
            if (CameraUploadType.Illegal_Alarm == type) {
                log.info("门禁操作告警信息入库");
                Alarm alarm = accCtrlProcessUtil.getAlarm(accCtrlProcess);
                alarmService.insertAlarm(alarm);
                accCtrlRealTimeEntity.setAlarmId(alarm.getAlarmId());
            }
            log.info("门禁实时信息入库");
            accCtrlRealTimeService.insertOrUpdate(accCtrlRealTimeEntity);
            if (CameraUploadType.Unlock==type){
                log.info("统计分析统计进出频次");
                addAccCtrlprocessService.insertOrUpdateEnterOrExitCount(accCtrlProcess.getAccessControlId());
                log.info("统计分析统计开关锁次数");
                addAccCtrlprocessService.insertOrUpdateLockOrUnlockCount(accCtrlProcess.getAccessControlId());
            }


            //TODO:(废弃)插入开锁指令队列
//            if (type == CameraUploadType.Unlock) {
//                UnlockCommandQueue unlockCommandQueue = new UnlockCommandQueue();
//                unlockCommandQueue.setAccCtrlProId(accCtrlProcess.getAccCtrlProId());
//                unlockCommandQueue.setUnlockFaceName(accCtrlProcess.getUserName());
//                unlockCommandQueue.setLockCode(accCtrlProcess.getLockCode());
//                unlockCommandQueue.setCreateTime(new Date());
//                unlockCommandQueueDao.insert(unlockCommandQueue);
//            }
        } catch (Exception e) {
            log.error("门禁操作信息入库失败", e);
            throw new Exception("门禁操作信息入库失败");
        }
    }


}
