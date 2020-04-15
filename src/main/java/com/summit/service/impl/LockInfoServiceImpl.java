package com.summit.service.impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.summit.cbb.utils.page.Page;
import com.summit.cbb.utils.page.Pageable;
import com.summit.common.util.UserAuthUtils;
import com.summit.constants.CommonConstants;
import com.summit.dao.entity.LockInfo;
import com.summit.dao.entity.SimplePage;
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

    /**
     * 根据锁id查询唯一锁信息
     *
     * @param lockId 锁id
     * @return 唯一锁信息对象
     */
    @Override
    public LockInfo selectLockById(String lockId) {
        List<String> rolesList = UserAuthUtils.getRoles();
        return lockInfoDao.selectLockById(lockId, rolesList);
    }

    /**
     * 根据锁编号查询唯一锁信息
     *
     * @param lockCode 锁编号
     * @return 唯一锁信息对象
     */
    @Override
    public LockInfo selectBylockCode(String lockCode) {
        //List<String> rolesList = UserAuthUtils.getRoles();
        return lockInfoDao.selectBylockCode(lockCode, null);
    }

    /**
     * 分页查询全部锁信息
     *
     * @param page 分页对象
     * @return 锁信息列表
     */
    @Override
    public Page<LockInfo> selectLockInfoByPage(SimplePage page) {
        List<String> rolesList = UserAuthUtils.getRoles();
        List<LockInfo> infoList = lockInfoDao.selectCondition(new LockInfo(), null, rolesList);
        Integer rowsCount = infoList == null ? 0 : infoList.size();
        Pageable pageable = PageConverter.getPageable(page, rowsCount);
        PageConverter.convertPage(page);
        List<LockInfo> lockInfos = lockInfoDao.selectCondition(new LockInfo(), page, rolesList);
        Page<LockInfo> backPage = new Page<>(lockInfos, pageable);
        return backPage;
    }

    /**
     * 分页查询全部有操作记录的锁信息
     *
     * @param page 分页对象
     * @return 锁信息列表
     */
    @Override
    public Page<LockInfo> selectHaveHistoryByPage(SimplePage page) {
        List<String> rolesList = UserAuthUtils.getRoles();
        List<LockInfo> infoList = lockInfoDao.selectAllHaveHistory(null, rolesList);
        Integer rowsCount = infoList == null ? 0 : infoList.size();
        Pageable pageable = PageConverter.getPageable(page, rowsCount);
        PageConverter.convertPage(page);
        List<LockInfo> lockInfos = lockInfoDao.selectAllHaveHistory(page, rolesList);
        Page<LockInfo> backPage = new Page<>(lockInfos, pageable);
        return backPage;
    }

    /**
     * 条件查询锁信息
     *
     * @param lockInfo 锁信息对象
     * @param page     分页对象
     * @return 锁信息列表
     */
    @Override
    public Page<LockInfo> selectCondition(LockInfo lockInfo, SimplePage page) {
        if (lockInfo == null) {
            log.error("锁信息为空");
            return null;
        }
        List<String> rolesList = UserAuthUtils.getRoles();
        List<LockInfo> lockList = lockInfoDao.selectCondition(lockInfo, null, rolesList);
        Integer rowsCount = lockList == null ? 0 : lockList.size();
        Pageable pageable = PageConverter.getPageable(page, rowsCount);
        PageConverter.convertPage(page);
        List<LockInfo> lockInfos = lockInfoDao.selectCondition(lockInfo, page, rolesList);
        Page<LockInfo> backPage = new Page<>(lockInfos, pageable);
        return backPage;
    }

    /**
     * 插入锁信息
     *
     * @param lockInfo 锁信息对象
     * @return 返回不为-1则为成功
     */
    @Override
    public int insertLock(LockInfo lockInfo) {
        if (lockInfo == null) {
            log.error("锁信息为空");
            return CommonConstants.UPDATE_ERROR;
        }
        lockInfo.setCurrentPassword("123456");
        //随机生成6位新密码
        lockInfo.setNewPassword(RandomUtil.randomStringUpper(6));
        return lockInfoDao.insert(lockInfo);
    }

    /**
     * 更新锁信息
     *
     * @param lockInfo 锁信息对象
     * @return 返回不为-1则为成功
     */
    @Override
    public int updateLock(LockInfo lockInfo) {
        if (lockInfo == null) {
            log.error("锁信息为空");
            return CommonConstants.UPDATE_ERROR;
        }

        String lockCode = lockInfo.getLockCode();
        String lockId = lockInfo.getLockId();

        return lockInfoDao.update(lockInfo, Wrappers.<LockInfo>lambdaUpdate()
                .eq(StrUtil.isNotBlank(lockId), LockInfo::getLockId, lockId)
                .or()
                .eq(StrUtil.isNotBlank(lockCode), LockInfo::getLockCode, lockCode));
    }

    /**
     * 根据id删除锁信息
     *
     * @param lockId 锁id
     * @return 返回不为-1则为成功
     */
    @Override
    public int delLockByLockId(String lockId) {
        if (lockId == null) {
            log.error("锁id为空");
            return CommonConstants.UPDATE_ERROR;
        }
        UpdateWrapper<LockInfo> wrapper = new UpdateWrapper<>();
        return lockInfoDao.delete(wrapper.eq("lock_id", lockId));
    }

    /**
     * 根据锁编号删除锁信息
     *
     * @param lockCode 锁编号
     * @return 返回不为-1则为成功
     */
    @Override
    public int delLockByLockCode(String lockCode) {
        if (lockCode == null) {
            log.error("锁编号为空");
            return CommonConstants.UPDATE_ERROR;
        }
        UpdateWrapper<LockInfo> wrapper = new UpdateWrapper<>();
        return lockInfoDao.delete(wrapper.eq("lock_code", lockCode));
    }
}
