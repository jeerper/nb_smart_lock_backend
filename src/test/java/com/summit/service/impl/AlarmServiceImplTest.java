package com.summit.service.impl;

import com.summit.cbb.utils.page.Page;
import com.summit.dao.entity.Alarm;
import com.summit.dao.entity.SimplePage;
import com.summit.dao.repository.AlarmDao;
import com.summit.service.AlarmService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AlarmServiceImplTest {
    @Autowired
    private AlarmService alarmService;
    @Autowired
    private AlarmDao alarmDao;

    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    SimplePage page = new SimplePage(1, 3);

    @Test
    public void insertAlarm() throws ParseException {
//        FileInfo facePanorama = new FileInfo("10","10n","10p","10d");
//        FileInfo facePic = new FileInfo("11","11n","11p","11d");
//        FileInfo faceMatch = new FileInfo("12","12n","12p","12d");
        String processId = "pid02";
//        LockProcess lockProcess = new LockProcess(processId,"devip01","lc01",null,"user01","un01",1,"sus","",
//                facePanorama,facePic,faceMatch,dateFormat.parse("2019-7-24 00:00:00"));
        Alarm alarm = new Alarm("aid02","an02",processId,
                null,null,null,dateFormat.parse("2019-8-03 00:03:00"),1,
                "p1","d1",new Date(),"r1",null);

        alarmService.insertAlarm(alarm);
    }

    @Test
    public void updateAlarm() throws ParseException {
        String processId = "pid01";
        Alarm alarm = new Alarm("aid01","an01",processId,
                null,null,null,dateFormat.parse("2019-7-24 00:01:01"),
                1,"p2","d2",new Date(),"r2",null);
        alarmService.updateAlarm(alarm);
    }

    @Test
    public void delLockAlarmById() {
        alarmService.delLockAlarmById("aid01");
    }

    @Test
    public void selectAll() {

        List<Alarm> alarms = alarmService.selectAll(null,null,new SimplePage(14,2));
        System.out.println(alarms);
    }

    @Test
    public void selectAlarmById() {
        Alarm alarm = alarmService.selectAlarmById("1167390622789668865");
        System.out.println(alarm);
    }

    @Test
    public void selectCountByCondition() {
        Alarm alarm  = new Alarm();
        alarm.setAlarmStatus(0);
        Integer count = alarmDao.selectCountByCondition(alarm,null,null,null);
        System.out.println(count);
    }

    @Test
    public void selectByAccCtrlProId() {
        Alarm alarm = alarmDao.selectByAccCtrlProId("1166896782903349250", null);
        System.out.println(alarm);
    }

    @Test
    public void selectAlarmCountByStatus() {
        Integer count = alarmService.selectAlarmCountByStatus(1);
        System.out.println(count);
    }

    @Test
    public void selectAlarmByName() {
    }

    @Test
    public void selectAlarmByStatus() {
        List<Alarm> alarms = alarmService.selectAlarmByStatus(1, null);
        System.out.println(alarms);
    }

    @Test
    public void selectAlarmByStatus1() {
    }

    @Test
    public void selectAlarmByLockCode() {
    }

    @Test
    public void selectAlarmByLockCode1() {
    }

    @Test
    public void selectAlarmByDeviceIp() {
        List<Alarm> alarms = alarmService.selectAlarmByDeviceIp("devip01", page);
        System.out.println(alarms);
    }

    @Test
    public void selectAlarmByDeviceIp1() {
    }

    @Test
    public void selectAlarmByDevId() {
    }

    @Test
    public void selectAlarmByDevId1() {
    }

    @Test
    public void selectAlarmCondition() {
        Alarm alarm = new Alarm();
        alarm.setAccessControlName("门禁");
        Page<Alarm> alarmPage = alarmService.selectAlarmConditionByPage(alarm, null);
        System.out.println(alarmPage);
    }

    @Test
    public void selectAlarmCondition1() {

    }
}