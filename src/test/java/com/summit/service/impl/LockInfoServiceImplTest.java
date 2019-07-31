package com.summit.service.impl;

import com.summit.dao.entity.LockInfo;
import com.summit.dao.entity.Page;
import com.summit.service.LockInfoService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class LockInfoServiceImplTest {

    @Autowired
    private LockInfoService lockInfoService;
    Page page = new Page(0, 3);

    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Test
    public void insertLock() throws ParseException {
        LockInfo lockInfo = new LockInfo("lid01","lc01",1,dateFormat.parse("2019-7-23 01:00:00"),null);
        lockInfoService.insertLock(lockInfo);
    }

    @Test
    public void updateLock() {
    }

    @Test
    public void delLockByLockId() {
    }

    @Test
    public void selectLockById() {
    }

    @Test
    public void selectBylockCode() {
        LockInfo lockInfo = lockInfoService.selectBylockCode("lc01");
        System.out.println(lockInfo);
    }

    @Test
    public void selectAll() {
        List<LockInfo> lockInfos = lockInfoService.selectAll(page);
        System.out.println(lockInfos);
    }

    @Test
    public void selectCondition() {
    }


    @Test
    public void delLockByLockCod() {
    }
}