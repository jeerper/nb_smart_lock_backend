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

    @Override
    public int insertLockProcess(LockProcess lockProcess) {
        if(lockProcess == null){
            log.error("锁操作信息为空");
            return -1;
        }
        int result = lockProcessDao.insert(lockProcess);
        LockInfo lockInfo = lockProcess.getLockInfo();
        if(lockInfo != null){
            lockInfoService.updateLock(lockInfo);
        }
        if(result != -1){

            FileInfo facePanorama = lockProcess.getFacePanorama();
            if(facePanorama != null){
                fileInfoDao.insert(facePanorama);
            }
            FileInfo facePic = lockProcess.getFacePic();
            if(facePic != null) {
                fileInfoDao.insert(facePic);
            }
            FileInfo faceMatch = lockProcess.getFaceMatch();
            if(faceMatch != null) {
                fileInfoDao.insert(faceMatch);
            }
        }
        return result;
    }

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

    @Override
    public int delLockProcess(String processId) {
        if(processId == null){
            log.error("锁操作信息id为空");
            return -1;
        }
        LockProcess lockProcess = lockProcessDao.selectLockProcessById(processId);
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

    @Override
    public List<LockProcess> selectAll(Page page) {
        PageConverter.convertPage(page);
        List<LockProcess> lockProcesses = lockProcessDao.selectCondition(new LockProcess(), null, null, page);


        lockProcesses = getLockProcesses(page, lockProcesses);

        LockAuthCtrl.toFilterLockProcesses(lockProcesses);
        return lockProcesses;
    }

    private List<LockProcess> getLockProcesses(Page page, List<LockProcess> lockProcesses) {
        if(page != null && lockProcesses != null){
            Integer current = page.getCurrent();
            Integer pageSize = page.getPageSize();
            if(current >= lockProcesses.size()){
                lockProcesses = null;
            }else if(current + pageSize >= lockProcesses.size()){
                lockProcesses = lockProcesses.subList(current, lockProcesses.size());
            }else{
                lockProcesses = lockProcesses.subList(current,current + pageSize);
            }
        }
        return lockProcesses;
    }

    @Override
    public LockProcess selectLockProcessById(String processId) {
        if(processId == null){
            log.error("锁操作信息id为空");
            return null;
        }
        LockProcess lockProcess = lockProcessDao.selectLockProcessById(processId);
        if(!LockAuthCtrl.toFilter(lockProcess)){
            return null;
        }
        return lockProcess;
    }

    @Override
    public List<LockProcess> selectLockProcessByLockCode(String lockCode, Date start, Date end, Page page) {
        if(lockCode == null){
            log.error("锁编号为空");
            return null;
        }
        PageConverter.convertPage(page);
        LockProcess lockProcess = new LockProcess();
        lockProcess.setLockCode(lockCode);
        List<LockProcess> lockProcesses = lockProcessDao.selectCondition(lockProcess, start, end, page);
        lockProcesses = getLockProcesses(page, lockProcesses);
        LockAuthCtrl.toFilterLockProcesses(lockProcesses);
        return lockProcesses;
    }

    @Override
    public List<LockProcess> selectLockProcessByLockCode(String lockCode, Page page) {
        if(lockCode == null){
            log.error("锁编号为空");
            return null;
        }
        PageConverter.convertPage(page);
        List<LockProcess> lockProcesses = lockProcessDao.selectByLockCode(lockCode, page);
        lockProcesses = getLockProcesses(page, lockProcesses);
        LockAuthCtrl.toFilterLockProcesses(lockProcesses);
        return lockProcesses;
    }

    @Override
    public List<LockProcess> selectLockProcessByDeviceIp(String deviceIp, Date start, Date end, Page page) {
        if(deviceIp == null){
            log.error("设备ip地址为空");
            return null;
        }
        PageConverter.convertPage(page);
        LockProcess lockProcess = new LockProcess();
        lockProcess.setDeviceIp(deviceIp);
        List<LockProcess> lockProcesses = lockProcessDao.selectCondition(lockProcess, start, end, page);
        lockProcesses = getLockProcesses(page, lockProcesses);
        LockAuthCtrl.toFilterLockProcesses(lockProcesses);
        return lockProcesses;
    }

    @Override
    public List<LockProcess> selectLockProcessByDeviceIp(String deviceIp, Page page) {
        if(deviceIp == null){
            log.error("设备ip地址为空");
            return null;
        }
        return selectLockProcessByDeviceIp(deviceIp,null,null, page);
    }

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
        List<LockProcess> lockProcesses = lockProcessDao.selectCondition(lp, start, end, page);
        lockProcesses = getLockProcesses(page, lockProcesses);
        LockAuthCtrl.toFilterLockProcesses(lockProcesses);
        return lockProcesses;
    }

    @Override
    public List<LockProcess> selectLockProcessByDevId(String devId, Page page) {
        if(devId == null){
            log.error("设备id为空");
            return null;
        }
        return selectLockProcessByDevId(devId,null,null, page);
    }

    @Override
    public List<LockProcess> selectLockProcessByUserName(String userName, Date start, Date end, Page page) {
        if(userName == null){
            log.error("操作人为空");
            return null;
        }
        PageConverter.convertPage(page);
        LockProcess lockProcess = new LockProcess();
        lockProcess.setUserName(userName);
        List<LockProcess> lockProcesses = lockProcessDao.selectCondition(lockProcess, start, end, page);
        lockProcesses = getLockProcesses(page, lockProcesses);
        LockAuthCtrl.toFilterLockProcesses(lockProcesses);
        return lockProcesses;
    }

    @Override
    public List<LockProcess> selectLockProcessByUserName(String userName, Page page) {
        if(userName == null){
            log.error("操作人为空");
            return null;
        }
        return selectLockProcessByUserName(userName,null,null, page);
    }

    @Override
    public List<LockProcess> selectLockProcessByType(Integer processType, Date start, Date end, Page page) {
        if(processType == null){
            log.error("操作类型为空");
            return null;
        }
        PageConverter.convertPage(page);
        LockProcess lockProcess = new LockProcess();
        lockProcess.setProcessType(processType);
        List<LockProcess> lockProcesses = lockProcessDao.selectCondition(lockProcess, start, end, page);
        lockProcesses = getLockProcesses(page, lockProcesses);
        LockAuthCtrl.toFilterLockProcesses(lockProcesses);
        return lockProcesses;
    }

    @Override
    public List<LockProcess> selectLockProcessByType(Integer processType, Page page) {
        if(processType == null){
            log.error("操作类型为空");
            return null;
        }
        return selectLockProcessByType(processType,null,null, page);
    }

    @Override
    public List<LockProcess> selectLockProcessByResult(String processResult, Date start, Date end, Page page) {
        if(processResult == null){
            log.error("操作结果为空");
            return null;
        }
        PageConverter.convertPage(page);
        LockProcess lockProcess = new LockProcess();
        lockProcess.setProcessResult(processResult);
        List<LockProcess> lockProcesses = lockProcessDao.selectCondition(lockProcess, start, end, page);
        lockProcesses = getLockProcesses(page, lockProcesses);
        LockAuthCtrl.toFilterLockProcesses(lockProcesses);
        return lockProcesses;
    }

    @Override
    public List<LockProcess> selectLockProcessByResult(String processResult, Page page) {
        if(processResult == null){
            log.error("操作结果为空");
            return null;
        }
        return selectLockProcessByResult(processResult,null,null, page);
    }

    @Override
    public List<LockProcess> selectLockProcessCondition(LockProcess lockProcess, Date start, Date end, Page page) {
        if(lockProcess == null){
            log.error("锁操作信息为空");
            return null;
        }
        PageConverter.convertPage(page);
        List<LockProcess> lockProcesses = lockProcessDao.selectCondition(lockProcess, start, end, page);
        lockProcesses = getLockProcesses(page, lockProcesses);
        LockAuthCtrl.toFilterLockProcesses(lockProcesses);
        return lockProcesses;
    }

    @Override
    public List<LockProcess> selectLockProcessCondition(LockProcess lockProcess, Page page) {
        if(lockProcess == null){
            log.error("锁操作信息为空");
            return null;
        }
        return selectLockProcessCondition(lockProcess,null,null, page);
    }
}
