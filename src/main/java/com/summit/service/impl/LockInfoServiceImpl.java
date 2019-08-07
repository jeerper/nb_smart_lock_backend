package com.summit.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.summit.common.entity.UserInfo;
import com.summit.common.web.filter.UserContextHolder;
import com.summit.dao.entity.LockInfo;
import com.summit.dao.entity.Page;
import com.summit.dao.repository.LockInfoDao;
import com.summit.service.LockInfoService;
import com.summit.util.LockAuthCtrl;
import com.summit.util.PageConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
public class LockInfoServiceImpl implements LockInfoService {
    @Autowired
    private LockInfoDao lockInfoDao;

    @Override
    public LockInfo selectLockById(String lockId) {
        List<String> rolesList = LockAuthCtrl.getRoles();
        LockInfo lockInfo = lockInfoDao.selectLockById(lockId, rolesList);
        if(!LockAuthCtrl.toFilter(lockInfo)){
            return null;
        }
        return lockInfo;
    }


    @Override
    public LockInfo selectBylockCode(String lockCode) {
        List<String> rolesList = LockAuthCtrl.getRoles();
        LockInfo lockInfo = lockInfoDao.selectBylockCode(lockCode, rolesList);
        if(!LockAuthCtrl.toFilter(lockInfo)){
            return null;
        }
        return lockInfo;
    }

    @Override
    public List<LockInfo> selectAll(Page page) {
        List<String> rolesList = LockAuthCtrl.getRoles();
        PageConverter.convertPage(page);
        List<LockInfo> lockInfos = lockInfoDao.selectCondition(new LockInfo(), page, rolesList);
//        LockAuthCtrl.toFilterLocks(lockInfos);

        return lockInfos;
    }


    @Override
    public List<LockInfo> selectCondition(LockInfo lockInfo, Page page) {
        if(lockInfo == null){
            log.error("锁信息为空");
            return null;
        }
        List<String> rolesList = LockAuthCtrl.getRoles();
        PageConverter.convertPage(page);
        List<LockInfo> lockInfos = lockInfoDao.selectCondition(lockInfo, page,rolesList);
        LockAuthCtrl.toFilterLocks(lockInfos);
        return lockInfos;
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
