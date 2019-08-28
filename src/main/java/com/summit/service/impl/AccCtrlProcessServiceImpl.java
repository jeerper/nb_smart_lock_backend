package com.summit.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.summit.cbb.utils.page.Page;
import com.summit.cbb.utils.page.Pageable;
import com.summit.constants.CommonConstants;
import com.summit.dao.entity.AccCtrlProcess;
import com.summit.dao.entity.AccessControlInfo;
import com.summit.dao.entity.Alarm;
import com.summit.dao.entity.FileInfo;
import com.summit.dao.entity.LockInfo;
import com.summit.dao.entity.LockProcess;
import com.summit.dao.entity.SimplePage;
import com.summit.dao.repository.AccCtrlProcessDao;
import com.summit.dao.repository.AccessControlDao;
import com.summit.dao.repository.AlarmDao;
import com.summit.dao.repository.FileInfoDao;
import com.summit.dao.repository.LockInfoDao;
import com.summit.service.AccCtrlProcessService;
import com.summit.service.AccessControlService;
import com.summit.service.AlarmService;
import com.summit.service.LockInfoService;
import com.summit.util.CommonUtil;
import com.summit.util.LockAuthCtrl;
import com.summit.util.PageConverter;
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
     * @param accCtrlProcess 门禁操作记录
     * @return 不为-1则成功
     */
    @Override
    @Transactional
    public int insertAccCtrlProcess(AccCtrlProcess accCtrlProcess) {
        if(accCtrlProcess == null){
            log.error("锁操作信息为空");
            return CommonConstants.UPDATE_ERROR;
        }
        FileInfo facePanorama = accCtrlProcess.getFacePanorama();
        if(facePanorama != null && facePanorama.getFilePath() != null){
            fileInfoDao.insert(facePanorama);
            accCtrlProcess.setFacePanoramaId(facePanorama.getFileId());
        }
        FileInfo facePic = accCtrlProcess.getFacePic();
        if(facePic != null && facePic.getFilePath() != null) {
            fileInfoDao.insert(facePic);
            accCtrlProcess.setFacePicId(facePic.getFileId());
        }
        FileInfo faceMatch = accCtrlProcess.getFaceMatch();
        if(faceMatch != null && faceMatch.getFilePath() != null) {
            fileInfoDao.insert(faceMatch);
            accCtrlProcess.setFaceMatchId(faceMatch.getFileId());
        }

        AccessControlInfo accessControlInfo = accCtrlProcess.getAccessControlInfo();
        if(accessControlInfo != null){
            accessControlDao.update(accessControlInfo,new UpdateWrapper<AccessControlInfo>().eq("access_control_id",accessControlInfo.getAccessControlId()));
            LockInfo lockInfo = accessControlInfo.getLockInfo();
            if (lockInfo != null) {
                lockInfoService.updateLock(lockInfo);
            }
        }
        return accCtrlProcessDao.insert(accCtrlProcess);
    }

    /**
     * 门禁操作记录更新
     * @param accCtrlProcess 门禁操作记录
     * @return 不为-1则成功
     */
    @Override
    @Transactional
    public int updateAccCtrlProcess(AccCtrlProcess accCtrlProcess) {
        if(accCtrlProcess == null){
            log.error("门禁操作信息为空");
            return CommonConstants.UPDATE_ERROR;
        }
        int result = accCtrlProcessDao.updateRecord(accCtrlProcess);
        if(result != CommonConstants.UPDATE_ERROR){
            UpdateWrapper<FileInfo> updateWrapper = new UpdateWrapper<>();
            FileInfo facePanorama = accCtrlProcess.getFacePanorama();
            if(facePanorama != null){
                fileInfoDao.update(accCtrlProcess.getFacePanorama(),updateWrapper.eq("file_id", facePanorama.getFileId()));
            }
            FileInfo facePic = accCtrlProcess.getFacePic();
            if(facePic != null){
                fileInfoDao.update(accCtrlProcess.getFacePic(),updateWrapper.eq("file_id",facePic.getFileId()));
            }
            FileInfo faceMatch = accCtrlProcess.getFaceMatch();
            if(faceMatch != null){
                fileInfoDao.update(accCtrlProcess.getFaceMatch(),updateWrapper.eq("file_id", faceMatch.getFileId()));
            }
        }
        return result;
    }

    /**
     * 门禁操作记录删除
     * @param accCtrlProId 门禁操作记录id
     * @return 不为-1则成功
     */
    @Override
    public int delAccCtrlProcess(String accCtrlProId) {
        if(accCtrlProId == null){
            log.error("锁操作信息id为空");
            return CommonConstants.UPDATE_ERROR;
        }
        List<String> roles = LockAuthCtrl.getRoles();
        AccCtrlProcess accCtrlProcess = accCtrlProcessDao.selectAccCtrlProcessById(accCtrlProId, null);
        UpdateWrapper<AccCtrlProcess> wrapper = new UpdateWrapper<>();
        UpdateWrapper<FileInfo> fileWrapper = new UpdateWrapper<>();
        int result = accCtrlProcessDao.delete(wrapper.eq("acc_ctrl_pro_id", accCtrlProId));
        if(result != CommonConstants.UPDATE_ERROR && accCtrlProcess != null){
            fileInfoDao.delete(fileWrapper.eq("file_id",accCtrlProcess.getFacePanorama().getFileId()));
            fileInfoDao.delete(fileWrapper.eq("file_id",accCtrlProcess.getFacePic().getFileId()));
            fileInfoDao.delete(fileWrapper.eq("file_id",accCtrlProcess.getFaceMatch().getFileId()));
        }
        return result;
    }

    /**
     * 门禁操作记录批量删除
     * @param accCtrlProIds 门禁操作记录id列表
     * @return 不为-1则成功
     */
    @Override
    public int delAccCtrlProcessByIdBatch(List<String> accCtrlProIds) {
        if(CommonUtil.isEmptyList(accCtrlProIds)){
            log.error("门禁操作记录id列表为空");
            return CommonConstants.UPDATE_ERROR;
        }
        List<String> alarmIds = new ArrayList<>();
        for(String accCtrlProId : accCtrlProIds) {
            Alarm alarm = alarmService.selectAlarmByIdBeyondAuthority(accCtrlProId);
            if(alarm == null || alarm.getAlarmId() == null)
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
     * @param page 分页对象
     * @return 门禁操作记录列表
     */
    @Override
    public List<AccCtrlProcess> selectAll(SimplePage page) {
        PageConverter.convertPage(page);
        return accCtrlProcessDao.selectCondition(new AccCtrlProcess(), null, null, page, LockAuthCtrl.getRoles());
    }

    /**
     * 根据Id查询
     * @param accCtrlProId 门禁操作记录id
     * @return 唯一确定门禁操作记录
     */
    @Override
    public AccCtrlProcess selectAccCtrlProcessById(String accCtrlProId) {
        if(accCtrlProId == null) {
            log.error("门禁操作记录id为空");
            return null;
        }
        return accCtrlProcessDao.selectAccCtrlProcessById(accCtrlProId, LockAuthCtrl.getRoles());
    }

    /**
     * 根据门禁id查询，可指定时间段
     * @param accessControlId 门禁id
     * @param start 开始时间
     * @param end 截止时间
     * @param page 分页对象
     * @return 门禁操作记录列表
     */
    @Override
    public List<AccCtrlProcess> selectAccCtrlProcessByAccCtrlId(String accessControlId, Date start, Date end, SimplePage page) {
        if(accessControlId == null) {
            log.error("门禁id为空");
            return null;
        }
        PageConverter.convertPage(page);
        AccCtrlProcess accCtrlProcess = new AccCtrlProcess();
        accCtrlProcess.setAccessControlId(accessControlId);
        return accCtrlProcessDao.selectCondition(accCtrlProcess,start,end,page,LockAuthCtrl.getRoles());
    }

    /**
     * 根据门禁id查询，不带时间重载
     * @param accessControlId 门禁id
     * @param page 分页对象
     * @return 门禁操作记录列表
     */
    @Override
    public List<AccCtrlProcess> selectAccCtrlProcessByAccCtrlId(String accessControlId, SimplePage page) {
        return selectAccCtrlProcessByAccCtrlId(accessControlId,null,null,page);
    }

    /**
     * 根据门禁名查询，可指定时间段
     * @param accessControlName 门禁名称
     * @param start 开始时间
     * @param end 截止时间
     * @param page 分页对象
     * @return 门禁操作记录列表
     */
    @Override
    public List<AccCtrlProcess> selectAccCtrlProcessByName(String accessControlName, Date start, Date end, SimplePage page) {
        if(accessControlName == null) {
            log.error("门禁名称为空");
            return null;
        }
        PageConverter.convertPage(page);
        AccCtrlProcess accCtrlProcess = new AccCtrlProcess();
        accCtrlProcess.setAccessControlName(accessControlName);
        return accCtrlProcessDao.selectCondition(accCtrlProcess,start,end,page,LockAuthCtrl.getRoles());
    }

    /**
     * 根据门禁名查询，不带时间重载
     * @param accessControlName 门禁名称
     * @param page 分页对象
     * @return 门禁操作记录列表
     */
    @Override
    public List<AccCtrlProcess> selectAccCtrlProcessByName(String accessControlName, SimplePage page) {
        return selectAccCtrlProcessByName(accessControlName,null,null,page);
    }

    /**
     * 根据锁编号查询，可指定时间段
     * @param lockCode 锁编号
     * @param start 开始时间
     * @param end 截止时间
     * @param page 分页对象
     * @return 门禁操作记录列表
     */
    @Override
    public List<AccCtrlProcess> selectAccCtrlProcessByLockCode(String lockCode, Date start, Date end, SimplePage page) {
        if(lockCode == null) {
            log.error("锁编号为空");
            return null;
        }
        PageConverter.convertPage(page);
        AccCtrlProcess accCtrlProcess = new AccCtrlProcess();
        accCtrlProcess.setLockCode(lockCode);
        return accCtrlProcessDao.selectCondition(accCtrlProcess,start,end,page,LockAuthCtrl.getRoles());
    }

    /**
     * 根据锁编号查询，不带时间重载
     * @param lockCode 锁编号
     * @param page 分页对象
     * @return 门禁操作记录列表
     */
    @Override
    public List<AccCtrlProcess> selectAccCtrlProcessByLockCode(String lockCode, SimplePage page) {
        return selectAccCtrlProcessByLockCode(lockCode,null,null, page);
    }

    /**
     * 根据操作类型查询（开门禁或关门禁），可指定时间段
     * @param processType 门禁操作类型（开门禁或关门禁）
     * @param start 开始时间
     * @param end 截止时间
     * @param page 分页对象
     * @return 门禁操作记录列表
     */
    @Override
    public List<AccCtrlProcess> selectAccCtrlProcessByType(Integer processType, Date start, Date end, SimplePage page) {
        if(processType == null) {
            log.error("操作类型为空");
            return null;
        }
        PageConverter.convertPage(page);
        AccCtrlProcess accCtrlProcess = new AccCtrlProcess();
        accCtrlProcess.setProcessType(processType);
        return accCtrlProcessDao.selectCondition(accCtrlProcess,start,end,page,LockAuthCtrl.getRoles());
    }

    /**
     * 根据操作类型查询（开门禁或关门禁），不带时间重载
     * @param processType 门禁操作类型（开门禁或关门禁）
     * @param page 分页对象
     * @return 门禁操作记录列表
     */
    @Override
    public List<AccCtrlProcess> selectAccCtrlProcessByType(Integer processType, SimplePage page) {
        return selectAccCtrlProcessByType(processType,null,null,page);
    }

    /**
     * 指定条件查询，可指定时间段
     * @param accCtrlProcess 门禁操作记录对象
     * @param start 开始时间
     * @param end 截止时间
     * @param page 分页对象
     * @return 门禁操作记录列表
     */
    @Override
    public Page<AccCtrlProcess> selectAccCtrlProcessCondition(AccCtrlProcess accCtrlProcess, Date start, Date end, SimplePage page) {
        if(accCtrlProcess == null) {
            accCtrlProcess = new AccCtrlProcess();
        }
        int rowsCount = accCtrlProcessDao.selectConutByCondition(new AccCtrlProcess(), null, null, LockAuthCtrl.getRoles());
        Pageable pageable = PageConverter.getPageable(page, rowsCount);
        PageConverter.convertPage(page);
        Page<AccCtrlProcess> backPage = new Page<>();
        List<AccCtrlProcess> accCtrlProcesses = accCtrlProcessDao.selectCondition(accCtrlProcess, start, end, page, LockAuthCtrl.getRoles());
        backPage.setContent(accCtrlProcesses);
        backPage.setPageable(pageable);
        return backPage;
    }

    /**
     * 指定条件查询，不带日期的重载
     * @param accCtrlProcess 门禁操作记录对象
     * @param page 分页对象
     * @return 门禁操作记录列表
     */
    @Override
    public Page<AccCtrlProcess> selectAccCtrlProcessCondition(AccCtrlProcess accCtrlProcess, SimplePage page) {
        return selectAccCtrlProcessCondition(accCtrlProcess,null,null,page);
    }
}
