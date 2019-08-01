package com.summit.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.summit.dao.entity.LockInfo;
import com.summit.dao.entity.Page;
import com.summit.dao.repository.LockInfoDao;
import com.summit.service.LockInfoService;
import com.summit.util.PageConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class LockInfoServiceImpl implements LockInfoService {
    @Autowired
    private LockInfoDao lockInfoDao;

    @Override
    public LockInfo selectLockById(String lockId) {
        return lockInfoDao.selectLockById(lockId);
    }

    @Override
    public LockInfo selectBylockCode(String lockCode) {
        return lockInfoDao.selectBylockCode(lockCode);
    }

    @Override
    public List<LockInfo> selectAll(Page page) {
        PageConverter.convertPage(page);
        return lockInfoDao.selectCondition(new LockInfo(),page);
    }

    @Override
    public List<LockInfo> selectCondition(LockInfo lockInfo, Page page) {
        if(lockInfo == null){
            log.error("锁信息为空");
            return null;
        }
        PageConverter.convertPage(page);
        return lockInfoDao.selectCondition(lockInfo,page);
    }

    @Override
    public int insertLock(LockInfo lockInfo) {
        if(lockInfo == null){
            log.error("锁信息为空");
            return -1;
        }
        return lockInfoDao.insert(lockInfo);
    }

    @Override
    public int updateLock(LockInfo lockInfo) {
        if(lockInfo == null){
            log.error("锁信息为空");
            return -1;
        }
        String lockCode = lockInfo.getLockCode();
        String lockId = lockInfo.getLockId();
        if(lockCode != null && lockId != null){
            lockCode = null;
        }
        UpdateWrapper<LockInfo> updateWrapper = new UpdateWrapper<>();

        return lockInfoDao.update(lockInfo, updateWrapper.eq("lock_id", lockId)
                .or().eq("lock_code", lockCode));
    }

    @Override
    public int delLockByLockId(String lockId) {
        if(lockId == null){
            log.error("锁id为空");
            return -1;
        }
        UpdateWrapper<LockInfo> wrapper = new UpdateWrapper<>();
        return lockInfoDao.delete(wrapper.eq("lock_id", lockId));
    }

    @Override
    public int delLockByLockCod(String lockCode) {
        if(lockCode == null){
            log.error("锁id为空");
            return -1;
        }
        UpdateWrapper<LockInfo> wrapper = new UpdateWrapper<>();
        return lockInfoDao.delete(wrapper.eq("lock_code", lockCode));
    }
}
