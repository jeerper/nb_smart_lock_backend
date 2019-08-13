package com.summit.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.summit.cbb.utils.page.Page;
import com.summit.cbb.utils.page.Pageable;
import com.summit.constants.CommonConstants;
import com.summit.dao.entity.AccessControlInfo;
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
    public Page<LockInfo> selectLockInfoByPage(SimplePage page) {
        List<String> rolesList = LockAuthCtrl.getRoles();
        List<LockInfo> infoList = lockInfoDao.selectCondition(new LockInfo(), null, rolesList);
        Integer rowsCount = infoList == null ? 0 : infoList.size();
        Pageable pageable = PageConverter.getPageable(page, rowsCount);
        PageConverter.convertPage(page);
        List<LockInfo> lockInfos = lockInfoDao.selectCondition(new LockInfo(), page, rolesList);
        Page<LockInfo> backPage = new Page<>();
        backPage.setContent(lockInfos);
        backPage.setPageable(pageable);
        return backPage;
    }

    /**
     * 分页查询全部有操作记录的锁信息
     * @param page 分页对象
     * @return 锁信息列表
     */
    @Override
    public Page<LockInfo> selectHaveHistoryByPage(SimplePage page) {
        List<String> rolesList = LockAuthCtrl.getRoles();
        List<LockInfo> infoList = lockInfoDao.selectAllHaveHistory(null, rolesList);
        Integer rowsCount = infoList == null ? 0 : infoList.size();
        Pageable pageable = PageConverter.getPageable(page,rowsCount);
        PageConverter.convertPage(page);
        List<LockInfo> lockInfos = lockInfoDao.selectAllHaveHistory(page, rolesList);
        Page<LockInfo> backPage = new Page<>();
        backPage.setContent(lockInfos);
        backPage.setPageable(pageable);
        return backPage;
    }

    /**
     * 条件查询锁信息
     * @param lockInfo 锁信息对象
     * @param page 分页对象
     * @return 锁信息列表
     */
    @Override
    public Page<LockInfo> selectCondition(LockInfo lockInfo, SimplePage page) {
        if(lockInfo == null){
            log.error("锁信息为空");
            return null;
        }
        List<String> rolesList = LockAuthCtrl.getRoles();
        List<LockInfo> lockList = lockInfoDao.selectCondition(lockInfo, null, rolesList);
        Integer rowsCount = lockList == null ? 0 : lockList.size();
        Pageable pageable = PageConverter.getPageable(page, rowsCount);
        PageConverter.convertPage(page);
        List<LockInfo> lockInfos = lockInfoDao.selectCondition(lockInfo, page, rolesList);
        Page<LockInfo> backPage = new Page<>();
        backPage.setContent(lockInfos);
        backPage.setPageable(pageable);
        return backPage;
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
            log.error("锁编号为空");
            return CommonConstants.UPDATE_ERROR;
        }
        UpdateWrapper<LockInfo> wrapper = new UpdateWrapper<>();
        return lockInfoDao.delete(wrapper.eq("lock_code", lockCode));
    }
}
