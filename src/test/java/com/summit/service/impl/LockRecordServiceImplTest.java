package com.summit.service.impl;

import com.summit.dao.entity.FileInfo;
import com.summit.dao.entity.LockProcess;
import com.summit.dao.entity.SimplePage;
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

    SimpleDateFormat temeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    SimplePage page = new SimplePage(1, 1);

    @Test
    public void insertLockProcess() throws ParseException {
        String facePanoramaId = "16";
        String facePicId = "17";
        String faceMatchId = "18";
        FileInfo facePanorama = new FileInfo(facePanoramaId,"16n","16p","16d");
        FileInfo facePic = new FileInfo(facePicId,"17n","17p","17d");
        FileInfo faceMatch = new FileInfo(faceMatchId,"18n","18p","18d");
        String processId = "pid03";
        LockProcess lockProcess = new LockProcess(null,"devip03","lid05","NB100002",null,"user01","un01",
                0,dateFormat.parse("2012-7-27"),"甘肃","庆阳",0,"card_aaa",95f,"白名单",2,
                1,"success","",
                facePanoramaId,facePicId,faceMatchId,facePanorama,facePic,faceMatch,temeFormat.parse("2019-7-27 08:00:00"));

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
        LockProcess lockProcess = lockRecordService.selectLockProcessById("1158360850495442945");
        System.out.println(lockProcess);
    }

    @Test
    public void selectLockProcessByLockCode() {
        List<LockProcess> lockProcesses = lockRecordService.selectLockProcessByLockCode("NB100002", null);
        System.out.println(lockProcesses);
    }

    @Test
    public void selectLockProcessByLockCode1() {
    }

    @Test
    public void selectLockProcessByDeviceIp() {
        List<LockProcess> lockProcesses = lockRecordService.selectLockProcessByDeviceIp("192.168.141.141", null);
        System.out.println(lockProcesses);
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