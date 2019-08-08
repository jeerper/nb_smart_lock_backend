package com.summit.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.summit.dao.entity.CameraDevice;
import com.summit.dao.entity.FileInfo;
import com.summit.dao.entity.LockInfo;
import com.summit.dao.entity.LockProcess;
import com.summit.dao.entity.Page;
import com.summit.dao.repository.AlarmDao;
import com.summit.dao.repository.CameraDeviceDao;
import com.summit.dao.repository.FileInfoDao;
import com.summit.dao.repository.LockInfoDao;
import com.summit.dao.repository.LockProcessDao;
import com.summit.service.LockInfoService;
import com.summit.service.LockRecordService;
import com.summit.util.LockAuthCtrl;
import com.summit.util.PageConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class LockRecordServiceImpl implements LockRecordService {
    @Autowired
    private LockProcessDao lockProcessDao;
    @Autowired
    private AlarmDao alarmDao;
    @Autowired
    private FileInfoDao fileInfoDao;
    @Autowired
    private CameraDeviceDao deviceDao;
    @Autowired
    private LockInfoService lockInfoService;

    /**
     * 锁操作记录插入
     * @param lockProcess 锁操作记录
     * @return 不为-1则成功
     */
    @Override
    public int insertLockProcess(LockProcess lockProcess) {
        if(lockProcess == null){
            log.error("锁操作信息为空");
            return -1;
        }
        FileInfo facePanorama = lockProcess.getFacePanorama();
        if(facePanorama != null){
            fileInfoDao.insert(facePanorama);
            lockProcess.setFacePanoramaId(facePanorama.getFileId());
        }
        FileInfo facePic = lockProcess.getFacePic();
        if(facePic != null) {
            fileInfoDao.insert(facePic);
            lockProcess.setFacePicId(facePic.getFileId());
        }
        FileInfo faceMatch = lockProcess.getFaceMatch();
        if(faceMatch != null) {
            fileInfoDao.insert(faceMatch);
            lockProcess.setFaceMatchId(faceMatch.getFileId());
        }

        int result = lockProcessDao.insert(lockProcess);
        LockInfo lockInfo = lockProcess.getLockInfo();
        if(lockInfo != null){
            lockInfoService.updateLock(lockInfo);
        }
        return result;
    }

    /**
     * 锁操作记录更新
     * @param lockProcess 锁操作记录
     * @return 不为-1则成功
     */
    @Override
    public int updateLockProcess(LockProcess lockProcess) {
        if(lockProcess == null){
            log.error("锁操作信息为空");
            return -1;
        }
        int result = lockProcessDao.updateRecord(lockProcess);
        if(result != -1){
            UpdateWrapper<FileInfo> updateWrapper = new UpdateWrapper<>();
            FileInfo facePanorama = lockProcess.getFacePanorama();
            if(facePanorama != null){
                fileInfoDao.update(lockProcess.getFacePanorama(),updateWrapper.eq("file_id", facePanorama.getFileId()));
            }
            FileInfo facePic = lockProcess.getFacePic();
            if(facePic != null){
                fileInfoDao.update(lockProcess.getFacePic(),updateWrapper.eq("file_id",facePic.getFileId()));
            }
            FileInfo faceMatch = lockProcess.getFaceMatch();
            if(faceMatch != null){
                fileInfoDao.update(lockProcess.getFaceMatch(),updateWrapper.eq("file_id", faceMatch.getFileId()));
            }
        }
        return result;
    }

    /**
     * 锁操作记录删除
     * @param processId 锁操作记录id
     * @return 不为-1则成功
     */
    @Override
    public int delLockProcess(String processId) {
        if(processId == null){
            log.error("锁操作信息id为空");
            return -1;
        }
        List<String> roles = LockAuthCtrl.getRoles();
        LockProcess lockProcess = lockProcessDao.selectLockProcessById(processId, roles);
        UpdateWrapper<LockProcess> wrapper = new UpdateWrapper<>();
        UpdateWrapper<FileInfo> fileWrapper = new UpdateWrapper<>();
        int result = lockProcessDao.delete(wrapper.eq("process_id", processId));
        if(result != 1){
            fileInfoDao.delete(fileWrapper.eq("file_id",lockProcess.getFacePanorama().getFileId()));
            fileInfoDao.delete(fileWrapper.eq("file_id",lockProcess.getFacePic().getFileId()));
            fileInfoDao.delete(fileWrapper.eq("file_id",lockProcess.getFaceMatch().getFileId()));
        }
        return result;
    }

    /**
     * 查询所有锁操作记录
     * @param page 分页对象
     * @return 锁操作记录列表
     */
    @Override
    public List<LockProcess> selectAll(Page page) {
        PageConverter.convertPage(page);
        List<String> roles = LockAuthCtrl.getRoles();
        List<LockProcess> lockProcesses = lockProcessDao.selectCondition(new LockProcess(), null, null, page,roles);

//        LockAuthCtrl.toFilterLockProcesses(lockProcesses);
        return lockProcesses;
    }

    /**
     * 根据Id查询
     * @param processId 锁操作记录id
     * @return 唯一确定锁操作记录
     */
    @Override
    public LockProcess selectLockProcessById(String processId) {
        if(processId == null){
            log.error("锁操作信息id为空");
            return null;
        }
        List<String> roles = LockAuthCtrl.getRoles();
        LockProcess lockProcess = lockProcessDao.selectLockProcessById(processId, roles);
        if(!LockAuthCtrl.toFilter(lockProcess)){
            return null;
        }
        return lockProcess;
    }

    /**
     * 根据锁编号查询，可指定时间段
     * @param lockCode 锁编号
     * @param start 开始时间
     * @param end 截止时间
     * @param page 分页对象
     * @return 锁操作记录列表
     */
    @Override
    public List<LockProcess> selectLockProcessByLockCode(String lockCode, Date start, Date end, Page page) {
        if(lockCode == null){
            log.error("锁编号为空");
            return null;
        }
        PageConverter.convertPage(page);
        LockProcess lockProcess = new LockProcess();
        lockProcess.setLockCode(lockCode);
        List<String> roles = LockAuthCtrl.getRoles();
        List<LockProcess> lockProcesses = lockProcessDao.selectCondition(lockProcess, start, end, page, roles);
//        LockAuthCtrl.toFilterLockProcesses(lockProcesses);
        return lockProcesses;
    }

    /**
     * 根据锁编号查询，不带时间重载
     * @param lockCode 锁编号
     * @param page 分页对象
     * @return 锁操作记录列表
     */
    @Override
    public List<LockProcess> selectLockProcessByLockCode(String lockCode, Page page) {
        if(lockCode == null){
            log.error("锁编号为空");
            return null;
        }
        PageConverter.convertPage(page);
        List<String> roles = LockAuthCtrl.getRoles();
        List<LockProcess> lockProcesses = lockProcessDao.selectByLockCode(lockCode, page, roles);
//        LockAuthCtrl.toFilterLockProcesses(lockProcesses);
        return lockProcesses;
    }

    /**
     * 根据设备ip地址查询，可指定时间段
     * @param deviceIp 摄像头ip地址
     * @param start 开始时间
     * @param end 截止时间
     * @param page 分页对象
     * @return 锁操作记录列表
     */
    @Override
    public List<LockProcess> selectLockProcessByDeviceIp(String deviceIp, Date start, Date end, Page page) {
        if(deviceIp == null){
            log.error("设备ip地址为空");
            return null;
        }
        PageConverter.convertPage(page);
        LockProcess lockProcess = new LockProcess();
        lockProcess.setDeviceIp(deviceIp);
        List<String> roles = LockAuthCtrl.getRoles();
        List<LockProcess> lockProcesses = lockProcessDao.selectCondition(lockProcess, start, end, page, roles);
//        LockAuthCtrl.toFilterLockProcesses(lockProcesses);
        return lockProcesses;
    }

    /**
     * 根据设备ip地址查询，不带时间重载
     * @param deviceIp 摄像头ip地址
     * @param page 分页对象
     * @return 锁操作记录列表
     */
    @Override
    public List<LockProcess> selectLockProcessByDeviceIp(String deviceIp, Page page) {
        if(deviceIp == null){
            log.error("设备ip地址为空");
            return null;
        }
        return selectLockProcessByDeviceIp(deviceIp,null,null, page);
    }

    /**
     * 根据锁操作记录对应的设备id查询，可指定时间段
     * @param devId 摄像头id
     * @param start 开始时间
     * @param end 截止时间
     * @param page 分页对象
     * @return 锁操作记录列表
     */
    @Override
    public List<LockProcess> selectLockProcessByDevId(String devId, Date start, Date end, Page page) {
        if(devId == null){
            log.error("设备id为空");
            return null;
        }
        PageConverter.convertPage(page);
        CameraDevice device = deviceDao.selectDeviceById(devId);
        LockProcess lp = new LockProcess();
        lp.setDeviceIp(device.getDeviceIp());
        List<String> roles = LockAuthCtrl.getRoles();
        List<LockProcess> lockProcesses = lockProcessDao.selectCondition(lp, start, end, page, roles);
//        LockAuthCtrl.toFilterLockProcesses(lockProcesses);
        return lockProcesses;
    }

    /**
     * 根据锁操作记录对应的设备id查询，不带时间重载
     * @param devId 摄像头id
     * @param page 分页对象
     * @return 锁操作记录列表
     */
    @Override
    public List<LockProcess> selectLockProcessByDevId(String devId, Page page) {
        if(devId == null){
            log.error("设备id为空");
            return null;
        }
        return selectLockProcessByDevId(devId,null,null, page);
    }

    /**
     * 根据操作人名称查询，可指定时间段
     * @param userName 操作人名称
     * @param start 开始时间
     * @param end 截止时间
     * @param page 分页对象
     * @return 锁操作记录列表
     */
    @Override
    public List<LockProcess> selectLockProcessByUserName(String userName, Date start, Date end, Page page) {
        if(userName == null){
            log.error("操作人为空");
            return null;
        }
        PageConverter.convertPage(page);
        LockProcess lockProcess = new LockProcess();
        lockProcess.setUserName(userName);
        List<String> roles = LockAuthCtrl.getRoles();
        List<LockProcess> lockProcesses = lockProcessDao.selectCondition(lockProcess, start, end, page, roles);
//        LockAuthCtrl.toFilterLockProcesses(lockProcesses);
        return lockProcesses;
    }

    /**
     * 根据操作人名称查询，不带时间重载
     * @param userName 操作人名称
     * @param page 分页对象
     * @return 锁操作记录列表
     */
    @Override
    public List<LockProcess> selectLockProcessByUserName(String userName, Page page) {
        if(userName == null){
            log.error("操作人为空");
            return null;
        }
        return selectLockProcessByUserName(userName,null,null, page);
    }

    /**
     * 根据操作类型查询（开锁或关锁），可指定时间段
     * @param processType 锁操作类型（开锁或关锁）
     * @param start 开始时间
     * @param end 截止时间
     * @param page 分页对象
     * @return 锁操作记录列表
     */
    @Override
    public List<LockProcess> selectLockProcessByType(Integer processType, Date start, Date end, Page page) {
        if(processType == null){
            log.error("操作类型为空");
            return null;
        }
        PageConverter.convertPage(page);
        LockProcess lockProcess = new LockProcess();
        lockProcess.setProcessType(processType);
        List<String> roles = LockAuthCtrl.getRoles();
        List<LockProcess> lockProcesses = lockProcessDao.selectCondition(lockProcess, start, end, page, roles);
//        LockAuthCtrl.toFilterLockProcesses(lockProcesses);
        return lockProcesses;
    }

    /**
     * 根据操作类型查询（开锁或关锁），不带时间重载
     * @param processType 锁操作类型（开锁或关锁）
     * @param page 分页对象
     * @return 锁操作记录列表
     */
    @Override
    public List<LockProcess> selectLockProcessByType(Integer processType, Page page) {
        if(processType == null){
            log.error("操作类型为空");
            return null;
        }
        return selectLockProcessByType(processType,null,null, page);
    }

    /**
     * 根据操作结果类型查询（成功或失败），可指定时间段
     * @param processResult 操作结果
     * @param start 开始时间
     * @param end 截止时间
     * @param page 分页对象
     * @return 锁操作记录列表
     */
    @Override
    public List<LockProcess> selectLockProcessByResult(String processResult, Date start, Date end, Page page) {
        if(processResult == null){
            log.error("操作结果为空");
            return null;
        }
        PageConverter.convertPage(page);
        LockProcess lockProcess = new LockProcess();
        lockProcess.setProcessResult(processResult);
        List<String> roles = LockAuthCtrl.getRoles();
        List<LockProcess> lockProcesses = lockProcessDao.selectCondition(lockProcess, start, end, page, roles);
//        LockAuthCtrl.toFilterLockProcesses(lockProcesses);
        return lockProcesses;
    }

    /**
     * 根据操作结果类型查询（成功或失败），不带时间重载
     * @param processResult 操作结果
     * @param page 分页对象
     * @return 锁操作记录列表
     */
    @Override
    public List<LockProcess> selectLockProcessByResult(String processResult, Page page) {
        if(processResult == null){
            log.error("操作结果为空");
            return null;
        }
        return selectLockProcessByResult(processResult,null,null, page);
    }

    /**
     * 指定条件查询，可指定时间段
     * @param lockProcess 锁操作记录对象
     * @param start 开始时间
     * @param end 截止时间
     * @param page 分页对象
     * @return 锁操作记录列表
     */
    @Override
    public List<LockProcess> selectLockProcessCondition(LockProcess lockProcess, Date start, Date end, Page page) {
        if(lockProcess == null){
            log.error("锁操作信息为空");
            return null;
        }
        PageConverter.convertPage(page);
        List<String> roles = LockAuthCtrl.getRoles();
        List<LockProcess> lockProcesses = lockProcessDao.selectCondition(lockProcess, start, end, page,roles);
//        LockAuthCtrl.toFilterLockProcesses(lockProcesses);
        return lockProcesses;
    }

    /**
     * 指定条件查询，不带日期的重载
     * @param lockProcess 锁操作记录对象
     * @param page 分页对象
     * @return 锁操作记录列表
     */
    @Override
    public List<LockProcess> selectLockProcessCondition(LockProcess lockProcess, Page page) {
        if(lockProcess == null){
            log.error("锁操作信息为空");
            return null;
        }
        return selectLockProcessCondition(lockProcess,null,null, page);
    }
}
