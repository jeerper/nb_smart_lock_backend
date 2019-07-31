package com.summit.service;

import com.summit.dao.entity.Alarm;
import com.summit.dao.entity.LockProcess;
import com.summit.dao.entity.Page;
import com.summit.dao.entity.SafeReport;

import java.util.Date;
import java.util.List;

public interface LockDataService {

    //###告警插入
    int insertAlarm(Alarm alarm);
    //###告警更新
    int updateAlarm(Alarm alarm);
    //###告警删除
    int delLockAlarm(Alarm alarm);

    //###告警查询
    //根据告警Id查询告警
    Alarm selectAlarmById(String alarmId);
    //根据告警name（或者说类型）查询，用selectCondition实现，可指定时间段
    List<Alarm> selectAlarmByName(String alarmName, Date start, Date end);
    //不带时间重载
    List<Alarm> selectAlarmByName(String alarmName);
    //根据告警状态查询，用selectCondition实现，可指定时间段
    List<Alarm> selectAlarmByStatus(Integer alarmStatus, Date start, Date end);
    //不带时间重载
    List<Alarm> selectAlarmByStatus(Integer alarmStatus);
    //根据锁操作记录对应的锁编号查询告警，可指定时间段
    List<Alarm> selectAlarmByLockCode(String lockCode, Date start, Date end);
    //不带时间重载
    List<Alarm> selectAlarmByLockCode(String lockCode);
    //根据锁操作记录对应的设备ip地址查询告警，可指定时间段
    List<Alarm>  selectAlarmByDeviceIp(String deviceIp, Date start, Date end);
    //不带时间重载
    List<Alarm>  selectAlarmByDeviceIp(String deviceIp);
    //根据锁操作记录对应的设备id查询告警，可指定时间段
    List<Alarm> selectAlarmByDevId(String devId, Date start, Date end);
    //不带时间重载
    List<Alarm> selectAlarmByDevId(String devId);
    //指定条件查询告警，可指定时间段
    List<Alarm> selectAlarmCondition(Alarm alarm, Date start, Date end);
    //指定条件查询不带日期的重载
    List<Alarm> selectAlarmCondition(Alarm alarm);



    //###锁操作记录插入
    int insertLockProcess(LockProcess lockProcess);
    //###锁操作记录更新
    int updateLockProcess(LockProcess lockProcess);
    //###锁操作记录删除
    int delLockProcess(LockProcess lockProcess);

    //###锁操作记录查询
    //查询所有
    public List<Alarm> selectAll(Page page);
    //根据Id查询
    LockProcess selectLockProcessById(String processId);
    //根据锁编号查询，可指定时间段
    List<LockProcess> selectLockProcessByLockCode(String lockCode, Date start, Date end);
    //不带时间重载
    List<LockProcess> selectLockProcessByLockCode(String lockCode);
    //根据设备ip地址查询，可指定时间段
    List<LockProcess> selectLockProcessByDeviceIp(String deviceIp, Date start, Date end);
    //不带时间重载
    List<LockProcess> selectLockProcessByDeviceIp(String deviceIp);
    //根据锁操作记录对应的设备id查询，可指定时间段
    List<LockProcess> selectLockProcessByDevId(String devId, Date start, Date end);
    //不带时间重载
    List<LockProcess> selectLockProcessByDevId(String devId);
    //根据开锁人查询，可指定时间段
    List<LockProcess> selectLockProcessByUserName(String userName, Date start, Date end);
    //不带时间重载
    List<LockProcess> selectLockProcessByUserName(String userName);
    //根据操作类型查询（开锁或关锁），可指定时间段
    List<LockProcess> selectLockProcessByType(Integer processType, Date start, Date end);
    //不带时间重载
    List<LockProcess> selectLockProcessByType(Integer processType);
    //根据操作结果类型查询（成功或失败），可指定时间段
    List<LockProcess> selectLockProcessByResult(String processResult, Date start, Date end);
    //不带时间重载
    List<LockProcess> selectLockProcessByResult(String processResult);
    //指定条件查询，可指定时间段
    List<LockProcess> selectLockProcessCondition(LockProcess lockProcess, Date start, Date end);
    //指定条件查询，不带日期的重载
    List<LockProcess> selectLockProcessCondition(LockProcess lockProcess);


    //###平安报插入
    int insertSafeReport(SafeReport safeReport);

    //###平安报更新
    int updateSafeReport(SafeReport safeReport);

    //###平安报删除
    int delSafeReport(SafeReport safeReport);

    //###平安报查询
    //根据Id查询
    SafeReport selectReportById(String safeRepId);
    //根据锁编号查询，可指定时间段
    List<SafeReport> selectReportByLockCode(String lockCode, Date start, Date end);
    //不带时间重载
    List<SafeReport> selectReportByLockCode(String lockCode);
    //指定条件查询，可指定时间段
    List<SafeReport> selectReportCondition(SafeReport safeReport, Date start, Date end);
    //指定条件查询，不带日期的重载
    List<SafeReport> selectReportCondition(SafeReport safeReport);


}
