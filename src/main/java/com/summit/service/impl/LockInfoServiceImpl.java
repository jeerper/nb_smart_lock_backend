package com.summit.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.summit.constants.CommonConstants;
import com.summit.dao.entity.LockInfo;
import com.summit.dao.entity.SimplePage;
import com.summit.dao.repository.LockInfoDao;
import com.summit.service.LockInfoService;
import com.summit.util.LockAuthCtrl;
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

    /**
     * 根据锁id查询唯一锁信息
     * @param lockId 锁id
     * @return 唯一锁信息对象
     */
    @Override
    public LockInfo selectLockById(String lockId) {
        List<String> rolesList = LockAuthCtrl.getRoles();
        return lockInfoDao.selectLockById(lockId, rolesList);
    }

    /**
     * 根据锁编号查询唯一锁信息
     * @param lockCode 锁编号
     * @return 唯一锁信息对象
     */
    @Override
    public LockInfo selectBylockCode(String lockCode) {
        List<String> rolesList = LockAuthCtrl.getRoles();
        return lockInfoDao.selectBylockCode(lockCode, rolesList);
    }

    /**
     * 分页查询全部锁信息
     * @param page 分页对象
     * @return 锁信息列表
     */
    @Override
    public List<LockInfo> selectAll(SimplePage page) {
        List<String> rolesList = LockAuthCtrl.getRoles();
        PageConverter.convertPage(page);
        return lockInfoDao.selectCondition(new LockInfo(), page, rolesList);
    }

    /**
     * 分页查询全部有操作记录的锁信息
     * @param page 分页对象
     * @return 锁信息列表
     */
    @Override
    public List<LockInfo> selectAllHaveHistory(SimplePage page) {
        List<String> rolesList = LockAuthCtrl.getRoles();
        PageConverter.convertPage(page);
        return lockInfoDao.selectAllHaveHistory(page, rolesList);
    }

    /**
     * 条件查询锁信息
     * @param lockInfo 锁信息对象
     * @param page 分页对象
     * @return 锁信息列表
     */
    @Override
    public List<LockInfo> selectCondition(LockInfo lockInfo, SimplePage page) {
        if(lockInfo == null){
            log.error("锁信息为空");
            return null;
        }
        List<String> rolesList = LockAuthCtrl.getRoles();
        PageConverter.convertPage(page);
        return lockInfoDao.selectCondition(lockInfo, page,rolesList);
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
            return CommonConstants.UPDATE_ERROR;
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
            return CommonConstants.UPDATE_ERROR;
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
            return CommonConstants.UPDATE_ERROR;
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
    public int delLockByLockCode(String lockCode) {
        if(lockCode == null){
            log.error("锁id为空");
            return CommonConstants.UPDATE_ERROR;
        }
        UpdateWrapper<LockInfo> wrapper = new UpdateWrapper<>();
        return lockInfoDao.delete(wrapper.eq("lock_code", lockCode));
    }
}
