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


@RunWith(SpringRunner.class)
@SpringBootTest
public class LockInfoServiceImplTest {

    @Autowired
    private LockInfoService lockInfoService;
    Page page = new Page(0, 3);

    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Test
    public void insertLock() throws ParseException {
        LockInfo lockInfo = new LockInfo("lid02","lc02",1,"user004",dateFormat.parse("2019-7-29 01:00:00"),null,null);
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
        LockInfo lockInfo = lockInfoService.selectLockById("1");
        System.out.println(lockInfo);
    }

    @Test
    public void selectBylockCode() {
        LockInfo lockInfo = lockInfoService.selectBylockCode("1nb");
        System.out.println(lockInfo);
    }

    @Test
    public void selectAll() {
        List<LockInfo> lockInfos = lockInfoService.selectAll(null);
        System.out.println(lockInfos);
    }

    @Test
    public void selectCondition() {
        LockInfo lockInfo =new LockInfo();
        lockInfo.setLockCode("1nb");
        lockInfoService.selectCondition(lockInfo,page);
    }


    @Test
    public void delLockByLockCod() {
    }
}