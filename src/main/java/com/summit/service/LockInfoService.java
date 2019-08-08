package com.summit.service;

import com.summit.dao.entity.LockInfo;
import com.summit.dao.entity.Page;

import java.util.List;

/**
 * 锁信息service接口
 */
public interface LockInfoService {

    /**
     * 根据锁id查询唯一锁信息
     * @param lockId 锁id
     * @return 唯一锁信息对象
     */
    LockInfo selectLockById(String lockId);

    /**
     * 根据锁编号查询唯一锁信息
     * @param lockCode 锁编号
     * @return 唯一锁信息对象
     */
    LockInfo selectBylockCode(String lockCode);

    /**
     * 分页查询全部锁信息
     * @param page 分页对象
     * @return 锁信息列表
     */
    List<LockInfo> selectAll(Page page);

    /**
     * 分页查询全部有操作记录的锁信息
     * @param page 分页对象
     * @return 锁信息列表
     */
    List<LockInfo> selectAllHaveHistory(Page page);

    /**
     * 条件查询锁信息
     * @param lockInfo 锁信息对象
     * @param page 分页对象
     * @return 锁信息列表
     */
    List<LockInfo> selectCondition(LockInfo lockInfo, Page page);

    /**
     * 插入锁信息
     * @param lockInfo 锁信息对象
     * @return 返回不为-1则为成功
     */
    int insertLock(LockInfo lockInfo);

    /**
     * 更新锁信息
     * @param lockInfo 锁信息对象
     * @return 返回不为-1则为成功
     */
    int updateLock(LockInfo lockInfo);

    /**
     * 根据id删除锁信息
     * @param lockId 锁id
     * @return 返回不为-1则为成功
     */
    int delLockByLockId(String lockId);

    /**
     * 根据锁编号删除锁信息
     * @param lockCode 锁编号
     * @return 返回不为-1则为成功
     */
    int delLockByLockCod(String lockCode);

}
