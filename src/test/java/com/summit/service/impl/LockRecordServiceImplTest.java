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

@RunWith(SpringRunner.class)
@SpringBootTest
public class LockRecordServiceImplTest {

    @Autowired
    private LockRecordService lockRecordService;

    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    Page page = new Page(1, 1);

    @Test
    public void insertLockProcess() throws ParseException {
        String facePanoramaId = "16";
        String facePicId = "17";
        String faceMatchId = "18";
        FileInfo facePanorama = new FileInfo(facePanoramaId,"16n","16p","16d");
        FileInfo facePic = new FileInfo(facePicId,"17n","17p","17d");
        FileInfo faceMatch = new FileInfo(faceMatchId,"18n","18p","18d");
        String processId = "pid03";
        LockProcess lockProcess = new LockProcess(null,"devip03","lc05",null,"user01","un01",1,"fail","匹配度过低",
                facePanoramaId,facePicId,faceMatchId,facePanorama,facePic,faceMatch,0.9f,dateFormat.parse("2019-7-27 08:00:00"));

        lockRecordService.insertLockProcess(lockProcess);
        System.out.println(lockProcess);
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