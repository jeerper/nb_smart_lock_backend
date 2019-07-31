package com.summit.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.summit.dao.entity.Alarm;
import com.summit.dao.entity.CameraDevice;
import com.summit.dao.entity.FileInfo;
import com.summit.dao.entity.LockProcess;
import com.summit.dao.entity.Page;
import com.summit.dao.repository.AlarmDao;
import com.summit.dao.repository.CameraDeviceDao;
import com.summit.dao.repository.FileInfoDao;
import com.summit.dao.repository.LockProcessDao;
import com.summit.service.LockRecordService;
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

    @Override
    public int insertLockProcess(LockProcess lockProcess) {

        int result = lockProcessDao.insertRecord(lockProcess);
        if(result != -1){
            fileInfoDao.insert(lockProcess.getFacePanorama());
            fileInfoDao.insert(lockProcess.getFacePic());
            fileInfoDao.insert(lockProcess.getFaceMatch());
        }
        return result;
    }

    @Override
    public int updateLockProcess(LockProcess lockProcess) {
        int result = lockProcessDao.updateRecord(lockProcess);
        if(result != -1){
            UpdateWrapper<FileInfo> updateWrapper = new UpdateWrapper<>();
            fileInfoDao.update(lockProcess.getFacePanorama(),updateWrapper.eq("file_id",lockProcess.getFacePanorama().getFileId()));
            fileInfoDao.update(lockProcess.getFacePic(),updateWrapper.eq("file_id",lockProcess.getFacePic().getFileId()));
            fileInfoDao.update(lockProcess.getFaceMatch(),updateWrapper.eq("file_id",lockProcess.getFaceMatch().getFileId()));
        }
        return result;
    }

    @Override
    public int delLockProcess(String processId) {
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
        return lockProcessDao.selectCondition(new LockProcess(),null,null ,page);
    }

    @Override
    public LockProcess selectLockProcessById(String processId) {
        return lockProcessDao.selectLockProcessById(processId);
    }

    @Override
    public List<LockProcess> selectLockProcessByLockCode(String lockCode, Date start, Date end, Page page) {
        LockProcess lockProcess = new LockProcess();
        lockProcess.setLockCode(lockCode);
        return lockProcessDao.selectCondition(lockProcess, start, end, page);
    }

    @Override
    public List<LockProcess> selectLockProcessByLockCode(String lockCode, Page page) {
        return lockProcessDao.selectByLockCode(lockCode, page);
    }

    @Override
    public List<LockProcess> selectLockProcessByDeviceIp(String deviceIp, Date start, Date end, Page page) {
        LockProcess lockProcess = new LockProcess();
        lockProcess.setDeviceIp(deviceIp);
        return lockProcessDao.selectCondition(lockProcess, start, end, page);
    }

    @Override
    public List<LockProcess> selectLockProcessByDeviceIp(String deviceIp, Page page) {
        return selectLockProcessByDeviceIp(deviceIp,null,null, page);
    }

    @Override
    public List<LockProcess> selectLockProcessByDevId(String devId, Date start, Date end, Page page) {
        CameraDevice device = deviceDao.selectDeviceById(devId);
        LockProcess lp = new LockProcess();
        lp.setDeviceIp(device.getDeviceIp());
        return lockProcessDao.selectCondition(lp, start, end, page);
    }

    @Override
    public List<LockProcess> selectLockProcessByDevId(String devId, Page page) {
        return selectLockProcessByDevId(devId,null,null, page);
    }

    @Override
    public List<LockProcess> selectLockProcessByUserName(String userName, Date start, Date end, Page page) {
        LockProcess lockProcess = new LockProcess();
        lockProcess.setUserName(userName);
        return lockProcessDao.selectCondition(lockProcess, start, end, page);
    }

    @Override
    public List<LockProcess> selectLockProcessByUserName(String userName, Page page) {
        return selectLockProcessByUserName(userName,null,null, page);
    }

    @Override
    public List<LockProcess> selectLockProcessByType(Integer processType, Date start, Date end, Page page) {
        LockProcess lockProcess = new LockProcess();
        lockProcess.setProcessType(processType);
        return lockProcessDao.selectCondition(lockProcess, start, end, page);
    }

    @Override
    public List<LockProcess> selectLockProcessByType(Integer processType, Page page) {
        return selectLockProcessByType(processType,null,null, page);
    }

    @Override
    public List<LockProcess> selectLockProcessByResult(String processResult, Date start, Date end, Page page) {
        LockProcess lockProcess = new LockProcess();
        lockProcess.setProcessResult(processResult);
        return lockProcessDao.selectCondition(lockProcess, start, end, page);
    }

    @Override
    public List<LockProcess> selectLockProcessByResult(String processResult, Page page) {
        return selectLockProcessByResult(processResult,null,null, page);
    }

    @Override
    public List<LockProcess> selectLockProcessCondition(LockProcess lockProcess, Date start, Date end, Page page) {
        return lockProcessDao.selectCondition(lockProcess,start,end, page);
    }

    @Override
    public List<LockProcess> selectLockProcessCondition(LockProcess lockProcess, Page page) {
        return selectLockProcessCondition(lockProcess,null,null, page);
    }
}
