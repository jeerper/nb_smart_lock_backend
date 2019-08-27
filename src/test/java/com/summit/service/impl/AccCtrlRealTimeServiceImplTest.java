package com.summit.service.impl;

import com.summit.dao.entity.AccCtrlRealTimeEntity;
import com.summit.service.AccCtrlRealTimeService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AccCtrlRealTimeServiceImplTest {

    @Autowired
    private AccCtrlRealTimeService accCtrlRealTimeService;

    @Test
    public void insertOrUpdate() {
        AccCtrlRealTimeEntity accCtrlRealTimeEntity = new AccCtrlRealTimeEntity();
        int result = accCtrlRealTimeService.insertOrUpdate(accCtrlRealTimeEntity);
        System.out.println(result);
    }

    @Test
    public void delRealTimeInfoByIdBatch() {

    }

    @Test
    public void selectCondition() {
    }

    @Test
    public void selectUpdatetimeById() {
        Long date = accCtrlRealTimeService.selectUpdatetimeById("1");
        System.out.println(date);
    }
}