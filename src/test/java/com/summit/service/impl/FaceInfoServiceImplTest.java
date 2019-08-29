package com.summit.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.summit.cbb.utils.page.Page;
import com.summit.dao.entity.FaceInfo;
import com.summit.dao.entity.FaceInfoEntity;
import com.summit.dao.entity.FileInfo;
import com.summit.dao.entity.SimplePage;
import com.summit.dao.repository.FaceInfoManagerDao;
import com.summit.entity.FaceInfoManagerEntity;
import com.summit.service.FaceInfoService;
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
public class FaceInfoServiceImplTest {
    @Autowired
    private FaceInfoService faceInfoService;
    @Autowired
    private FaceInfoManagerDao  faceInfoManagerDao;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    SimplePage page = new SimplePage(1, 3);
    @Test
    public void insertFaceInfo() throws ParseException {
        FileInfo facePanorama = new FileInfo("10","10n","10p","10d");
        FileInfo facePic = new FileInfo("11","11n","11p","11d");
        FileInfo faceMatch = new FileInfo("12","12n","12p","12d");
        FaceInfoEntity faceInfo = new FaceInfoEntity("fi03","user01","un03",1,dateFormat.parse("2019-7-28 12:00:00"),
                "province03","city03",1,"card03",0.8f,"白名单",2,
                facePanorama,facePic,faceMatch,"devip01",dateFormat.parse("2019-7-29 12:09:09"));

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
        FaceInfoEntity faceInfo = faceInfoService.selectFaceInfoById("fi01");
        System.out.println(faceInfo);
    }

    @Test
    public void selectByUserName() {
        FaceInfoEntity faceInfo = faceInfoService.selectByUserName("fun01");
        System.out.println(faceInfo);
    }

    @Test
    public void selectByUserId() {
        FaceInfoEntity faceInfo = faceInfoService.selectByUserId("fui01");
        System.out.println(faceInfo);
    }

    @Test
    public void selectAll() {
        List<FaceInfoEntity> faceInfos = faceInfoService.selectAll(page);
        System.out.println(faceInfos);
    }

    @Test
    public void selectCondition() {
    }

    @Test
    public void selectCondition1() {
    }
    @Test
    public void select(){
        FaceInfoManagerEntity faceInfoManagerEntity = new FaceInfoManagerEntity();
        System.out.println(faceInfoManagerEntity);
        Page<Object> page = new Page<>();
        List<FaceInfo> faceInfoList = faceInfoManagerDao.selectFaceInfoByPage(faceInfoManagerEntity, null);
        System.out.println(faceInfoList);
    }
    @Test
    public void select2(){
        QueryWrapper<FaceInfo> wrapper=new QueryWrapper<>();
        List<FaceInfo> faceInfoList = faceInfoManagerDao.selectList(wrapper);
        System.out.println(faceInfoList);
    }
}