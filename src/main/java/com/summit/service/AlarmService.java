package com.summit.service;

import com.summit.cbb.utils.page.Page;
import com.summit.dao.entity.Alarm;
import com.summit.dao.entity.SimplePage;

import java.util.Date;
import java.util.List;

/**
 * 告警service接口
 */
public interface AlarmService {

    /**
     * 告警插入
     * @param alarm 告警对象
     * @return 不为-1则成功
     */
    int insertAlarm(Alarm alarm);

    /**
     * 告警更新
     * @param alarm 告警对象
     * @return 不为-1则成功
     */
    int updateAlarm(Alarm alarm);

    /**
     * 告警删除
     * @param alarmId 告警id
     * @return 不为-1则成功
     */
    int delLockAlarmById(String alarmId);

    /**
     * 告警批量删除
     * @param alarmIds 告警id列表
     * @return 不为-1则成功
     */
    int delAlarmByIdBatch(List<String> alarmIds);


    //###告警查询------------------------
    /**
     * 查询所有
     * @param start 开始时间
     * @param end 截止时间
     * @return 告警记录列表
     */
    List<Alarm> selectAll(Date start, Date end,Integer current, Integer pageSize);

    /**
     * 根据告警Id查询唯一告警记录
     * @param alarmId 告警id
     * @return 唯一确定的告警记录
     */
    Alarm selectAlarmById(String alarmId);

    /**
     * 根据告警Idid查询唯一门禁信息,不考虑权限
     * @param alarmId 告警id
     * @return 唯一告警对象
     */
    Alarm selectAlarmByIdBeyondAuthority(String alarmId);

    /**
     * 根据告警name（或者说类型）查询，用selectCondition实现，可指定时间段
     * @param alarmName 告警名
     * @param start 开始时间
     * @param end 截止时间
     * @return 告警记录列表
     */
    List<Alarm> selectAlarmByName(String alarmName, Date start, Date end, Integer current, Integer pageSize);

    /**
     * 根据告警name（或者说类型）查询，用selectCondition实现，不带时间重载
     * @param alarmName 告警名
     * @return 告警记录列表
     */
    List<Alarm> selectAlarmByName(String alarmName,Integer current, Integer pageSize);

    /**
     * 根据告警状态查询，用selectCondition实现，可指定时间段
     * @param alarmStatus 告警状态
     * @param start 开始时间
     * @param end 截止时间
     * @return 告警记录列表
     */
    List<Alarm> selectAlarmByStatus(Integer alarmStatus, Date start, Date end, Integer current, Integer pageSize);
    //不带时间重载

    /**
     * 根据告警状态查询，用selectCondition实现，不带时间重载
     * @param alarmStatus 告警状态
     * @return 告警记录列表
     */
    List<Alarm> selectAlarmByStatus(Integer alarmStatus, Integer current, Integer pageSize);


    /**
     * 根据状态查询告警数量
     * @param alarmStatus 告警状态
     * @return 告警数量
     */
    Integer selectAlarmCountByStatus(Integer alarmStatus) throws Exception;

    /**
     * 根据锁操作记录对应的锁编号查询告警，可指定时间段
     * @param lockCode 锁边号
     * @param start 开始时间
     * @param end 截止时间
     * @param page 分页对象
     * @return 告警记录列表
     */
    List<Alarm> selectAlarmByLockCode(String lockCode, Date start, Date end, SimplePage page);

    /**
     * 根据锁操作记录对应的锁编号查询告警，不带时间重载
     * @param lockCode 锁边号
     * @param page 分页对象
     * @return 告警记录列表
     */
    List<Alarm> selectAlarmByLockCode(String lockCode, SimplePage page);

    /**
     * 根据锁操作记录对应的设备ip地址查询告警，可指定时间段
     * @param deviceIp 摄像头ip地址
     * @param start 开始时间
     * @param end 截止时间
     * @param page 分页对象
     * @return 告警记录列表
     */
    List<Alarm>  selectAlarmByDeviceIp(String deviceIp, Date start, Date end, SimplePage page);

    /**
     * 根据锁操作记录对应的设备ip地址查询告警，不带时间重载
     * @param deviceIp 摄像头ip地址
     * @param page 分页对象
     * @return 告警记录列表
     */
    List<Alarm>  selectAlarmByDeviceIp(String deviceIp, SimplePage page);

    /**
     * 根据锁操作记录对应的设备id查询告警，可指定时间段
     * @param devId 摄像头设备id
     * @param start 开始时间
     * @param end 截止时间
     * @param page 分页对象
     * @return 告警记录列表
     */
    List<Alarm> selectAlarmByDevId(String devId, Date start, Date end, SimplePage page);

    /**
     * 根据锁操作记录对应的设备id查询告警，不带时间重载
     * @param devId 摄像头设备id
     * @param page 分页对象
     * @return 告警记录列表
     */
    List<Alarm> selectAlarmByDevId(String devId, SimplePage page);

    /**
     * 根据alarm对象所带条件查询，可指定时间段
     * @param alarm 告警对象
     * @param start 开始时间
     * @param end 截止时间
     * @return 告警记录列表
     */
    Page<Alarm> selectAlarmConditionByPage(Alarm alarm, Date start, Date end, String deptId,Integer alarmStatus, Integer current, Integer pageSize) throws Exception;

    /**
     * 根据alarm对象所带条件查询，不带时间段重载
     * @param alarm 告警对象
     * @return 告警记录列表
     */
    Page<Alarm> selectAlarmConditionByPage(Alarm alarm,Integer current, Integer pageSize) throws Exception;

}
