package com.summit.service.impl;

import com.summit.cbb.utils.page.Page;
import com.summit.dao.entity.LockInfo;
import com.summit.dao.entity.SimplePage;
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
    SimplePage page = new SimplePage(1, 8);

    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Test
    public void insertLock() throws ParseException {
        LockInfo lockInfo = new LockInfo(null,"lc08",1,"user008",
                dateFormat.parse("2019-7-27 08:00:00"),dateFormat.parse("2019-7-28 08:00:00"),null,null);
        lockInfoService.insertLock(lockInfo);
        System.out.println(lockInfo);
    }

    @Test
    public void updateLock() {
    }

    @Test
    public void delLockByLockId() {
    }

    @Test
    public void selectLockById() {
        LockInfo lockInfo = lockInfoService.selectLockById("lid04");
        System.out.println(lockInfo);
    }

    @Test
    public void selectBylockCode() {
        LockInfo lockInfo = lockInfoService.selectBylockCode("lc07");
        System.out.println(lockInfo);
    }

    @Test
    public void selectAll() {
        Page<LockInfo> lockInfoPage = lockInfoService.selectLockInfoByPage(new SimplePage(2, 3));
        List<LockInfo> lockInfos = lockInfoPage.getContent();
        System.out.println(lockInfos);
    }

    @Test
    public void selectCondition() {
        LockInfo lockInfo =new LockInfo();
        lockInfo.setLockCode("1nb");
        lockInfoService.selectCondition(lockInfo,page);
    }

    @Test
    public void selectAllHaveHistory() {
        List<LockInfo> lockInfos = lockInfoService.selectHaveHistoryByPage(new SimplePage(1,1)).getContent();
        System.out.println(lockInfos);
    }


    @Test
    public void delLockByLockCod() {
    }
}