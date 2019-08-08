package com.summit.service;

import com.summit.dao.entity.LockProcess;
import com.summit.dao.entity.Page;

import java.util.Date;
import java.util.List;

/**
 * 锁操作记录service接口
 */
public interface LockRecordService {

    /**
     * 锁操作记录插入
     * @param lockProcess 锁操作记录
     * @return 不为-1则成功
     */
    int insertLockProcess(LockProcess lockProcess);

    /**
     * 锁操作记录更新
     * @param lockProcess 锁操作记录
     * @return 不为-1则成功
     */
    int updateLockProcess(LockProcess lockProcess);

    /**
     * 锁操作记录删除
     * @param processId 锁操作记录id
     * @return 不为-1则成功
     */
    int delLockProcess(String processId);


    //###锁操作记录查询------------------------------

    /**
     * 查询所有锁操作记录
     * @param page 分页对象
     * @return 锁操作记录列表
     */
    List<LockProcess> selectAll(Page page);

    /**
     * 根据Id查询
     * @param processId 锁操作记录id
     * @return 唯一确定锁操作记录
     */
    LockProcess selectLockProcessById(String processId);

    /**
     * 根据锁编号查询，可指定时间段
     * @param lockCode 锁编号
     * @param start 开始时间
     * @param end 截止时间
     * @param page 分页对象
     * @return 锁操作记录列表
     */
    List<LockProcess> selectLockProcessByLockCode(String lockCode, Date start, Date end, Page page);

    /**
     * 根据锁编号查询，不带时间重载
     * @param lockCode 锁编号
     * @param page 分页对象
     * @return 锁操作记录列表
     */
    List<LockProcess> selectLockProcessByLockCode(String lockCode, Page page);

    /**
     * 根据设备ip地址查询，可指定时间段
     * @param deviceIp 摄像头ip地址
     * @param start 开始时间
     * @param end 截止时间
     * @param page 分页对象
     * @return 锁操作记录列表
     */
    List<LockProcess> selectLockProcessByDeviceIp(String deviceIp, Date start, Date end, Page page);

    /**
     * 根据设备ip地址查询，不带时间重载
     * @param deviceIp 摄像头ip地址
     * @param page 分页对象
     * @return 锁操作记录列表
     */
    List<LockProcess> selectLockProcessByDeviceIp(String deviceIp, Page page);

    /**
     * 根据锁操作记录对应的设备id查询，可指定时间段
     * @param devId 摄像头id
     * @param start 开始时间
     * @param end 截止时间
     * @param page 分页对象
     * @return 锁操作记录列表
     */
    List<LockProcess> selectLockProcessByDevId(String devId, Date start, Date end, Page page);

    /**
     * 根据锁操作记录对应的设备id查询，不带时间重载
     * @param devId 摄像头id
     * @param page 分页对象
     * @return 锁操作记录列表
     */
    List<LockProcess> selectLockProcessByDevId(String devId, Page page);

    /**
     * 根据操作人名称查询，可指定时间段
     * @param userName 操作人名称
     * @param start 开始时间
     * @param end 截止时间
     * @param page 分页对象
     * @return 锁操作记录列表
     */
    List<LockProcess> selectLockProcessByUserName(String userName, Date start, Date end, Page page);

    /**
     * 根据操作人名称查询，不带时间重载
     * @param userName 操作人名称
     * @param page 分页对象
     * @return 锁操作记录列表
     */
    List<LockProcess> selectLockProcessByUserName(String userName, Page page);

    /**
     * 根据操作类型查询（开锁或关锁），可指定时间段
     * @param processType 锁操作类型（开锁或关锁）
     * @param start 开始时间
     * @param end 截止时间
     * @param page 分页对象
     * @return 锁操作记录列表
     */
    List<LockProcess> selectLockProcessByType(Integer processType, Date start, Date end, Page page);

    /**
     * 根据操作类型查询（开锁或关锁），不带时间重载
     * @param processType 锁操作类型（开锁或关锁）
     * @param page 分页对象
     * @return 锁操作记录列表
     */
    List<LockProcess> selectLockProcessByType(Integer processType, Page page);

    /**
     * 根据操作结果类型查询（成功或失败），可指定时间段
     * @param processResult 操作结果
     * @param start 开始时间
     * @param end 截止时间
     * @param page 分页对象
     * @return 锁操作记录列表
     */
    List<LockProcess> selectLockProcessByResult(String processResult, Date start, Date end, Page page);

    /**
     * 根据操作结果类型查询（成功或失败），不带时间重载
     * @param processResult 操作结果
     * @param page 分页对象
     * @return 锁操作记录列表
     */
    List<LockProcess> selectLockProcessByResult(String processResult, Page page);

    /**
     * 指定条件查询，可指定时间段
     * @param lockProcess 锁操作记录对象
     * @param start 开始时间
     * @param end 截止时间
     * @param page 分页对象
     * @return 锁操作记录列表
     */
    List<LockProcess> selectLockProcessCondition(LockProcess lockProcess, Date start, Date end, Page page);

    /**
     * 指定条件查询，不带日期的重载
     * @param lockProcess 锁操作记录对象
     * @param page 分页对象
     * @return 锁操作记录列表
     */
    List<LockProcess> selectLockProcessCondition(LockProcess lockProcess, Page page);
}
