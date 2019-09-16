package com.summit.service.impl;

import com.summit.cbb.utils.page.Page;
import com.summit.dao.entity.Alarm;
import com.summit.dao.entity.SimplePage;
import com.summit.dao.repository.AlarmDao;
import com.summit.sdk.huawei.model.AlarmStatus;
import com.summit.service.AlarmService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
@Slf4j
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
    public void insertAlarm() {

        Alarm alarm = new Alarm();
        alarm.setAccCtrlProId("123123213213123");
        alarm.setAlarmTime(new Date());
        alarm.setAlarmStatus(AlarmStatus.UNPROCESSED.getCode());
        alarm.setDescription("哈哈");
        alarmService.insertAlarm(alarm);
        log.debug(alarm.getAlarmId());


    }



    @Test
    public void delLockAlarmById() {
        alarmService.delLockAlarmById("aid01");
    }

    @Test
    public void selectAll() {

        List<Alarm> alarms = alarmService.selectAll(null,null,14,2);
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
        List<Alarm> alarms = alarmService.selectAlarmByStatus(1, null,null);
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
        Page<Alarm> alarmPage = alarmService.selectAlarmConditionByPage(alarm, null,null);
        System.out.println(alarmPage);
    }

    @Test
    public void selectAlarmCondition1() {

    }
}