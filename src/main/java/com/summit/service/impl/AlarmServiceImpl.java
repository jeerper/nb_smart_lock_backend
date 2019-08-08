package com.summit.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
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

    @Override
    public int insertAlarm(Alarm alarm) {
        if(alarm == null){
            log.error("告警信息为空");
            return -1;
        }
        List<String> roles = LockAuthCtrl.getRoles();
        if(lockProcessDao.selectLockProcessById(alarm.getProcessId(),roles) == null){
            log.warn("此告警对应锁操作记录不存在");
//            return -1;
        }
        return alarmDao.insert(alarm);
    }

    @Override
    public int updateAlarm(Alarm alarm) {
        if(alarm == null){
            log.error("告警信息为空");
            return -1;
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

    @Override
    public int delLockAlarmById(String alarmId) {
        if(alarmId == null){
            log.error("告警id为空");
            return -1;
        }
        UpdateWrapper<Alarm> wrapper = new UpdateWrapper<>();
        return alarmDao.delete(wrapper.eq("alarm_id" , alarmId));
    }

    @Override
    public List<Alarm> selectAll(Page page) {
        PageConverter.convertPage(page);
        List<String> roles = LockAuthCtrl.getRoles();
        List<Alarm> alarms = alarmDao.selectCondition(new Alarm(), null, null, page,roles);

        LockAuthCtrl.toFilterAlarms(alarms);
        return alarms;
    }



    @Override
    public Alarm selectAlarmById(String alarmId) {
        if(alarmId == null){
            log.error("告警id为空");
            return null;
        }
        List<String> roles = LockAuthCtrl.getRoles();
        Alarm alarm = alarmDao.selectAlarmById(alarmId, roles);
        if(!LockAuthCtrl.toFilter(alarm)){
            return null;
        }
        return alarm;
    }

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
        List<Alarm> alarms = alarmDao.selectCondition(alarm, start, end, page, roles);
        LockAuthCtrl.toFilterAlarms(alarms);
        return alarms;
    }

    @Override
    public List<Alarm> selectAlarmByName(String alarmName, Page page) {
        if(alarmName == null){
            log.error("告警名称为空");
            return null;
        }
        return selectAlarmByName(alarmName, null, null, page);
    }

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
        List<Alarm> alarms = alarmDao.selectCondition(alarm, start, end, page, roles);
        LockAuthCtrl.toFilterAlarms(alarms);
        return alarms;
    }

    @Override
    public List<Alarm> selectAlarmByStatus(Integer alarmStatus, Page page) {
        if(alarmStatus == null){
            log.error("告警状态为空");
            return null;
        }
        return selectAlarmByStatus(alarmStatus, null, null, page);
    }

    @Override
    public Integer selectAlarmCountByStatus(Integer alarmStatus) {
        if(alarmStatus == null){
            log.error("告警状态为空");
            return null;
        }
        List<String> roles = LockAuthCtrl.getRoles();
        return alarmDao.selectAlarmCountByStatus(alarmStatus,roles);
    }

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
//        LockAuthCtrl.toFilterAlarms(alarms);
        return alarms;
    }

    @Override
    public List<Alarm> selectAlarmByLockCode(String lockCode, Page page) {
        if(lockCode == null){
            log.error("锁编号为空");
            return null;
        }
        return selectAlarmByLockCode(lockCode, null, null, page);
    }

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
//        LockAuthCtrl.toFilterAlarms(alarms);
        return alarms;
    }

    @Override
    public List<Alarm> selectAlarmByDeviceIp(String deviceIp, Page page) {
        if(deviceIp == null){
            log.error("设备ip地址为空");
            return null;
        }
        return selectAlarmByDeviceIp(deviceIp, null, null, page);
    }

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
//        LockAuthCtrl.toFilterAlarms(alarms);
        return alarms;
    }

    @Override
    public List<Alarm> selectAlarmByDevId(String devId, Page page) {
        if(devId == null){
            log.error("设备id为空");
            return null;
        }
        return selectAlarmByDevId(devId, null, null, page);
    }

    @Override
    public List<Alarm> selectAlarmCondition(Alarm alarm, Date start, Date end, Page page) {
        if(alarm == null){
            log.error("告警信息为空");
            return null;
        }
        PageConverter.convertPage(page);
        List<String> roles = LockAuthCtrl.getRoles();
        List<Alarm> alarms = alarmDao.selectCondition(alarm, start, end, page, roles);
//        LockAuthCtrl.toFilterAlarms(alarms);
        return alarms;
    }

    @Override
    public List<Alarm> selectAlarmCondition(Alarm alarm, Page page) {
        if(alarm == null){
            log.error("告警信息为空");
            return null;
        }
        return selectAlarmCondition(alarm, null, null, page);
    }
}
