package com.summit.service;

import com.summit.dao.entity.Alarm;
import com.summit.dao.entity.LockProcess;
import com.summit.dao.entity.Page;

import java.util.Date;
import java.util.List;

public interface LockRecordService {
    //###锁操作记录插入
    int insertLockProcess(LockProcess lockProcess);
    //###锁操作记录更新
    int updateLockProcess(LockProcess lockProcess);
    //###锁操作记录删除
    int delLockProcess(String processId);

    //###锁操作记录查询
    //查询所有
    List<LockProcess> selectAll(Page page);
    //根据Id查询
    LockProcess selectLockProcessById(String processId);
    //根据锁编号查询，可指定时间段
    List<LockProcess> selectLockProcessByLockCode(String lockCode, Date start, Date end, Page page);
    //不带时间重载
    List<LockProcess> selectLockProcessByLockCode(String lockCode, Page page);
    //根据设备ip地址查询，可指定时间段
    List<LockProcess> selectLockProcessByDeviceIp(String deviceIp, Date start, Date end, Page page);
    //不带时间重载
    List<LockProcess> selectLockProcessByDeviceIp(String deviceIp, Page page);
    //根据锁操作记录对应的设备id查询，可指定时间段
    List<LockProcess> selectLockProcessByDevId(String devId, Date start, Date end, Page page);
    //不带时间重载
    List<LockProcess> selectLockProcessByDevId(String devId, Page page);
    //根据开锁人查询，可指定时间段
    List<LockProcess> selectLockProcessByUserName(String userName, Date start, Date end, Page page);
    //不带时间重载
    List<LockProcess> selectLockProcessByUserName(String userName, Page page);
    //根据操作类型查询（开锁或关锁），可指定时间段
    List<LockProcess> selectLockProcessByType(Integer processType, Date start, Date end, Page page);
    //不带时间重载
    List<LockProcess> selectLockProcessByType(Integer processType, Page page);
    //根据操作结果类型查询（成功或失败），可指定时间段
    List<LockProcess> selectLockProcessByResult(String processResult, Date start, Date end, Page page);
    //不带时间重载
    List<LockProcess> selectLockProcessByResult(String processResult, Page page);
    //指定条件查询，可指定时间段
    List<LockProcess> selectLockProcessCondition(LockProcess lockProcess, Date start, Date end, Page page);
    //指定条件查询，不带日期的重载
    List<LockProcess> selectLockProcessCondition(LockProcess lockProcess, Page page);
}
