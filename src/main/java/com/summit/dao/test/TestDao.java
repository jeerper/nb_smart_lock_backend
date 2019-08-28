package com.summit.dao.test;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.summit.dao.entity.AccCtrlProcess;
import com.summit.dao.entity.AccessControlInfo;
import com.summit.dao.entity.Alarm;
import com.summit.dao.entity.LockInfo;
import com.summit.dao.entity.LockProcess;
import com.summit.dao.entity.LockRole;
import com.summit.dao.entity.SafeReport;
import com.summit.dao.entity.SimplePage;
import com.summit.dao.repository.AccCtrlProcessDao;
import com.summit.dao.repository.AccCtrlRealTimeDao;
import com.summit.dao.repository.AccessControlDao;
import com.summit.dao.repository.AlarmDao;
import com.summit.dao.repository.FaceInfoDao;
import com.summit.dao.repository.LockInfoDao;
import com.summit.dao.repository.LockProcessDao;
import com.summit.dao.repository.LockRoleDao;
import com.summit.dao.repository.SafeReportDao;
import com.summit.service.AlarmService;
import com.summit.service.LockInfoService;
import com.summit.service.LockRecordService;
import com.summit.util.LockAuthCtrl;
import io.swagger.models.auth.In;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
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
    private LockInfoService lockInfoService;
    @Autowired
    private SafeReportDao safeReportDao;
    @Autowired
    private LockRoleDao lockRoleDao;

    @Autowired
    private AlarmService alarmService;
    @Autowired
    private LockRecordService lockRecordService;

    @Autowired
    private AccCtrlProcessDao accCtrlProcessDao;
    @Autowired
    private AccessControlDao accessControlDao;
    @Autowired
    private AccCtrlRealTimeDao accCtrlRealTimeDao;


    SimpleDateFormat temeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    List<String> roles = LockAuthCtrl.getRoles();

    @Test
    public void selectHaveHistoryCountByPage() throws ParseException {
        int  count = accessControlDao.selectHaveHistoryCountByPage(null,roles);

        System.out.println(count);
    }
    @Test
    public void selectAlarmById() throws ParseException {
        Alarm alarm = alarmDao.selectAlarmById("1159043604048580609",roles);

        System.out.println(alarm);
    }
    @Test
    public void selectAlarmByAccCtrlProId() throws ParseException {
        Alarm alarm = alarmDao.selectByAccCtrlProId("acp001", roles);
        System.out.println(alarm);
    }
    @Test
    public void selectAlarmCondition() throws ParseException {
        Alarm alarm = new Alarm();
//        alarm.setAlarmId("1159043604048580609");
        alarm.setAlarmStatus(0);
        AccCtrlProcess accCtrlProcess = new AccCtrlProcess();
        accCtrlProcess.setAccessControlName("门禁1");
        alarm.setAccCtrlProcess(accCtrlProcess);
        List<Alarm> alarms = alarmDao.selectCondition(alarm, null, null, null, roles);
        System.out.println(alarms);
    }
    @Test
    public void selectAlarmCountByStatus() throws ParseException {
        Integer result = alarmDao.selectAlarmCountByStatus(0, roles);
        System.out.println(result);
    }
    @Test
    public void insertACPRecord() throws ParseException {
        AccCtrlProcess accCtrlProcess = new AccCtrlProcess("acp002","ac01","门禁1","did03","192.168.141.141","entry","lid05","NB100002",null,"uid01","un01",1,dateFormat.parse("1994-01-01"),
                "甘肃","庆阳",0,"123",98f,"白名单",2,1,"success","","","","","","","","","",
                null,null,null,null,temeFormat.parse("2019-08-12 15:10:10"), 1);
        int record = accCtrlProcessDao.insertRecord(accCtrlProcess);

        System.out.println(record);
    }

    @Test
    public void selectAccCtrlProcessByLockCode() throws ParseException {
        List<AccCtrlProcess> records = accCtrlProcessDao.selectAccCtrlProcessByLockCode("NB100002", null, roles);

        System.out.println(records);
    }
    @Test
    public void selectAccCtrlProcessById() throws ParseException {
        AccCtrlProcess record = accCtrlProcessDao.selectAccCtrlProcessById("acp001", roles);

        System.out.println(record);
    }
    @Test
    public void selectAccCtrlProcessCondition() throws ParseException {
        AccCtrlProcess accCtrlProcess = new AccCtrlProcess();
        accCtrlProcess.setAccCtrlProId("acp001");
        accCtrlProcess.setLockCode("NB100002");
        List<AccCtrlProcess> records = accCtrlProcessDao.selectCondition(accCtrlProcess, null,null,null, roles);

        System.out.println(records);
    }

    @Test
    public void insertACRecord() throws ParseException {
        AccessControlInfo accessControlInfo = new AccessControlInfo("ac02","门禁2","lid06","lc06",null,"did01","devip01",null,"did02","devip02",null,2,"un02",
                temeFormat.parse("2019-08-13 11:10:10"),temeFormat.parse("2019-08-13 12:11:10"),null,"东经107.40","北纬33.42");
        int record = accessControlDao.insert(accessControlInfo);
        System.out.println(record);
    }
    @Test
    public void selectAccCtrlById() throws ParseException {
        AccessControlInfo record = accessControlDao.selectAccCtrlById("ac01", roles);
        System.out.println(record);
    }
    @Test
    public void selectAccCtrlByLockCode() throws ParseException {
        AccessControlInfo record = accessControlDao.selectAccCtrlByLockCode("NB100002", roles);
        System.out.println(record);
    }

    @Test
    public void selectCondition() throws ParseException {
        AccessControlInfo accessControlInfo = new AccessControlInfo();
        accessControlInfo.setLockCode("NB100002");
        List<AccessControlInfo> records = accessControlDao.selectCondition(accessControlInfo, null,roles);
        System.out.println(records);
    }

    @Test
    public void selectAllHaveHistory() throws ParseException {
        List<AccessControlInfo> records = accessControlDao.selectHaveHistoryByPage(null, roles);
        System.out.println(records);
    }


    @Test
    public void testLockRole(){
        Map<String,Object> map = new HashMap<>();
        map.put("role_id","r1");
        List<LockRole> lockRoles = lockRoleDao.selectByMap(map);
        QueryWrapper<LockRole> queryWrapper = new QueryWrapper<>();
        lockRoleDao.selectList(queryWrapper.eq("role_id", "r1"));
        System.out.println(lockRoles);
    }

    @Test
    public void testLockId(){
        SafeReport safeReport = safeReportDao.selectSafeReportById("1re");
        System.out.println(safeReport);
    }
    @Test
    public void testFilter(){
        List<LockInfo> lockInfos = lockInfoService.selectLockInfoByPage(null).getContent();
        System.out.println(lockInfos);
//        LockAuthCtrl.toFilterLocks(lockInfos);

        System.out.println(lockInfos);
    }

    @Test
    public void testFilterLP(){
        List<LockProcess> lockProcesses = lockRecordService.selectAll(null);
        System.out.println(lockProcesses);
//        LockAuthCtrl.toFilterLockProcesses(lockProcesses);

        System.out.println(lockProcesses);
    }

    @Test
    public void testFilterAlarm(){
        List<Alarm> alarms = alarmService.selectAll(null,null,null);
        System.out.println(alarms);
//        LockAuthCtrl.toFilterAlarms(alarms);

        System.out.println(alarms);
    }

    @Test
    public void testQueryAlarmCount(){
        Integer alarmCount = alarmDao.selectAlarmCountByStatus(1, roles);
        System.out.println(alarmCount);
    }
    @Test
    public void testupdateAlarm(){
    }

    @Test
    public void testLockCode(){
        List<SafeReport> safeReports = safeReportDao.selectByLockCode("1nb",null,null,null);
        System.out.println(safeReports);
    }

    @Test
    public void testLockRoleAuth(){
        List<SafeReport> safeReports = safeReportDao.selectReportByRoles(Arrays.asList("r1"), null, null, null);
        System.out.println(safeReports);
    }
    @Test
    public void testLock2(){
        List<LockProcess> lockProcesses = lockProcessDao.selectCondition(null, null, null, null,roles);
        System.out.println(lockProcesses);
    }


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

//        LockInfo lockInfo = new LockInfo();
//        lockInfo.setStatus(1);
//        List<LockInfo> lockInfos = lockInfoDao.selectCondition(lockInfo);
//        System.out.println(lockInfos);
//
//        SafeReport selSafeReport = safeReportDao.selectById("2re");
//        System.out.println(selSafeReport);
//
//
//        SafeReport safeReport = new SafeReport();
//        safeReport.setLockCode("1nb");
//        List<SafeReport> safeReports = safeReportDao.selectCondition(safeReport,date1,date2);
//        System.out.println(safeReports);
//        Alarm alarm = new Alarm();
//        alarm.setProcessId("p1");
//        List<Alarm> alarms = alarmDao.selectCondition(alarm, null, date2);
//        System.out.println(alarms);
//
//        FaceInfoEntity faceInfo = new FaceInfoEntity();
//        faceInfo.setCardId("fgd");
//        List<FaceInfoEntity> faceInfos = faceInfoDao.selectCondition(faceInfo, null, date2);
//        System.out.println(faceInfos);
        LockProcess lockProcess = new LockProcess();
        lockProcess.setUserName("1dfg");
        List<LockProcess> lockProcesses = lockProcessDao.selectCondition(lockProcess,date1,date2,null,roles);
        System.out.println(lockProcesses);
    }

    @Test
    public void testInsert(){
        Alarm alarm = new Alarm();
        alarm.setAccCtrlProId("p2");

        int insert = -1;
        try {
            insert = alarmDao.insert(alarm);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(insert);
    }

    @Test
    public void testInsAndUpLock(){
        LockProcess process = lockProcessDao.selectLockProcessById("p1",roles);
//        process.setProcessId("2");
        process.setDeviceIp("222");
        process.getFaceMatch().setFileId("6");
        int insert = -1;
        try {
//            insert = lockProcessDao.insertRecord(process);
            insert = lockProcessDao.updateRecord(process);
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
