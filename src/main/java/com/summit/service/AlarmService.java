package com.summit.service;

import com.summit.dao.entity.Alarm;

import java.util.Date;
import java.util.List;

public interface AlarmService {
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
}
