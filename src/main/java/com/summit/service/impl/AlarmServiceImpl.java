package com.summit.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.summit.dao.entity.Alarm;
import com.summit.dao.entity.CameraDevice;
import com.summit.dao.entity.LockProcess;
import com.summit.dao.entity.Page;
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
        if(lockProcessDao.selectLockProcessById(alarm.getProcessId()) == null){
            log.warn("此告警对应锁操作记录不存在");
//            return -1;
        }
        return alarmDao.insert(alarm);
    }

    @Override
    public int updateAlarm(Alarm alarm) {
        if(lockProcessDao.selectLockProcessById(alarm.getProcessId()) == null){
            log.warn("此告警对应锁操作记录不存在");
//            return -1;
        }
        UpdateWrapper<Alarm> updateWrapper = new UpdateWrapper<>();
        return alarmDao.update(alarm , updateWrapper.eq("alarm_id" , alarm.getAlarmId())
        /*.or().eq("process_id" , alarm.getProcessId())*/);
    }

    @Override
    public int delLockAlarmById(String alarmId) {
        UpdateWrapper<Alarm> wrapper = new UpdateWrapper<>();
        return alarmDao.delete(wrapper.eq("alarm_id" , alarmId));
    }

    @Override
    public List<Alarm> selectAll(Page page) {
        return alarmDao.selectCondition(new Alarm(), null, null, page);
    }

    @Override
    public Alarm selectAlarmById(String alarmId) {
        return alarmDao.selectAlarmById(alarmId);
    }

    @Override
    public List<Alarm> selectAlarmByName(String alarmName, Date start, Date end, Page page) {
        Alarm alarm = new Alarm();
        alarm.setAlarmName(alarmName);
        return alarmDao.selectCondition(alarm, start, end, page);
    }

    @Override
    public List<Alarm> selectAlarmByName(String alarmName, Page page) {
        return selectAlarmByName(alarmName, null, null, page);
    }

    @Override
    public List<Alarm> selectAlarmByStatus(Integer alarmStatus, Date start, Date end, Page page) {
        Alarm alarm = new Alarm();
        alarm.setAlarmStatus(alarmStatus);
        return alarmDao.selectCondition(alarm, start, end, page);
    }

    @Override
    public List<Alarm> selectAlarmByStatus(Integer alarmStatus, Page page) {
        return selectAlarmByStatus(alarmStatus, null, null, page);
    }

    @Override
    public List<Alarm> selectAlarmByLockCode(String lockCode, Date start, Date end, Page page) {
        List<Alarm> alarms = new ArrayList<>();
        List<LockProcess> lockProcesses = lockProcessDao.selectByLockCode(lockCode, page);
        for (LockProcess lockProcess: lockProcesses) {
            alarms.add(alarmDao.selectByProcessId(lockProcess.getProcessId()));
        }
        return alarms;
    }

    @Override
    public List<Alarm> selectAlarmByLockCode(String lockCode, Page page) {
        return selectAlarmByLockCode(lockCode, null, null, page);
    }

    @Override
    public List<Alarm> selectAlarmByDeviceIp(String deviceIp, Date start, Date end, Page page) {
        List<Alarm> alarms = new ArrayList<>();
        LockProcess lp = new LockProcess();
        lp.setDeviceIp(deviceIp);
        List<LockProcess> lockProcesses = lockProcessDao.selectCondition(lp, start, end, page);
        for (LockProcess lockProcess: lockProcesses) {
            alarms.add(alarmDao.selectByProcessId(lockProcess.getProcessId()));
        }
        return alarms;
    }

    @Override
    public List<Alarm> selectAlarmByDeviceIp(String deviceIp, Page page) {
        return selectAlarmByDeviceIp(deviceIp, null, null, page);
    }

    @Override
    public List<Alarm> selectAlarmByDevId(String devId, Date start, Date end, Page page) {
        List<Alarm> alarms = new ArrayList<>();
        CameraDevice cameraDevice = deviceDao.selectDeviceById(devId);
        if(cameraDevice == null){
            log.warn("此设备id不存在");
            return null;
        }
        LockProcess lp = new LockProcess();
        lp.setDeviceIp(cameraDevice.getDeviceIp());
        List<LockProcess> lockProcesses = lockProcessDao.selectCondition(lp, start, end, page);
        for (LockProcess lockProcess: lockProcesses) {
            alarms.add(alarmDao.selectByProcessId(lockProcess.getProcessId()));
        }
        return alarms;
    }

    @Override
    public List<Alarm> selectAlarmByDevId(String devId, Page page) {
        return selectAlarmByDevId(devId, null, null, page);
    }

    @Override
    public List<Alarm> selectAlarmCondition(Alarm alarm, Date start, Date end, Page page) {
        return alarmDao.selectCondition(alarm, start, end, page);
    }

    @Override
    public List<Alarm> selectAlarmCondition(Alarm alarm, Page page) {
        return selectAlarmCondition(alarm, null, null, page);
    }
}
