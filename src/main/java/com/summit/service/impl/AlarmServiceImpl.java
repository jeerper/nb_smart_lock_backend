package com.summit.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.summit.constants.CommonConstants;
import com.summit.dao.entity.Alarm;
import com.summit.dao.entity.CameraDevice;
import com.summit.dao.entity.LockProcess;
import com.summit.dao.entity.Page;
import com.summit.dao.repository.AlarmDao;
import com.summit.dao.repository.CameraDeviceDao;
import com.summit.dao.repository.LockProcessDao;
import com.summit.service.AlarmService;
import com.summit.util.LockAuthCtrl;
import com.summit.util.PageConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class AlarmServiceImpl implements AlarmService {

    @Autowired
    private AlarmDao alarmDao;

    @Autowired
    private LockProcessDao lockProcessDao;
    @Autowired
    private CameraDeviceDao deviceDao;

    /**
     * 告警插入
     * @param alarm 告警对象
     * @return 不为-1则成功
     */
    @Override
    public int insertAlarm(Alarm alarm) {
        if(alarm == null){
            log.error("告警信息为空");
            return CommonConstants.UPDATE_ERROR;
        }
        List<String> roles = LockAuthCtrl.getRoles();
        if(lockProcessDao.selectLockProcessById(alarm.getProcessId(),roles) == null){
            log.warn("此告警对应锁操作记录不存在");
//            return -1;
        }
        return alarmDao.insert(alarm);
    }

    /**
     * 告警更新
     * @param alarm 告警对象
     * @return 不为-1则成功
     */
    @Override
    public int updateAlarm(Alarm alarm) {
        if(alarm == null){
            log.error("告警信息为空");
            return CommonConstants.UPDATE_ERROR;
        }
        List<String> roles = LockAuthCtrl.getRoles();
        if(lockProcessDao.selectLockProcessById(alarm.getProcessId(),roles) == null){
            log.warn("此告警对应锁操作记录不存在");
//            return -1;
        }
        UpdateWrapper<Alarm> updateWrapper = new UpdateWrapper<>();
        if(alarm.getAlarmId() != null){
            return alarmDao.update(alarm , updateWrapper.eq("alarm_id" , alarm.getAlarmId()));
        }
        return alarmDao.update(alarm , updateWrapper.eq("process_id" , alarm.getProcessId()));
    }

    /**
     * 告警删除
     * @param alarmId 告警id
     * @return 不为-1则成功
     */
    @Override
    public int delLockAlarmById(String alarmId) {
        if(alarmId == null){
            log.error("告警id为空");
            return CommonConstants.UPDATE_ERROR;
        }
        UpdateWrapper<Alarm> wrapper = new UpdateWrapper<>();
        return alarmDao.delete(wrapper.eq("alarm_id" , alarmId));
    }

    /**
     * 查询所有
     * @param start 开始时间
     * @param end 截止时间
     * @param page 分页对象
     * @return 告警记录列表
     */
    @Override
    public List<Alarm> selectAll(Date start, Date end, Page page) {
        PageConverter.convertPage(page);
        List<String> roles = LockAuthCtrl.getRoles();
        return alarmDao.selectCondition(new Alarm(), start, end, page,roles);
    }

    /**
     * 根据告警Id查询唯一告警记录
     * @param alarmId 告警id
     * @return 唯一确定的告警记录
     */
    @Override
    public Alarm selectAlarmById(String alarmId) {
        if(alarmId == null){
            log.error("告警id为空");
            return null;
        }
        List<String> roles = LockAuthCtrl.getRoles();
        return alarmDao.selectAlarmById(alarmId, roles);
    }

    /**
     * 根据告警name（或者说类型）查询，用selectCondition实现，可指定时间段
     * @param alarmName 告警名
     * @param start 开始时间
     * @param end 截止时间
     * @param page 分页对象
     * @return 告警记录列表
     */
    @Override
    public List<Alarm> selectAlarmByName(String alarmName, Date start, Date end, Page page) {
        if(alarmName == null){
            log.error("告警名称为空");
            return null;
        }
        PageConverter.convertPage(page);
        Alarm alarm = new Alarm();
        alarm.setAlarmName(alarmName);
        List<String> roles = LockAuthCtrl.getRoles();
        return alarmDao.selectCondition(alarm, start, end, page, roles);
    }

    /**
     * 根据告警name（或者说类型）查询，用selectCondition实现，不带时间重载
     * @param alarmName 告警名
     * @param page 分页对象
     * @return 告警记录列表
     */
    @Override
    public List<Alarm> selectAlarmByName(String alarmName, Page page) {
        if(alarmName == null){
            log.error("告警名称为空");
            return null;
        }
        return selectAlarmByName(alarmName, null, null, page);
    }

    /**
     * 根据告警状态查询，用selectCondition实现，可指定时间段
     * @param alarmStatus 告警状态
     * @param start 开始时间
     * @param end 截止时间
     * @param page 分页对象
     * @return 告警记录列表
     */
    @Override
    public List<Alarm> selectAlarmByStatus(Integer alarmStatus, Date start, Date end, Page page) {
        if(alarmStatus == null){
            log.error("告警状态为空");
            return null;
        }
        PageConverter.convertPage(page);
        Alarm alarm = new Alarm();
        alarm.setAlarmStatus(alarmStatus);
        List<String> roles = LockAuthCtrl.getRoles();
        return alarmDao.selectCondition(alarm, start, end, page, roles);
    }

    /**
     * 根据告警状态查询，用selectCondition实现，不带时间重载
     * @param alarmStatus 告警状态
     * @param page 分页对象
     * @return 告警记录列表
     */
    @Override
    public List<Alarm> selectAlarmByStatus(Integer alarmStatus, Page page) {
        if(alarmStatus == null){
            log.error("告警状态为空");
            return null;
        }
        return selectAlarmByStatus(alarmStatus, null, null, page);
    }

    /**
     * 根据状态查询告警数量
     * @param alarmStatus 告警状态
     * @return 告警数量
     */
    @Override
    public Integer selectAlarmCountByStatus(Integer alarmStatus) {
        if(alarmStatus == null){
            log.error("告警状态为空");
            return null;
        }
        List<String> roles = LockAuthCtrl.getRoles();
        return alarmDao.selectAlarmCountByStatus(alarmStatus,roles);
    }

    /**
     * 根据锁操作记录对应的锁编号查询告警，可指定时间段
     * @param lockCode 锁边号
     * @param start 开始时间
     * @param end 截止时间
     * @param page 分页对象
     * @return 告警记录列表
     */
    @Override
    public List<Alarm> selectAlarmByLockCode(String lockCode, Date start, Date end, Page page) {
        if(lockCode == null){
            log.error("锁编号为空");
            return null;
        }
        PageConverter.convertPage(page);
        List<Alarm> alarms = new ArrayList<>();
        List<String> roles = LockAuthCtrl.getRoles();
        List<LockProcess> lockProcesses = lockProcessDao.selectByLockCode(lockCode, page,roles);
        for (LockProcess lockProcess: lockProcesses) {

            alarms.add(alarmDao.selectByProcessId(lockProcess.getProcessId(), roles));
        }
        return alarms;
    }

    /**
     * 根据锁操作记录对应的锁编号查询告警，不带时间重载
     * @param lockCode 锁边号
     * @param page 分页对象
     * @return 告警记录列表
     */
    @Override
    public List<Alarm> selectAlarmByLockCode(String lockCode, Page page) {
        if(lockCode == null){
            log.error("锁编号为空");
            return null;
        }
        return selectAlarmByLockCode(lockCode, null, null, page);
    }

    /**
     * 根据锁操作记录对应的设备ip地址查询告警，可指定时间段
     * @param deviceIp 摄像头ip地址
     * @param start 开始时间
     * @param end 截止时间
     * @param page 分页对象
     * @return 告警记录列表
     */
    @Override
    public List<Alarm> selectAlarmByDeviceIp(String deviceIp, Date start, Date end, Page page) {
        if(deviceIp == null){
            log.error("设备ip地址为空");
            return null;
        }
        PageConverter.convertPage(page);
        List<Alarm> alarms = new ArrayList<>();
        LockProcess lp = new LockProcess();
        lp.setDeviceIp(deviceIp);
        List<String> roles = LockAuthCtrl.getRoles();
        List<LockProcess> lockProcesses = lockProcessDao.selectCondition(lp, start, end, page,roles);
        for (LockProcess lockProcess: lockProcesses) {
            alarms.add(alarmDao.selectByProcessId(lockProcess.getProcessId(), roles));
        }
        return alarms;
    }

    /**
     * 根据锁操作记录对应的设备ip地址查询告警，不带时间重载
     * @param deviceIp 摄像头ip地址
     * @param page 分页对象
     * @return 告警记录列表
     */
    @Override
    public List<Alarm> selectAlarmByDeviceIp(String deviceIp, Page page) {
        if(deviceIp == null){
            log.error("设备ip地址为空");
            return null;
        }
        return selectAlarmByDeviceIp(deviceIp, null, null, page);
    }

    /**
     * 根据锁操作记录对应的设备id查询告警，可指定时间段
     * @param devId 摄像头设备id
     * @param start 开始时间
     * @param end 截止时间
     * @param page 分页对象
     * @return 告警记录列表
     */
    @Override
    public List<Alarm> selectAlarmByDevId(String devId, Date start, Date end, Page page) {
        if(devId == null){
            log.error("设备id为空");
            return null;
        }
        PageConverter.convertPage(page);
        List<Alarm> alarms = new ArrayList<>();
        CameraDevice cameraDevice = deviceDao.selectDeviceById(devId);
        if(cameraDevice == null){
            log.warn("此设备id不存在");
            return null;
        }
        LockProcess lp = new LockProcess();
        lp.setDeviceIp(cameraDevice.getDeviceIp());
        List<String> roles = LockAuthCtrl.getRoles();
        List<LockProcess> lockProcesses = lockProcessDao.selectCondition(lp, start, end, page,roles);
        for (LockProcess lockProcess: lockProcesses) {
            alarms.add(alarmDao.selectByProcessId(lockProcess.getProcessId(), roles));
        }
        return alarms;
    }

    /**
     * 根据锁操作记录对应的设备id查询告警，不带时间重载
     * @param devId 摄像头设备id
     * @param page 分页对象
     * @return 告警记录列表
     */
    @Override
    public List<Alarm> selectAlarmByDevId(String devId, Page page) {
        if(devId == null){
            log.error("设备id为空");
            return null;
        }
        return selectAlarmByDevId(devId, null, null, page);
    }

    /**
     * 根据alarm对象所带条件查询，可指定时间段
     * @param alarm 告警对象
     * @param start 开始时间
     * @param end 截止时间
     * @param page 分页对象
     * @return 告警记录列表
     */
    @Override
    public List<Alarm> selectAlarmCondition(Alarm alarm, Date start, Date end, Page page) {
        if(alarm == null){
            log.error("告警信息为空");
            return null;
        }
        PageConverter.convertPage(page);
        List<String> roles = LockAuthCtrl.getRoles();
        return alarmDao.selectCondition(alarm, start, end, page, roles);
    }

    /**
     * 根据alarm对象所带条件查询，不带时间段重载
     * @param alarm 告警对象
     * @param page 分页对象
     * @return 告警记录列表
     */
    @Override
    public List<Alarm> selectAlarmCondition(Alarm alarm, Page page) {
        if(alarm == null){
            log.error("告警信息为空");
            return null;
        }
        return selectAlarmCondition(alarm, null, null, page);
    }
}
