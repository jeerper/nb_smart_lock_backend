package com.summit.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.summit.dao.entity.Alarm;
import com.summit.dao.entity.CameraDevice;
import com.summit.dao.entity.LockProcess;
import com.summit.dao.repository.AlarmDao;
import com.summit.dao.repository.CameraDeviceDao;
import com.summit.dao.repository.LockProcessDao;
import com.summit.service.AlarmService;
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

    @Override
    public int insertAlarm(Alarm alarm) {
        if(lockProcessDao.selectById(alarm.getProcessId()) == null){
            log.warn("此告警对应锁操作记录不存在");
//            return -1;
        }
        return alarmDao.insert(alarm);
    }

    @Override
    public int updateAlarm(Alarm alarm) {
        if(lockProcessDao.selectById(alarm.getProcessId()) == null){
            log.warn("此告警对应锁操作记录不存在");
//            return -1;
        }
        UpdateWrapper<Alarm> updateWrapper = new UpdateWrapper<>();
        return alarmDao.update(alarm , updateWrapper.eq("alarm_id" , alarm.getAlarmId())
        /*.or().eq("process_id" , alarm.getProcessId())*/);
    }

    @Override
    public int delLockAlarm(Alarm alarm) {
        if(alarm.getAlarmId() != null && alarm.getProcessId() != null){
            //若alarmId和processId都不为空，以alarmId为准
            alarm.setProcessId(null);
        }
        UpdateWrapper<Alarm> wrapper = new UpdateWrapper<>();
        return alarmDao.delete(wrapper.eq("alarm_id" , alarm.getAlarmId())
                .or().eq("process_id" , alarm.getProcessId()));
    }

    @Override
    public Alarm selectAlarmById(String alarmId) {
        return alarmDao.selectById(alarmId);
    }

    @Override
    public List<Alarm> selectAlarmByName(String alarmName, Date start, Date end) {
        Alarm alarm = new Alarm();
        alarm.setAlarmName(alarmName);
        return alarmDao.selectCondition(alarm, start, end);
    }

    @Override
    public List<Alarm> selectAlarmByName(String alarmName) {
        return selectAlarmByName(alarmName, null, null);
    }

    @Override
    public List<Alarm> selectAlarmByStatus(Integer alarmStatus, Date start, Date end) {
        Alarm alarm = new Alarm();
        alarm.setAlarmStatus(alarmStatus);
        return alarmDao.selectCondition(alarm, start, end);
    }

    @Override
    public List<Alarm> selectAlarmByStatus(Integer alarmStatus) {
        return selectAlarmByStatus(alarmStatus, null, null);
    }

    @Override
    public List<Alarm> selectAlarmByLockCode(String lockCode, Date start, Date end) {
        List<Alarm> alarms = new ArrayList<>();
        List<LockProcess> lockProcesses = lockProcessDao.selectByLockCode(lockCode);
        for (LockProcess lockProcess: lockProcesses) {
            alarms.add(alarmDao.selectByProcessId(lockProcess.getProcessId()));
        }
        return alarms;
    }

    @Override
    public List<Alarm> selectAlarmByLockCode(String lockCode) {
        return selectAlarmByLockCode(lockCode, null, null);
    }

    @Override
    public List<Alarm> selectAlarmByDeviceIp(String deviceIp, Date start, Date end) {
        List<Alarm> alarms = new ArrayList<>();
        LockProcess lp = new LockProcess();
        lp.setDeviceIp(deviceIp);
        List<LockProcess> lockProcesses = lockProcessDao.selectCondition(lp, start, end);
        for (LockProcess lockProcess: lockProcesses) {
            alarms.add(alarmDao.selectByProcessId(lockProcess.getProcessId()));
        }
        return alarms;
    }

    @Override
    public List<Alarm> selectAlarmByDeviceIp(String deviceIp) {
        return selectAlarmByDeviceIp(deviceIp, null, null);
    }

    @Override
    public List<Alarm> selectAlarmByDevId(String devId, Date start, Date end) {
        List<Alarm> alarms = new ArrayList<>();
        CameraDevice cameraDevice = deviceDao.selectById(devId);
        if(cameraDevice == null){
            log.warn("此设备id不存在");
            return null;
        }
        LockProcess lp = new LockProcess();
        lp.setDeviceIp(cameraDevice.getDeviceIp());
        List<LockProcess> lockProcesses = lockProcessDao.selectCondition(lp, start, end);
        for (LockProcess lockProcess: lockProcesses) {
            alarms.add(alarmDao.selectByProcessId(lockProcess.getProcessId()));
        }
        return alarms;
    }

    @Override
    public List<Alarm> selectAlarmByDevId(String devId) {
        return selectAlarmByDevId(devId, null, null);
    }

    @Override
    public List<Alarm> selectAlarmCondition(Alarm alarm, Date start, Date end) {
        return alarmDao.selectCondition(alarm, start, end);
    }

    @Override
    public List<Alarm> selectAlarmCondition(Alarm alarm) {
        return selectAlarmCondition(alarm, null, null);
    }
}
