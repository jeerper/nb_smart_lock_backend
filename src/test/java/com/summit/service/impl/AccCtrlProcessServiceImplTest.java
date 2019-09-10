package com.summit.service.impl;

import cn.hutool.core.date.DateTime;
import com.summit.cbb.utils.page.Page;
import com.summit.dao.entity.AccCtrlProcess;
import com.summit.sdk.huawei.model.CardType;
import com.summit.sdk.huawei.model.FaceInfo;
import com.summit.sdk.huawei.model.FaceLibType;
import com.summit.sdk.huawei.model.Gender;
import com.summit.service.AccCtrlProcessService;
import com.summit.service.AccCtrlRealTimeService;
import com.summit.util.AccCtrlProcessUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AccCtrlProcessServiceImplTest {

    @Autowired
    private AccCtrlProcessService accCtrlProcessService;
    @Autowired
    private AccCtrlProcessUtil accCtrlProcessUtil;
    @Autowired
    private AccCtrlRealTimeService accCtrlRealTimeService;

    @Test
    public void selectAccCtrlProcessById() {
        AccCtrlProcess accCtrlProcess = accCtrlProcessService.selectAccCtrlProcessById("1166534285776130050");
        System.out.println(accCtrlProcess);
    }
    @Test
    public void selectAccCtrlProcessCondition() {
        AccCtrlProcess accCtrlProcess = new AccCtrlProcess();
//        accCtrlProcess.setAccCtrlProId("1163356805951225858");
//        accCtrlProcess.setAccessControlName("门");
//        accCtrlProcess.setProcessType(3);
        Page<AccCtrlProcess> accCtrlProcessPage = accCtrlProcessService.selectAccCtrlProcessCondition(accCtrlProcess,null);
        System.out.println(accCtrlProcessPage);
    }
    @Test
    public void selectAccCtrlProcessByLockCode() {
        List<AccCtrlProcess> accCtrlProcesss = accCtrlProcessService.selectAccCtrlProcessByLockCode("NB100002", null, null, null);
        System.out.println(accCtrlProcesss);
    }

    private FaceInfo getFace1() {
        FaceInfo faceInfo = new FaceInfo();
        faceInfo.setName("小明");
        faceInfo.setGender(Gender.PU_MALE);
        faceInfo.setProvince("北极");
        faceInfo.setCity("南京");
        faceInfo.setBirthday("1999-09-09");
        faceInfo.setFaceMatchRate(93.1f);
        faceInfo.setFaceLibType(FaceLibType.FACE_LIB_WHITE);
        faceInfo.setFaceLibName("白名单");
        faceInfo.setCardType(CardType.IDENTITY);
        faceInfo.setCardId("01");
        faceInfo.setDeviceIp("1.6.6.6");
        faceInfo.setPicSnapshotTime(new DateTime());
        return faceInfo;
    }
    private FaceInfo getFace2() {
        FaceInfo faceInfo = new FaceInfo();
        faceInfo.setName("小花");
        faceInfo.setGender(Gender.PU_FEMALE);
        faceInfo.setProvince("上海");
        faceInfo.setCity("上海市");
        faceInfo.setBirthday("1992-09-09");
        faceInfo.setFaceMatchRate(92.1f);
        faceInfo.setFaceLibType(FaceLibType.FACE_LIB_WHITE);
        faceInfo.setFaceLibName("白名单");
        faceInfo.setCardType(CardType.IDENTITY);
        faceInfo.setCardId("02");
        faceInfo.setDeviceIp("1.6.6.7");
        faceInfo.setPicSnapshotTime(new DateTime());
        return faceInfo;
    }
    private FaceInfo getFace3() {
        FaceInfo faceInfo = new FaceInfo();
        faceInfo.setName("小红");
        faceInfo.setGender(Gender.PU_FEMALE);
        faceInfo.setProvince("陕西");
        faceInfo.setCity("西安");
        faceInfo.setBirthday("1995-09-09");
        faceInfo.setFaceMatchRate(93.2f);
        faceInfo.setFaceLibType(FaceLibType.FACE_LIB_WHITE);
        faceInfo.setFaceLibName("白名单");
        faceInfo.setCardType(CardType.IDENTITY);
        faceInfo.setCardId("03");
        faceInfo.setDeviceIp("5.5.5.59");
        faceInfo.setPicSnapshotTime(new DateTime());
        return faceInfo;
    }
    private FaceInfo getFace4() {
        FaceInfo faceInfo = new FaceInfo();
        faceInfo.setName("小艳");
        faceInfo.setGender(Gender.PU_FEMALE);
        faceInfo.setProvince("甘肃");
        faceInfo.setCity("xx");
        faceInfo.setBirthday("1999-09-09");
        faceInfo.setFaceMatchRate(96.1f);
        faceInfo.setFaceLibType(FaceLibType.FACE_LIB_WHITE);
        faceInfo.setFaceLibName("白名单");
        faceInfo.setCardType(CardType.IDENTITY);
        faceInfo.setCardId("04");
        faceInfo.setDeviceIp("1.1.1.3");
        faceInfo.setPicSnapshotTime(new DateTime());
        return faceInfo;
    }
    private FaceInfo getFace5() {
        FaceInfo faceInfo = new FaceInfo();
        faceInfo.setName("小强");
        faceInfo.setGender(Gender.PU_MALE);
        faceInfo.setProvince("陕西");
        faceInfo.setCity("西安");
        faceInfo.setBirthday("1999-09-09");
        faceInfo.setFaceMatchRate(93.1f);
        faceInfo.setFaceLibType(FaceLibType.FACE_LIB_WHITE);
        faceInfo.setFaceLibName("白名单");
        faceInfo.setCardType(CardType.IDENTITY);
        faceInfo.setCardId("05");
        faceInfo.setDeviceIp("192.168.153.2");
        faceInfo.setPicSnapshotTime(new DateTime());
        return faceInfo;
    }
    private FaceInfo getFace6() {
        FaceInfo faceInfo = new FaceInfo();
        faceInfo.setName("小林");
        faceInfo.setGender(Gender.PU_MALE);
        faceInfo.setProvince("新疆");
        faceInfo.setCity("焉耆");
        faceInfo.setBirthday("1999-09-09");
        faceInfo.setFaceMatchRate(93.1f);
        faceInfo.setFaceLibType(FaceLibType.FACE_LIB_WHITE);
        faceInfo.setFaceLibName("白名单");
        faceInfo.setCardType(CardType.IDENTITY);
        faceInfo.setCardId("06");
        faceInfo.setDeviceIp("192.168.153.3");
        faceInfo.setPicSnapshotTime(new DateTime());
        return faceInfo;
    }
    private FaceInfo getFace7() {
        FaceInfo faceInfo = new FaceInfo();
        faceInfo.setName("小李");
        faceInfo.setGender(Gender.PU_MALE);
        faceInfo.setProvince("云南");
        faceInfo.setCity("昆明");
        faceInfo.setBirthday("1999-09-09");
        faceInfo.setFaceMatchRate(93.1f);
        faceInfo.setFaceLibType(FaceLibType.FACE_LIB_WHITE);
        faceInfo.setFaceLibName("白名单");
        faceInfo.setCardType(CardType.IDENTITY);
        faceInfo.setCardId("07");
        faceInfo.setDeviceIp("2.3.4.5");
        faceInfo.setPicSnapshotTime(new DateTime());
        return faceInfo;
    }
    private FaceInfo getFace8() {
        FaceInfo faceInfo = new FaceInfo();
        faceInfo.setName("王老五");
        faceInfo.setGender(Gender.PU_MALE);
        faceInfo.setProvince("北极");
        faceInfo.setCity("南极");
        faceInfo.setBirthday("1999-09-09");
        faceInfo.setFaceMatchRate(93.1f);
        faceInfo.setFaceLibType(FaceLibType.FACE_LIB_WHITE);
        faceInfo.setFaceLibName("白名单");
        faceInfo.setCardType(CardType.IDENTITY);
        faceInfo.setCardId("08");
        faceInfo.setDeviceIp("2.3.4.6");
        faceInfo.setPicSnapshotTime(new DateTime());
        return faceInfo;
    }
    private FaceInfo getFace9() {
        FaceInfo faceInfo = new FaceInfo();
        faceInfo.setName("李大侠");
        faceInfo.setGender(Gender.PU_MALE);
        faceInfo.setProvince("江苏");
        faceInfo.setCity("南京");
        faceInfo.setBirthday("1999-09-09");
        faceInfo.setFaceMatchRate(93.1f);
        faceInfo.setFaceLibType(FaceLibType.FACE_LIB_WHITE);
        faceInfo.setFaceLibName("白名单");
        faceInfo.setCardType(CardType.IDENTITY);
        faceInfo.setCardId("09");
        faceInfo.setDeviceIp("22.33.44.55");
        faceInfo.setPicSnapshotTime(new DateTime());
        return faceInfo;
    }
    private FaceInfo getFace10() {
        FaceInfo faceInfo = new FaceInfo();
        faceInfo.setName("小刘");
        faceInfo.setGender(Gender.PU_MALE);
        faceInfo.setProvince("广东");
        faceInfo.setCity("陆丰");
        faceInfo.setBirthday("1999-09-09");
        faceInfo.setFaceMatchRate(93.1f);
        faceInfo.setFaceLibType(FaceLibType.FACE_LIB_WHITE);
        faceInfo.setFaceLibName("白名单");
        faceInfo.setCardType(CardType.IDENTITY);
        faceInfo.setCardId("10");
        faceInfo.setDeviceIp("22.33.44.56");
        faceInfo.setPicSnapshotTime(new DateTime());
        return faceInfo;
    }
    private FaceInfo getFace11() {
        FaceInfo faceInfo = new FaceInfo();
        faceInfo.setName("小张");
        faceInfo.setGender(Gender.PU_MALE);
        faceInfo.setProvince("广西");
        faceInfo.setCity("桂林");
        faceInfo.setBirthday("1999-09-09");
        faceInfo.setFaceMatchRate(93.1f);
        faceInfo.setFaceLibType(FaceLibType.FACE_LIB_WHITE);
        faceInfo.setFaceLibName("白名单");
        faceInfo.setCardType(CardType.IDENTITY);
        faceInfo.setCardId("11");
        faceInfo.setDeviceIp("192.168.200.1");
        faceInfo.setPicSnapshotTime(new DateTime());
        return faceInfo;
    }
    private FaceInfo getFace12() {
        FaceInfo faceInfo = new FaceInfo();
        faceInfo.setName("小小");
        faceInfo.setGender(Gender.PU_MALE);
        faceInfo.setProvince("美国");
        faceInfo.setCity("纽约");
        faceInfo.setBirthday("1999-09-09");
        faceInfo.setFaceMatchRate(93.1f);
        faceInfo.setFaceLibType(FaceLibType.FACE_LIB_WHITE);
        faceInfo.setFaceLibName("白名单");
        faceInfo.setCardType(CardType.IDENTITY);
        faceInfo.setCardId("12");
        faceInfo.setDeviceIp("192.168.200.2");
        faceInfo.setPicSnapshotTime(new DateTime());
        return faceInfo;
    }
    private FaceInfo getFace13() {
        FaceInfo faceInfo = new FaceInfo();
        faceInfo.setName("大大");
        faceInfo.setGender(Gender.PU_MALE);
        faceInfo.setProvince("山西");
        faceInfo.setCity("太阳");
        faceInfo.setBirthday("1999-09-09");
        faceInfo.setFaceMatchRate(93.1f);
        faceInfo.setFaceLibType(FaceLibType.FACE_LIB_WHITE);
        faceInfo.setFaceLibName("白名单");
        faceInfo.setCardType(CardType.IDENTITY);
        faceInfo.setCardId("13");
        faceInfo.setDeviceIp("6.6.6.6");
        faceInfo.setPicSnapshotTime(new DateTime());
        return faceInfo;
    }
    private FaceInfo getFace14() {
        FaceInfo faceInfo = new FaceInfo();
        faceInfo.setName("中中");
        faceInfo.setGender(Gender.PU_MALE);
        faceInfo.setProvince("浙江");
        faceInfo.setCity("杭州");
        faceInfo.setBirthday("1995-09-09");
        faceInfo.setFaceMatchRate(93.1f);
        faceInfo.setFaceLibType(FaceLibType.FACE_LIB_WHITE);
        faceInfo.setFaceLibName("白名单");
        faceInfo.setCardType(CardType.IDENTITY);
        faceInfo.setCardId("14");
        faceInfo.setDeviceIp("2.4.6.8");
        faceInfo.setPicSnapshotTime(new DateTime());
        return faceInfo;
    }
}