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

    /**
     * 根据锁id查询唯一锁信息
     * @param lockId 锁id
     * @return 唯一锁信息对象
     */
    @Override
    public LockInfo selectLockById(String lockId) {
        List<String> rolesList = LockAuthCtrl.getRoles();
        LockInfo lockInfo = lockInfoDao.selectLockById(lockId, rolesList);
        if(!LockAuthCtrl.toFilter(lockInfo)){
            return null;
        }
        return lockInfo;
    }

    /**
     * 根据锁编号查询唯一锁信息
     * @param lockCode 锁编号
     * @return 唯一锁信息对象
     */
    @Override
    public LockInfo selectBylockCode(String lockCode) {
        List<String> rolesList = LockAuthCtrl.getRoles();
        LockInfo lockInfo = lockInfoDao.selectBylockCode(lockCode, rolesList);
        if(!LockAuthCtrl.toFilter(lockInfo)){
            return null;
        }
        return lockInfo;
    }

    /**
     * 分页查询全部锁信息
     * @param page 分页对象
     * @return 锁信息列表
     */
    @Override
    public List<LockInfo> selectAll(Page page) {
        List<String> rolesList = LockAuthCtrl.getRoles();
        PageConverter.convertPage(page);
        List<LockInfo> lockInfos = lockInfoDao.selectCondition(new LockInfo(), page, rolesList);
//        LockAuthCtrl.toFilterLocks(lockInfos);

        return lockInfos;
    }

    /**
     * 分页查询全部有操作记录的锁信息
     * @param page 分页对象
     * @return 锁信息列表
     */
    @Override
    public List<LockInfo> selectAllHaveHistory(Page page) {
        List<String> rolesList = LockAuthCtrl.getRoles();
        PageConverter.convertPage(page);
        List<LockInfo> lockInfos = lockInfoDao.selectAllHaveHistory(page, rolesList);

        return lockInfos;
    }

    /**
     * 条件查询锁信息
     * @param lockInfo 锁信息对象
     * @param page 分页对象
     * @return 锁信息列表
     */
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

    /**
     * 插入锁信息
     * @param lockInfo 锁信息对象
     * @return 返回不为-1则为成功
     */
    @Override
    public int insertLock(LockInfo lockInfo) {
        if(lockInfo == null){
            log.error("锁信息为空");
            return -1;
        }
        return lockInfoDao.insert(lockInfo);
    }

    /**
     * 更新锁信息
     * @param lockInfo 锁信息对象
     * @return 返回不为-1则为成功
     */
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

    /**
     * 根据id删除锁信息
     * @param lockId 锁id
     * @return 返回不为-1则为成功
     */
    @Override
    public int delLockByLockId(String lockId) {
        if(lockId == null){
            log.error("锁id为空");
            return -1;
        }
        UpdateWrapper<LockInfo> wrapper = new UpdateWrapper<>();
        return lockInfoDao.delete(wrapper.eq("lock_id", lockId));
    }

    /**
     * 根据锁编号删除锁信息
     * @param lockCode 锁编号
     * @return 返回不为-1则为成功
     */
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
