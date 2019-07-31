package com.summit.dao.test;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.summit.dao.entity.Alarm;
import com.summit.dao.entity.FaceInfo;
import com.summit.dao.entity.FileInfo;
import com.summit.dao.entity.LockInfo;
import com.summit.dao.entity.LockProcess;
import com.summit.dao.entity.SafeReport;
import com.summit.dao.repository.AlarmDao;
import com.summit.dao.repository.FaceInfoDao;
import com.summit.dao.repository.FileInfoDao;
import com.summit.dao.repository.LockInfoDao;
import com.summit.dao.repository.LockProcessDao;
import com.summit.dao.repository.SafeReportDao;
import com.summit.dao.repository.UserDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TestDao {
    @Autowired
    private LockProcessDao lockProcessDao;
    @Autowired
    private FaceInfoDao faceInfoDao;
    @Autowired
    private AlarmDao alarmDao;
    @Autowired
    private LockInfoDao lockInfoDao;
    @Autowired
    private SafeReportDao safeReportDao;

    @Test
    public void test(){
        Date date1 = null;
        Date date2 = null;
        try {
            date1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2019-7-24 00:00:00");
            date2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2019-7-31 00:00:00");
        } catch (ParseException e) {
            e.printStackTrace();
        }

//        Map<String , Object> map = new HashMap<>();
//        map.put("fileId","aaa");

        LockInfo lockInfo = new LockInfo();
        lockInfo.setStatus(1);
        List<LockInfo> lockInfos = lockInfoDao.selectCondition(lockInfo);
        System.out.println(lockInfos);

        SafeReport selSafeReport = safeReportDao.selectById("2re");
        System.out.println(selSafeReport);


        SafeReport safeReport = new SafeReport();
        safeReport.setLockCode("1nb");
        List<SafeReport> safeReports = safeReportDao.selectCondition(safeReport,date1,date2);
        System.out.println(safeReports);
        Alarm alarm = new Alarm();
        alarm.setProcessId("p1");
        List<Alarm> alarms = alarmDao.selectCondition(alarm, null, date2);
        System.out.println(alarms);

        FaceInfo faceInfo = new FaceInfo();
        faceInfo.setCardId("fgd");
        List<FaceInfo> faceInfos = faceInfoDao.selectCondition(faceInfo, null, date2);
        System.out.println(faceInfos);
        LockProcess lockProcess = new LockProcess();
        lockProcess.setUserName("1dfg");
        List<LockProcess> lockProcesses = lockProcessDao.selectCondition(lockProcess,date1,date2);
        System.out.println(lockProcesses);
    }

    @Test
    public void testInsert(){
        Alarm alarm = new Alarm();
        alarm.setProcessId("p2");

        int insert = -1;
        try {
            insert = alarmDao.insert(alarm);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(insert);
    }
    @Test
    public void testUpdate(){
        Alarm alarm = new Alarm();
        alarm.setAlarmName("alarm5");

        int update = -1;
        try {
            UpdateWrapper<Alarm> updateWrapper = new UpdateWrapper<>();
            update = alarmDao.update(alarm, updateWrapper.eq("process_id", "p1").or()
                    .eq("alarm_status",1));
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(update);
    }
}
