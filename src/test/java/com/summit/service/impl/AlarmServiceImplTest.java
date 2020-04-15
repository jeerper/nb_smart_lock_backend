package com.summit.service.impl;

import cn.hutool.core.util.NumberUtil;
import com.summit.cbb.utils.page.Page;
import com.summit.dao.entity.AccessControlInfo;
import com.summit.dao.entity.Alarm;
import com.summit.dao.entity.SimplePage;
import com.summit.dao.repository.AccessControlDao;
import com.summit.dao.repository.AlarmDao;
import com.summit.sdk.huawei.model.AlarmStatus;
import com.summit.sdk.huawei.model.WorkProcessType;
import com.summit.service.AccessControlService;
import com.summit.service.AlarmService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class AlarmServiceImplTest {
    @Autowired
    private AlarmService alarmService;
    @Autowired
    private AlarmDao alarmDao;
    @Autowired
    private AccessControlService accessControlService;
    @Autowired
    private AccessControlDao accessControlDao;


    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    SimplePage page = new SimplePage(1, 3);

    @Test
    public void insertAlarm() {
        Alarm alarm = new Alarm();
        alarm.setAccCtrlProId("123123213213123");
        alarm.setAlarmTime(new Date());
        alarm.setAlarmStatus(AlarmStatus.UNPROCESSED.getCode());
        alarm.setDescription("哈哈");
        alarmService.insertAlarm(alarm);
        log.debug(alarm.getAlarmId());


    }
    @Test
    public void accessControlService() {
        List<String> depts=new ArrayList<>();
        depts.add("1");
        AccessControlInfo accessControlInfo = accessControlDao.selectAccCtrlByLockCode("summit6",depts);
        System.out.println(accessControlInfo);
    }



    @Test
    public void delLockAlarmById() {
        //alarmService.delLockAlarmById("aid01");
        float faceAccCtrlprogress=0;
        faceAccCtrlprogress=100/120;
        System.out.println(faceAccCtrlprogress);
        float fx = new BigDecimal((float) 100/120).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
        System.out.println(fx);
        Double aDouble = toFloat(100, 11111);
        System.out.println("aDouble:"+aDouble);

        double div = NumberUtil.div(100, 120, 2);
        System.out.println("eeeeeeeee"+div);
        String s = NumberUtil.roundStr(div, 2);
        System.out.println("11111111"+s);

    }
    public static Double toFloat(int denominator,int numerator) {
        // TODO 自动生成的方法存根
        DecimalFormat df=new DecimalFormat("0.00");//设置保留位数
        return Double.valueOf(df.format((float)denominator/numerator));
    }

    @Test
    public void selectAll() {

        List<Alarm> alarms = alarmService.selectAll(null,null,14,2);
        System.out.println(alarms);
    }

    @Test
    public void selectAlarmById() {
       // Alarm alarm = alarmService.selectAlarmById("1167390622789668865");
        //System.out.println(alarm);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("河长", "eee");
        map.put("河段名称：", "weweqwe");
        map.put("开始日期：", "2019-10-2");
        map.put("结束日期：", "2019-10-6");
        map.put("巡河任务", "2019-10-6");
        System.out.println(map.toString());

    }

    @Test
    public void selectCountByCondition() {
        Alarm alarm  = new Alarm();
        alarm.setAlarmStatus(0);
        Integer count = alarmDao.selectCountByCondition(alarm,null,null,null);
        System.out.println(count);
    }

    @Test
    public void selectByAccCtrlProId() {
        Alarm alarm = alarmDao.selectByAccCtrlProId("1166896782903349250", null);
        System.out.println(alarm);
    }

    @Test
    public void selectAlarmCountByStatus() {
        Integer count = alarmService.selectAlarmCountByStatus(1);
        System.out.println(count);
    }

    @Test
    public void selectAlarmByName() throws ParseException {
        /*SimpleDateFormat facestartDf = new SimpleDateFormat("yyyy-MM");//设置日期格式
        Date date = new Date();
        String startTime = facestartDf.format(date);
        System.out.println(startTime);*/
       /* Date faceStartTime = CommonUtil.dateFormat.get().parse(startTime);
        System.out.println(faceStartTime);*/
        String orginFileName="nb_smart_lock源码.docx";
        int i = orginFileName.lastIndexOf(".");
        String substring = orginFileName.substring(orginFileName.lastIndexOf("."));
        System.out.println(substring);
    }

    @Test
    public void selectAlarmByStatus() {
        List<Alarm> alarms = alarmService.selectAlarmByStatus(1, null,null);
        System.out.println(alarms);
    }

    @Test
    public void selectAlarmByStatus1() {
      //  System.out.println(CameraUploadType.valueOf("Illegal_Alarm"));

          WorkProcessType workProcessType = WorkProcessType.getWorkProcessType(1);
          System.out.println(workProcessType.getCode());
//        System.out.println(CameraUploadType.Illegal_Alarm.getDescription());
//        System.out.println(CameraUploadType.valueOf("Illegal_Alarm").getCode());
    }

    @Test
    public void selectAlarmByLockCode() {
    }

    @Test
    public void selectAlarmByLockCode1() {
    }

    @Test
    public void selectAlarmByDeviceIp() {
        List<Alarm> alarms = alarmService.selectAlarmByDeviceIp("devip01", page);
        System.out.println(alarms);
    }

    @Test
    public void selectAlarmByDeviceIp1() {
    }

    @Test
    public void selectAlarmByDevId() {
    }

    @Test
    public void selectAlarmByDevId1() {
    }

    @Test
    public void selectAlarmCondition() throws Exception {
        Alarm alarm = new Alarm();
        alarm.setAccessControlName("门禁");
        Page<Alarm> alarmPage = alarmService.selectAlarmConditionByPage(alarm, null,null);
        System.out.println(alarmPage);
    }

    @Test
    public void selectAlarmCondition1() {

    }
    @Test
    public void selectAlarmCondition2() throws IOException {
        String path1="D:\\qianyy\\nb_smart_lock_backend\\snapshot\\1177104482461077505_Face.jpg";
        String path2="D:\\qianyy\\nb_smart_lock_backend\\snapshot\\1177104482461077506_Face.jpg";
        String image1 = imageToBase64Str(path1);
        String image2 = imageToBase64Str(path1);
       if (image1.equals(image2)){
           System.out.println("相同");
       }else {
           System.out.println("不相同");
       }
    }



    public String  imageToBase64Str(String path) throws IOException {
        FileInputStream fis = new FileInputStream(new File(path));
        byte[] read = new byte[1024];
        int len = 0;
        List<byte[]> blist=new ArrayList<byte[]>();
        int ttllen=0;
        while((len = fis.read(read))!= -1){
            byte[] dst=new byte[len];
            System.arraycopy(read, 0, dst, 0, len);
            ttllen+=len;
            blist.add(dst);
        }
        fis.close();
        byte[] dstByte=new byte[ttllen];
        int pos=0;
        for (int i=0;i<blist.size();i++){
            if (i==0){
                pos=0;
            }
            else{
                pos+=blist.get(i-1).length;
            }
            System.arraycopy(blist.get(i), 0, dstByte, pos, blist.get(i).length);
        }
        String baseStr= Base64.getEncoder().encodeToString(dstByte);
        System.out.println("baseStr: "+baseStr);
       /* byte[] op= Base64.getDecoder().decode(baseStr);
        FileOutputStream fos = new FileOutputStream(new File("D:\\mybiji\\copy.jpg"));
        fos.write(op,0,op.length );
        fos.flush();
        fos.close();*/
       return  baseStr;

    }


}