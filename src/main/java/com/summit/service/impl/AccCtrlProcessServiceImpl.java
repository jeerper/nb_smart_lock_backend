package com.summit.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.summit.cbb.utils.page.Page;
import com.summit.common.util.UserAuthUtils;
import com.summit.constants.CommonConstants;
import com.summit.dao.entity.*;
import com.summit.dao.repository.AccCtrlProcessDao;
import com.summit.dao.repository.AccessControlDao;
import com.summit.dao.repository.AlarmDao;
import com.summit.dao.repository.FileInfoDao;
import com.summit.sdk.huawei.model.CameraUploadType;
import com.summit.service.AccCtrlProcessService;
import com.summit.service.AccessControlService;
import com.summit.service.AlarmService;
import com.summit.service.LockInfoService;
import com.summit.util.CommonUtil;
import com.summit.util.UserDeptAuthUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class AccCtrlProcessServiceImpl implements AccCtrlProcessService {

    @Autowired
    private AccCtrlProcessDao accCtrlProcessDao;
    @Autowired
    private AccessControlService accessControlService;
    @Autowired
    private AccessControlDao accessControlDao;
    @Autowired
    private FileInfoDao fileInfoDao;
    @Autowired
    private AlarmService alarmService;
    @Autowired
    private AlarmDao alarmDao;
    @Autowired
    private LockInfoService lockInfoService;

    /**
     * 门禁操作记录插入
     *
     * @param accCtrlProcess 门禁操作记录
     * @return 不为-1则成功
     */
    @Override
    @Transactional
    public int insertAccCtrlProcess(CameraUploadType type, AccCtrlProcess accCtrlProcess) {
        if (accCtrlProcess == null) {
            log.error("锁操作信息为空");
            return CommonConstants.UPDATE_ERROR;
        }
        FileInfo facePanorama = accCtrlProcess.getFacePanorama();
        if (facePanorama != null && facePanorama.getFilePath() != null) {
            fileInfoDao.insert(facePanorama);
            accCtrlProcess.setFacePanoramaId(facePanorama.getFileId());
        }

        if (type !=null && CameraUploadType.Unlock == type ){
            accessControlDao.update(null, Wrappers.<AccessControlInfo>lambdaUpdate()
                    .set(AccessControlInfo::getStatus,accCtrlProcess.getProcessType())
                    .eq(AccessControlInfo::getAccessControlId, accCtrlProcess.getAccessControlId()));
        }else if (type !=null  && CameraUploadType.Illegal_Alarm == type){
            accessControlDao.update(null, Wrappers.<AccessControlInfo>lambdaUpdate()
                    .set(AccessControlInfo::getAlarmStatus,accCtrlProcess.getAlarmStatus())
                    .set(AccessControlInfo::getStatus,accCtrlProcess.getProcessType())
                    .eq(AccessControlInfo::getAccessControlId, accCtrlProcess.getAccessControlId()));
        }else if (type !=null && CameraUploadType.Lock==type){
            accessControlDao.update(null, Wrappers.<AccessControlInfo>lambdaUpdate()
                    .set(AccessControlInfo::getStatus,accCtrlProcess.getProcessType())
                    .eq(AccessControlInfo::getAccessControlId, accCtrlProcess.getAccessControlId()));
        }


        LockInfo lockInfo = new LockInfo();
        lockInfo.setLockId(accCtrlProcess.getLockId());
        lockInfo.setLockCode(accCtrlProcess.getLockCode());
        if ( type !=null && CameraUploadType.Lock==type){
            lockInfo.setStatus(accCtrlProcess.getProcessType());
        }else if (type !=null && CameraUploadType.Illegal_Alarm == type){
            lockInfo.setAlarmStatus(accCtrlProcess.getAlarmStatus());
            lockInfo.setStatus(accCtrlProcess.getProcessType());
        }else if (type !=null && CameraUploadType.Unlock == type ){
            lockInfo.setStatus(accCtrlProcess.getProcessType());
        }
        lockInfo.setUpdatetime(accCtrlProcess.getCreateTime());
        lockInfoService.updateLock(lockInfo);

        return accCtrlProcessDao.insert(accCtrlProcess);
    }

    /**
     * 门禁操作记录更新
     *
     * @param accCtrlProcess 门禁操作记录
     * @return 不为-1则成功
     */
    @Override
    @Transactional
    public int updateAccCtrlProcess(AccCtrlProcess accCtrlProcess) {
        if (accCtrlProcess == null) {
            log.error("门禁操作信息为空");
            return CommonConstants.UPDATE_ERROR;
        }
        int result = accCtrlProcessDao.updateRecord(accCtrlProcess);
        if (result != CommonConstants.UPDATE_ERROR) {
            UpdateWrapper<FileInfo> updateWrapper = new UpdateWrapper<>();
            FileInfo facePanorama = accCtrlProcess.getFacePanorama();
            if (facePanorama != null) {
                fileInfoDao.update(accCtrlProcess.getFacePanorama(), updateWrapper.eq("file_id", facePanorama.getFileId()));
            }
            FileInfo facePic = accCtrlProcess.getFacePic();
            if (facePic != null) {
                fileInfoDao.update(accCtrlProcess.getFacePic(), updateWrapper.eq("file_id", facePic.getFileId()));
            }
            FileInfo faceMatch = accCtrlProcess.getFaceMatch();
            if (faceMatch != null) {
                fileInfoDao.update(accCtrlProcess.getFaceMatch(), updateWrapper.eq("file_id", faceMatch.getFileId()));
            }
        }
        return result;
    }

    /**
     * 门禁操作记录删除
     *
     * @param accCtrlProId 门禁操作记录id
     * @return 不为-1则成功
     */
    @Override
    public int delAccCtrlProcess(String accCtrlProId) {
        if (accCtrlProId == null) {
            log.error("锁操作信息id为空");
            return CommonConstants.UPDATE_ERROR;
        }
        List<String> roles = UserAuthUtils.getRoles();
        AccCtrlProcess accCtrlProcess = accCtrlProcessDao.selectAccCtrlProcessById(accCtrlProId, null);
        UpdateWrapper<AccCtrlProcess> wrapper = new UpdateWrapper<>();
        UpdateWrapper<FileInfo> fileWrapper = new UpdateWrapper<>();
        int result = accCtrlProcessDao.delete(wrapper.eq("acc_ctrl_pro_id", accCtrlProId));
        if (result != CommonConstants.UPDATE_ERROR && accCtrlProcess != null) {
            fileInfoDao.delete(fileWrapper.eq("file_id", accCtrlProcess.getFacePanorama().getFileId()));
            fileInfoDao.delete(fileWrapper.eq("file_id", accCtrlProcess.getFacePic().getFileId()));
            fileInfoDao.delete(fileWrapper.eq("file_id", accCtrlProcess.getFaceMatch().getFileId()));
        }
        return result;
    }

    /**
     * 门禁操作记录批量删除
     *
     * @param accCtrlProIds 门禁操作记录id列表
     * @return 不为-1则成功
     */
    @Override
    public int delAccCtrlProcessByIdBatch(List<String> accCtrlProIds) {
        if (CommonUtil.isEmptyList(accCtrlProIds)) {
            log.error("门禁操作记录id列表为空");
            return CommonConstants.UPDATE_ERROR;
        }
        List<String> alarmIds = new ArrayList<>();
        for (String accCtrlProId : accCtrlProIds) {
            Alarm alarm = alarmService.selectAlarmByIdBeyondAuthority(accCtrlProId);
            if (alarm == null || alarm.getAlarmId() == null)
                continue;
            alarmIds.add(alarm.getAlarmId());
        }
        try {
            alarmDao.deleteBatchIds(alarmIds);
        } catch (Exception e) {
            log.error("删除门禁操作记录关联的告警信息失败");
        }
        int result = -1;
        try {
            result = accCtrlProcessDao.deleteBatchIds(accCtrlProIds);
        } catch (Exception e) {
            log.error("删除门禁操作记录失败");
        }
        return result;
    }

    /**
     * 查询所有门禁操作记录
     *
     * @return 门禁操作记录列表
     */
    @Override
    public List<AccCtrlProcess> selectAll() {
        return accCtrlProcessDao.selectCondition(null, null, null, null, UserAuthUtils.getRoles());
    }

    /**
     * 根据Id查询
     *
     * @param accCtrlProId 门禁操作记录id
     * @return 唯一确定门禁操作记录
     */
    @Override
    public AccCtrlProcess selectAccCtrlProcessById(String accCtrlProId) {
        if (accCtrlProId == null) {
            log.error("门禁操作记录id为空");
            return null;
        }
        return accCtrlProcessDao.selectAccCtrlProcessById(accCtrlProId,UserDeptAuthUtils.getDepts());
    }

    /**
     * 根据门禁id查询，可指定时间段
     *
     * @param accessControlId 门禁id
     * @param start           开始时间
     * @param end             截止时间
     * @return 门禁操作记录列表
     */
    @Override
    public List<AccCtrlProcess> selectAccCtrlProcessByAccCtrlId(String accessControlId, Date start, Date end, Integer current, Integer pageSize) {
        if (accessControlId == null) {
            log.error("门禁id为空");
            return null;
        }
        AccCtrlProcess accCtrlProcess = new AccCtrlProcess();
        accCtrlProcess.setAccessControlId(accessControlId);
        Page<AccCtrlProcess> pageParam = null;
        if (current != null && pageSize != null) {
            pageParam = new Page<>(current, pageSize);
        }
        return accCtrlProcessDao.selectCondition(pageParam, accCtrlProcess, start, end, UserAuthUtils.getRoles());
    }

    /**
     * 根据门禁id查询，不带时间重载
     *
     * @param accessControlId 门禁id
     * @return 门禁操作记录列表
     */
    @Override
    public List<AccCtrlProcess> selectAccCtrlProcessByAccCtrlId(String accessControlId, Integer current, Integer pageSize) {
        return selectAccCtrlProcessByAccCtrlId(accessControlId, null, null, current, pageSize);
    }

    /**
     * 根据门禁名查询，可指定时间段
     *
     * @param accessControlName 门禁名称
     * @param start             开始时间
     * @param end               截止时间
     * @return 门禁操作记录列表
     */
    @Override
    public List<AccCtrlProcess> selectAccCtrlProcessByName(String accessControlName, Date start, Date end, Integer current, Integer pageSize) {
        if (accessControlName == null) {
            log.error("门禁名称为空");
            return null;
        }
        Page<AccCtrlProcess> pageParam = null;

        if (current != null && pageSize != null) {
            pageParam = new Page<>(current, pageSize);
        }
        AccCtrlProcess accCtrlProcess = new AccCtrlProcess();
        accCtrlProcess.setAccessControlName(accessControlName);
        return accCtrlProcessDao.selectCondition(pageParam, accCtrlProcess, start, end, UserAuthUtils.getRoles());
    }

    /**
     * 根据门禁名查询，不带时间重载
     *
     * @param accessControlName 门禁名称
     * @return 门禁操作记录列表
     */
    @Override
    public List<AccCtrlProcess> selectAccCtrlProcessByName(String accessControlName, Integer current, Integer pageSize) {
        return selectAccCtrlProcessByName(accessControlName, null, null, current, pageSize);
    }

    /**
     * 根据锁编号查询，可指定时间段
     *
     * @param lockCode 锁编号
     * @param start    开始时间
     * @param end      截止时间
     * @return 门禁操作记录列表
     */
    @Override
    public List<AccCtrlProcess> selectAccCtrlProcessByLockCode(String lockCode, Date start, Date end, Integer current, Integer pageSize) {
        if (lockCode == null) {
            log.error("锁编号为空");
            return null;
        }
        Page<AccCtrlProcess> pageParam = null;

        if (current != null && pageSize != null) {
            pageParam = new Page<>(current, pageSize);
        }
        AccCtrlProcess accCtrlProcess = new AccCtrlProcess();
        accCtrlProcess.setLockCode(lockCode);
        return accCtrlProcessDao.selectCondition(pageParam, accCtrlProcess, start, end, UserAuthUtils.getRoles());
    }

    /**
     * 根据锁编号查询，不带时间重载
     *
     * @param lockCode 锁编号
     * @return 门禁操作记录列表
     */
    @Override
    public List<AccCtrlProcess> selectAccCtrlProcessByLockCode(String lockCode, Integer current, Integer pageSize) {
        return selectAccCtrlProcessByLockCode(lockCode, null, null, current, pageSize);
    }

    /**
     * 根据操作类型查询（开门禁或关门禁），可指定时间段
     *
     * @param processType 门禁操作类型（开门禁或关门禁）
     * @param start       开始时间
     * @param end         截止时间
     * @return 门禁操作记录列表
     */
    @Override
    public List<AccCtrlProcess> selectAccCtrlProcessByType(Integer processType, Date start, Date end, Integer current, Integer pageSize) {
        if (processType == null) {
            log.error("操作类型为空");
            return null;
        }
        Page<AccCtrlProcess> pageParam = null;

        if (current != null && pageSize != null) {
            pageParam = new Page<>(current, pageSize);
        }
        AccCtrlProcess accCtrlProcess = new AccCtrlProcess();
        accCtrlProcess.setProcessType(processType);
        return accCtrlProcessDao.selectCondition(pageParam, accCtrlProcess, start, end, UserAuthUtils.getRoles());
    }

    /**
     * 根据操作类型查询（开门禁或关门禁），不带时间重载
     *
     * @param processType 门禁操作类型（开门禁或关门禁）
     * @return 门禁操作记录列表
     */
    @Override
    public List<AccCtrlProcess> selectAccCtrlProcessByType(Integer processType, Integer current, Integer pageSize) {
        return selectAccCtrlProcessByType(processType, null, null, current, pageSize);
    }

    /**
     * 指定条件查询，可指定时间段
     *
     * @param accCtrlProcess 门禁操作记录对象
     * @param start          开始时间
     * @param end            截止时间
     * @return 门禁操作记录列表
     */
    @Override
    public Page<AccCtrlProcess> selectAccCtrlProcessCondition(AccCtrlProcess accCtrlProcess, Date start, Date end, Integer current,
                                                              Integer pageSize) {

        Page<AccCtrlProcess> pageParam = null;

        if (current != null && pageSize != null) {
            pageParam = new Page<>(current, pageSize);
        }
        List<AccCtrlProcess> accCtrlProcesses = accCtrlProcessDao.selectCondition(pageParam, accCtrlProcess, start, end, UserDeptAuthUtils.getDepts());

        if (pageParam != null) {
            pageParam.setRecords(accCtrlProcesses);
            return pageParam;
        }
        return new Page<>(accCtrlProcesses, null);
    }

    /**
     * 根据门禁操作id记录查询当前的操所记录
     *
     * @param accCtrlProId
     * @return 当前的操所记录
     */
    @Override
    public AccCtrlProcess selectAccCtrlProcessByAcpId(String accCtrlProId) {
        AccCtrlProcess accCtrlProcess = accCtrlProcessDao.selectById(accCtrlProId);
        return accCtrlProcess;
    }

}
