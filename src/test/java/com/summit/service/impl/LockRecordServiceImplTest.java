package com.summit.service.impl;

import com.summit.dao.entity.FileInfo;
import com.summit.dao.entity.LockProcess;
import com.summit.dao.entity.Page;
import com.summit.service.LockRecordService;
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
public class LockRecordServiceImplTest {

    @Autowired
    private LockRecordService lockRecordService;

    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    Page page = new Page(1, 3);

    @Test
    public void insertLockProcess() throws ParseException {
        FileInfo facePanorama = new FileInfo("LockPro01","LockProNm01","LockProPaht01","LockProDes01");
        FileInfo facePic = new FileInfo("LockPro02","LockProNm02","LockProPaht02","LockProDes02");
        FileInfo faceMatch = new FileInfo("LockPro03","LockProNm03","LockProPaht03","LockProDes03");
        String processId = "pid01";
        LockProcess lockProcess = new LockProcess(processId,"devip01","lc01",null,"user01","un01",1,"sus","",
                facePanorama,facePic,faceMatch,dateFormat.parse("2019-7-24 00:00:00"));

        lockRecordService.insertLockProcess(lockProcess);
    }

    @Test
    public void updateLockProcess() {
    }

    @Test
    public void delLockProcess() {
    }

    @Test
    public void selectAll() {
        List<LockProcess> lockProcesses = lockRecordService.selectAll(null);
        System.out.println(lockProcesses);
    }

    @Test
    public void selectLockProcessById() {
    }

    @Test
    public void selectLockProcessByLockCode() {
    }

    @Test
    public void selectLockProcessByLockCode1() {
    }

    @Test
    public void selectLockProcessByDeviceIp() {
    }

    @Test
    public void selectLockProcessByDeviceIp1() {
    }

    @Test
    public void selectLockProcessByDevId() {
    }

    @Test
    public void selectLockProcessByDevId1() {
    }

    @Test
    public void selectLockProcessByUserName() {
    }

    @Test
    public void selectLockProcessByUserName1() {
    }

    @Test
    public void selectLockProcessByType() {
    }

    @Test
    public void selectLockProcessByType1() {
    }

    @Test
    public void selectLockProcessByResult() {
    }

    @Test
    public void selectLockProcessByResult1() {
    }

    @Test
    public void selectLockProcessCondition() {
    }

    @Test
    public void selectLockProcessCondition1() {
    }
}