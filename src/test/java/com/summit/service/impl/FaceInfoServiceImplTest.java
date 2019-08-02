package com.summit.service.impl;

import com.summit.dao.entity.FaceInfo;
import com.summit.dao.entity.FileInfo;
import com.summit.dao.entity.Page;
import com.summit.service.FaceInfoService;
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
public class FaceInfoServiceImplTest {
    @Autowired
    private FaceInfoService faceInfoService;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    Page page = new Page(0, 3);
    @Test
    public void insertFaceInfo() throws ParseException {
        FileInfo facePanorama = new FileInfo("01","1n","1p","1d");
        FileInfo facePic = new FileInfo("02","2n","2p","2d");
        FileInfo faceMatch = new FileInfo("03","3n","3p","3d");
        FaceInfo faceInfo = new FaceInfo("fi01","fun01","fui01",0,dateFormat.parse("2019-7-24 00:00:00"),
                "province01","city01",1,"card01",1.1f,"",2,
                facePanorama,facePic,faceMatch,"devip01",dateFormat.parse("2019-7-23 00:00:00"));

        faceInfoService.insertFaceInfo(faceInfo);
    }

    @Test
    public void updateFaceInfo() {
    }

    @Test
    public void delFaceInfoById() {

    }

    @Test
    public void delFaceInfoByUserName() {
    }

    @Test
    public void selectFaceInfoById() {
        FaceInfo faceInfo = faceInfoService.selectFaceInfoById("fi01");
        System.out.println(faceInfo);
    }

    @Test
    public void selectByUserName() {
        FaceInfo faceInfo = faceInfoService.selectByUserName("fun01");
        System.out.println(faceInfo);
    }

    @Test
    public void selectByUserId() {
        FaceInfo faceInfo = faceInfoService.selectByUserId("fui01");
        System.out.println(faceInfo);
    }

    @Test
    public void selectAll() {
        List<FaceInfo> faceInfos = faceInfoService.selectAll(page);
        System.out.println(faceInfos);
    }

    @Test
    public void selectCondition() {
    }

    @Test
    public void selectCondition1() {
    }
}