package com.summit.service.impl;

import com.summit.dao.entity.LockProcess;
import com.summit.dao.repository.LockProcessDao;
import com.summit.service.LockRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class LockRecordServiceImpl implements LockRecordService {
    @Autowired
    private LockProcessDao lockProcessDao;

    @Override
    public int insertLockProcess(LockProcess lockProcess) {
        return 0;
    }

    @Override
    public int updateLockProcess(LockProcess lockProcess) {
        return 0;
    }

    @Override
    public int delLockProcess(LockProcess lockProcess) {
        return 0;
    }

    @Override
    public LockProcess selectLockProcessById(String processId) {
        return null;
    }

    @Override
    public List<LockProcess> selectLockProcessByLockCode(String lockCode, Date start, Date end) {
        return null;
    }

    @Override
    public List<LockProcess> selectLockProcessByLockCode(String lockCode) {
        return null;
    }

    @Override
    public List<LockProcess> selectLockProcessByDeviceIp(String deviceIp, Date start, Date end) {
        return null;
    }

    @Override
    public List<LockProcess> selectLockProcessByDeviceIp(String deviceIp) {
        return null;
    }

    @Override
    public List<LockProcess> selectLockProcessByDevId(String devId, Date start, Date end) {
        return null;
    }

    @Override
    public List<LockProcess> selectLockProcessByDevId(String devId) {
        return null;
    }

    @Override
    public List<LockProcess> selectLockProcessByUserName(String userName, Date start, Date end) {
        return null;
    }

    @Override
    public List<LockProcess> selectLockProcessByUserName(String userName) {
        return null;
    }

    @Override
    public List<LockProcess> selectLockProcessByType(Integer processType, Date start, Date end) {
        return null;
    }

    @Override
    public List<LockProcess> selectLockProcessByType(Integer processType) {
        return null;
    }

    @Override
    public List<LockProcess> selectLockProcessByResult(String processResult, Date start, Date end) {
        return null;
    }

    @Override
    public List<LockProcess> selectLockProcessByResult(String processResult) {
        return null;
    }

    @Override
    public List<LockProcess> selectLockProcessCondition(LockProcess lockProcess, Date start, Date end) {
        return null;
    }

    @Override
    public List<LockProcess> selectLockProcessCondition(LockProcess lockProcess) {
        return null;
    }
}
