package com.summit.util;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.summit.common.Common;
import com.summit.common.entity.RestfulEntityBySummit;
import com.summit.common.entity.UserInfo;
import com.summit.common.web.filter.UserContextHolder;
import com.summit.dao.entity.FaceInfo;
import com.summit.dao.entity.*;
import com.summit.dao.repository.*;
import com.summit.entity.BackLockInfo;
import com.summit.entity.LockRequest;
import com.summit.sdk.huawei.model.*;
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
    @Autowired
    private AlarmDao alarmDao;
    @Autowired
    private AccCtrlRealTimeDao accCtrlRealTimeDao;
    @Autowired
    private FaceInfoManagerDao faceInfoManagerDao;


    /**
     * 查询开锁状态
     *
     * @param lockRequest 请求参数
     * @return 开锁状态
     */
    public BackLockInfo getLockStatus(LockRequest lockRequest) {
        RestfulEntityBySummit back = null;
        try {
            back = nbLockServiceImpl.toQueryLockStatus(lockRequest);
        } catch (Exception e) {
            log.error("查询开锁状态失败", e);
            return null;
        }
        Object backData = back.getData();
        if (backData == null) {
            return null;
        }
        if ((backData instanceof BackLockInfo)) {
            return (BackLockInfo) backData;
        }
        return null;
    }

    /**
     * 根据锁id获取锁编号
     *
     * @return 锁编号
     */
    public String getLockCodeById(String lockId) {
        if (CommonUtil.isEmptyStr(lockId))
            return null;
        LockInfo lockInfo = lockInfoDao.selectById(lockId);
        String lockCode = null;
        if (lockInfo != null) {
            lockCode = lockInfo.getLockCode();
        }
        return lockCode;
    }

    /**
     * 根据锁编号获取锁id
     *
     * @return 锁id
     */
    public String getLockIdByCode(String lockCode) {
        if (CommonUtil.isEmptyStr(lockCode))
            return null;
        List<LockInfo> lockInfos = lockInfoDao.selectList(new QueryWrapper<LockInfo>().eq("lock_code", lockCode));
        if (CommonUtil.isEmptyList(lockInfos))
            return null;
        LockInfo lockInfo = lockInfos.get(0);
        String lockId = null;
        if (lockInfo != null) {
            lockId = lockInfo.getLockId();
        }
        return lockId;
    }

    /**
     * 查询调用查询锁状态接口，插入一条锁操作记录，根据返回结果改变锁、门禁状态
     *
     * @param backData    开锁时返回结果
     * @param lockRequest 请求参数
     */
    public void toInsertAndUpdateData(Object backData, LockRequest lockRequest) {
        if (lockRequest == null) {
            return;
        }
        String lockId = lockRequest.getLockId();
        String lockCode = lockRequest.getTerminalNum();
        if (lockId == null && lockCode == null) {
            return;
        }
        if (lockCode == null) {
            lockCode = getLockCodeById(lockId);
        }
        if (lockId == null) {
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
        if (userInfo != null) {
            accCtrlProcess.setUserName(userInfo.getName());
            String sex = userInfo.getSex();
            if (sex != null) {
                Integer gender = null;
                try {
                    gender = Integer.parseInt(sex);
                } catch (NumberFormatException e) {
                    log.error("sex {} is illegal", sex);
                }
                accCtrlProcess.setGender(gender);
            }
        }
        accCtrlProcess.setProcessMethod(LockProcessMethod.INTERFACE_BY.getCode());
        //设置对应门禁信息

        String accCtrlProId = lockRequest.getAccCtrlProId();
        AccCtrlProcess accCtrlPro = null;
        if (accCtrlProId != null) {
            accCtrlPro = accCtrlProcessDao.selectById(accCtrlProId);
        }
        if (accCtrlPro != null) {

            accCtrlProcess.setAccessControlName(accCtrlPro.getAccessControlName());
            accCtrlProcess.setAccessControlId(accCtrlPro.getAccessControlId());
            accCtrlProcess.setGender(accCtrlPro.getGender());
            //设置设备信息
            accCtrlProcess.setDeviceId(accCtrlPro.getDeviceId());
            accCtrlProcess.setDeviceIp(accCtrlPro.getDeviceIp());
            accCtrlProcess.setDeviceType(accCtrlPro.getDeviceType());
        } else {
            AccessControlInfo acInfo = accessControlService.selectAccCtrlByLockCode(lockCode);
            if (acInfo != null) {
                String accessControlId = acInfo.getAccessControlId();
                accCtrlProcess.setAccessControlId(accessControlId);
                String accessControlName = acInfo.getAccessControlName();
                accCtrlProcess.setAccessControlName(accessControlName);

            }
        }

        //设置操作结果及失败原因
        BackLockInfo backLockInfo = null;
        if (backData == null) {
            accCtrlProcess.setProcessResult(LockProcessResultType.Failure.getCode());
            accCtrlProcess.setFailReason("未知");
        } else {
            if ((backData instanceof BackLockInfo)) {
                backLockInfo = (BackLockInfo) backData;
                String type = backLockInfo.getType();

                String content = backLockInfo.getContent();
                Integer objx = backLockInfo.getObjx();

                accCtrlProcess.setProcessResult(objx);

                if (LockProcessResultType.CommandSuccess.getCode() != objx) {
                    if (CommonUtil.isEmptyStr(content)) {
                        accCtrlProcess.setFailReason("未知");
                    } else {
                        accCtrlProcess.setFailReason(content);
                    }
                }
                //改为全局扫描更新
                //若状态是开锁，或开锁失败且状态为4终端不在线，则更改锁、门禁、实时状态
//                if(objx != null && (objx == LockStatus.UNLOCK.getCode()
//                        || objx == LockStatus.NOT_ONLINE.getCode() || "终端不在线".equals(content))){
//                    //改变锁、门禁状态为当前状态
//                    toUpdateAccCtrlAndLockStatus(objx,lockCode);
//                }

            } else {
                accCtrlProcess.setProcessResult(LockProcessResultType.Exception.getCode());
                accCtrlProcess.setFailReason("未知");
            }
        }
        try {
            accCtrlProcessService.insertAccCtrlProcess(CameraUploadType.Unlock, accCtrlProcess);
            log.info("门禁操作记录入库成功");
        } catch (Exception e) {
            log.error("门禁操作记录入库失败");
        }

    }


    /**
     * 根据人脸识别结果信息及其他信息组装需要入库的门禁操作记录信息
     *
     * @param lockCode         锁编码
     * @param faceInfo         识别的人脸信息
     * @param type             人脸识别结果类型
     * @param facePanoramaFile 人脸全景图对象
     * @param snapshotTime      人脸扫描时间
     * @param processResult    门禁操作结果返回码
     * @param failReason       人脸识别或门禁操作识别原因
     * @return 组装好的门禁操作记录信息对象
     */
    public AccCtrlProcess getAccCtrlProcess(String lockCode,FaceInfo faceInfo,Float score, CameraUploadType type, FileInfo facePanoramaFile, Date snapshotTime,
                                            LockProcessResultType processResult, String failReason,Integer enterOrExit) {

        AccCtrlProcess accCtrlProcess = new AccCtrlProcess();
        if (faceInfo !=null){
            accCtrlProcess.setUserName(faceInfo.getUserName());
            accCtrlProcess.setFaceName(faceInfo.getUserName());
            accCtrlProcess.setFaceId(faceInfo.getFaceid());
            if (faceInfo.getGender() != null){
                accCtrlProcess.setGender(faceInfo.getGender());
            }
            try {
                if (faceInfo.getBirthday() != null)
                    accCtrlProcess.setBirthday(CommonUtil.dateFormat.get().parse(faceInfo.getBirthday()));
            } catch (ParseException e) {
                log.error("生日格式有误");
            }
            accCtrlProcess.setProvince(faceInfo.getProvince());
            accCtrlProcess.setCity(faceInfo.getCity());
            if (faceInfo.getCardType() != null){
                accCtrlProcess.setCardType(faceInfo.getCardType());
            }
            accCtrlProcess.setCardId(faceInfo.getCardId());
            accCtrlProcess.setProcessMethod(LockProcessMethod.FACE_RECOGNITION.getCode());//刷脸操作
        }else if (Common.getLogUser()!=null){
            UserInfo logUser = Common.getLogUser();
            accCtrlProcess.setUserName(logUser.getUserName());
           accCtrlProcess.setProcessMethod(LockProcessMethod.Login_Operation.getCode());//app登录操作
        }
        //人脸匹配率需要提供
        if (score !=null){
            accCtrlProcess.setFaceMatchRate(score);
        }else {
            accCtrlProcess.setFaceMatchRate(0.00f);//用户app登录人脸匹配率为0
        }
        if (facePanoramaFile !=null){
            accCtrlProcess.setFacePanorama(facePanoramaFile);
        }
        accCtrlProcess.setCreateTime(snapshotTime);
        AccessControlInfo acInfo = accessControlService.selectAccCtrlByLockCode(lockCode);
        if (acInfo != null) {
            String accessControlId = acInfo.getAccessControlId();
            accCtrlProcess.setAccessControlId(accessControlId);
            String accessControlName = acInfo.getAccessControlName();
            accCtrlProcess.setAccessControlName(accessControlName);
            accCtrlProcess.setLockId(acInfo.getLockId());
        }
        accCtrlProcess.setLockCode(lockCode);

        if (CameraUploadType.Unlock == type) {
            //改为在开锁真正成功后更新状态
            accCtrlProcess.setProcessTime(new Date());
            accCtrlProcess.setProcessType(LockProcessType.UNLOCK.getCode());//开锁
            accCtrlProcess.setAlarmStatus(AlarmProcessType.No_Alarm.getCode());//无报警
            //刷脸成功后开锁操作也有可能失败,成功则failReason=null
            accCtrlProcess.setFailReason(failReason);
        } else if (CameraUploadType.Illegal_Alarm == type) {
            accCtrlProcess.setAlarmStatus(AlarmProcessType.Illegal_LOCK_ALARM.getCode());//非法开锁告警
            accCtrlProcess.setProcessType(LockProcessType.CLOSE_LOCK.getCode());//关锁
            if (CommonUtil.isEmptyStr(failReason)) {
                accCtrlProcess.setFailReason("匹配度过低");
            } else {
                accCtrlProcess.setFailReason(failReason);
            }
        } else {
            //关锁
            accCtrlProcess.setProcessType(LockProcessType.CLOSE_LOCK.getCode());//关锁
        }
        if (processResult != null) {
            accCtrlProcess.setProcessResult(processResult.getCode());
        }
        if (enterOrExit !=null){
            accCtrlProcess.setEnterOrExit(enterOrExit);
        }
        return accCtrlProcess;
    }

    /**
     * 根据门禁操作记录信息组装告警信息入库信息对象
     *
     * @param accCtrlProcess 门禁操作记录对象
     * @return 门禁告警对象
     */
    public Alarm getAlarm(AccCtrlProcess accCtrlProcess) {
        Alarm alarm = new Alarm();
        alarm.setAccCtrlProId(accCtrlProcess.getAccCtrlProId());
        alarm.setAlarmTime(accCtrlProcess.getCreateTime());
        alarm.setAlarmDealStatus(AlarmStatus.UNPROCESSED.getCode());//报警处理状态(0已处理，1未处理)
        alarm.setDescription(accCtrlProcess.getFailReason());
        alarm.setEnterOrExit(accCtrlProcess.getEnterOrExit());
        alarm.setAlarmStatus(accCtrlProcess.getAlarmStatus());
        alarm.setAccessControlId(accCtrlProcess.getAccessControlId());
        return alarm;
    }

    /**
     * 通过门禁操作记录id查询告警
     *
     * @param accCtrlProId 门禁操作记录id
     * @return 门禁操作记录id对应告警
     */
    public Alarm getAlarmByAccCtrlProId(String accCtrlProId) {
        if (CommonUtil.isEmptyStr(accCtrlProId)) {
            return null;
        }
        List<Alarm> alarms = alarmDao.selectList(new QueryWrapper<Alarm>().eq("acc_ctrl_pro_id", accCtrlProId));
        if (CommonUtil.isEmptyList(alarms)) {
            return null;
        }
        return alarms.get(0);
    }


    /**
     * 通过门禁操作信息对象组装门禁实时信息对象以进行入库操作
     *
     * @param accCtrlProcess 门禁操作信息对象
     * @return 门禁实时信息对象
     */
    public AccCtrlRealTimeEntity getAccCtrlRealTimeEntity(CameraUploadType type,AccCtrlProcess accCtrlProcess) {

        String accessControlId;
        if (accCtrlProcess == null || (accessControlId = accCtrlProcess.getAccessControlId()) == null)
            return null;

        AccessControlInfo accessControlInfo = accessControlDao.selectById(accessControlId);
        AccCtrlRealTimeEntity accCtrlRealTimeEntity = new AccCtrlRealTimeEntity();
        //插入时需要
        Date createTime = accCtrlProcess.getCreateTime();
        accCtrlRealTimeEntity.setPicSnapshotTime(createTime);
        AccCtrlRealTimeEntity realTimeEntity = accCtrlRealTimeDao.selectRealTimeInfoByAccCtrlId(accessControlId, null);
        if (realTimeEntity != null && realTimeEntity.getAccCrtlRealTimeId() != null) {
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
        if (accessControlInfo != null) {
            //只有当为报警状态时，才传入上报类型，更新门禁状态，其他状态由轮训器更改
//            if (accCtrlProcess.getProcessType() == LockProcessType.LOCK_ALARM.getCode()) {
//                accCtrlRealTimeEntity.setAccCtrlStatus(accCtrlProcess.getProcessType());
//            } else if (accCtrlProcess.getProcessResult() != null && LockProcessResultType.codeOf(accCtrlProcess.getProcessResult()) != LockProcessResultType.CommandSuccess) {
//                accCtrlRealTimeEntity.setAccCtrlStatus(LockProcessType.CLOSE_LOCK.getCode());
//            }
            if (type !=null && CameraUploadType.Unlock == type ){
                accCtrlRealTimeEntity.setAccCtrlStatus(accCtrlProcess.getProcessType());//开锁状态
                accCtrlRealTimeEntity.setAlarmStatus(accCtrlProcess.getAlarmStatus());//无报警
            }else if (type !=null && CameraUploadType.Lock==type){
                accCtrlRealTimeEntity.setAccCtrlStatus(accCtrlProcess.getProcessType());
            }else if (type !=null  && CameraUploadType.Illegal_Alarm == type){
                accCtrlRealTimeEntity.setAlarmStatus(accCtrlProcess.getAlarmStatus()); //告警状态
                accCtrlRealTimeEntity.setAccCtrlStatus(accCtrlProcess.getProcessType());//关锁状态
            }
            accCtrlRealTimeEntity.setLongitude(accessControlInfo.getLongitude());
            accCtrlRealTimeEntity.setLatitude(accessControlInfo.getLatitude());
            accCtrlRealTimeEntity.setWorkStatus(accessControlInfo.getWorkStatus());
        }
        accCtrlRealTimeEntity.setDevId(accCtrlProcess.getDeviceId());
        accCtrlRealTimeEntity.setDeviceIp(accCtrlProcess.getDeviceIp());
        accCtrlRealTimeEntity.setDeviceType(accCtrlProcess.getDeviceType());
        accCtrlRealTimeEntity.setName(accCtrlProcess.getUserName());//开锁人姓名
        if (accCtrlProcess.getFaceName()!=null){
            accCtrlRealTimeEntity.setUserName(accCtrlProcess.getFaceName());//人脸名称
        }else {
            accCtrlRealTimeEntity.setUserName("");//人脸名称
        }
        accCtrlRealTimeEntity.setGender(accCtrlProcess.getGender());
        accCtrlRealTimeEntity.setBirthday(accCtrlProcess.getBirthday());
        accCtrlRealTimeEntity.setProvince(accCtrlProcess.getProvince());
        accCtrlRealTimeEntity.setCity(accCtrlProcess.getCity());
        accCtrlRealTimeEntity.setCardType(accCtrlProcess.getCardType());
        accCtrlRealTimeEntity.setCardId(accCtrlProcess.getCardId());
        Float faceMatchRate = accCtrlProcess.getFaceMatchRate();
        accCtrlRealTimeEntity.setFaceMatchRate(faceMatchRate);
        accCtrlRealTimeEntity.setFaceLibName(accCtrlProcess.getFaceLibName());
        accCtrlRealTimeEntity.setFaceLibType(accCtrlProcess.getFaceLibType());
        //通过人脸名称查找人脸库图片
       /* com.summit.dao.entity.FaceInfo faceInfo = faceInfoManagerDao.selectOne(Wrappers.<com.summit.dao.entity.FaceInfo>lambdaQuery()
                .eq(com.summit.dao.entity.FaceInfo::getUserName, accCtrlRealTimeEntity.getName()));*/
        com.summit.dao.entity.FaceInfo faceInfo = faceInfoManagerDao.selectOne(Wrappers.<com.summit.dao.entity.FaceInfo>lambdaQuery()
                .eq(com.summit.dao.entity.FaceInfo::getFaceid, accCtrlProcess.getFaceId()));
        //人脸识别全景图Url
        FileInfo facePanorama = accCtrlProcess.getFacePanorama();
        if (facePanorama != null && faceInfo != null) {
            accCtrlRealTimeEntity.setFacePanoramaUrl(facePanorama.getFilePath());
        }else {
            accCtrlRealTimeEntity.setFacePanoramaUrl("");
        }
        if (faceInfo != null) {
            accCtrlRealTimeEntity.setFaceMatchUrl(faceInfo.getFaceImage());
        }else if (Common.getLogUser() !=null && StrUtil.isNotBlank(Common.getLogUser().getHeadPortrait())) {
            accCtrlRealTimeEntity.setFaceMatchUrl(Common.getLogUser().getHeadPortrait());
        }
        accCtrlRealTimeEntity.setUpdatetime(accCtrlProcess.getCreateTime());
        accCtrlRealTimeEntity.setEnterOrExit(accCtrlProcess.getEnterOrExit());
        return accCtrlRealTimeEntity;
    }
}
