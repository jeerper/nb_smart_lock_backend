package com.summit.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.summit.cbb.utils.page.Page;
import com.summit.cbb.utils.page.Pageable;
import com.summit.common.util.UserAuthUtils;
import com.summit.constants.CommonConstants;
import com.summit.dao.entity.*;
import com.summit.dao.repository.AlarmDao;
import com.summit.dao.repository.CameraDeviceDao;
import com.summit.dao.repository.LockProcessDao;
import com.summit.service.AddAccCtrlprocessService;
import com.summit.service.AlarmService;
import com.summit.service.DeptsService;
import com.summit.util.CommonUtil;
import com.summit.util.PageConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class AlarmServiceImpl implements AlarmService {

    @Autowired
    private AlarmDao alarmDao;

    @Autowired
    private LockProcessDao lockProcessDao;
    @Autowired
    private CameraDeviceDao deviceDao;
    @Autowired
    private DeptsService deptsService;
    @Autowired
    private AddAccCtrlprocessService addAccCtrlprocessService;
    /**
     * 告警插入
     * @param alarm 告警对象
     * @return 不为-1则成功
     */
    @Override
    public int insertAlarm(Alarm alarm) {
        //todo 插入统计分析告警次数显示
        String accessControlId = alarm.getAccessControlId();
        AddAccCtrlprocess addccCtrlprocess = addAccCtrlprocessService.selectAddAccCtrlByAccCtrlID(accessControlId);
        if (null == addccCtrlprocess){
            AddAccCtrlprocess addAccCtrlprocess=new AddAccCtrlprocess();
            Integer alarmCount=1;
            addAccCtrlprocess.setAccessControlId(accessControlId);
            addAccCtrlprocess.setAlarmCount(alarmCount);
            int add=addAccCtrlprocessService.insert(addAccCtrlprocess);
        }else {
            int i=addAccCtrlprocessService.updateAddAccProcessAlarmCount(accessControlId);
        }
        return alarmDao.insert(alarm);
    }

    /**
     * 告警更新
     * @param alarm 告警对象
     * @return 不为-1则成功
     */
    @Override
    public int updateAlarm(Alarm alarm) {
        if(alarm == null){
            log.error("告警信息为空");
            return CommonConstants.UPDATE_ERROR;
        }
        UpdateWrapper<Alarm> updateWrapper = new UpdateWrapper<>();
        if(alarm.getAlarmId() != null){
            return alarmDao.update(alarm , updateWrapper.eq("alarm_id" , alarm.getAlarmId()));
        }
        return alarmDao.update(alarm , updateWrapper.eq("acc_ctrl_pro_id" , alarm.getAccCtrlProId()));
    }

    /**
     * 告警删除
     * @param alarmId 告警id
     * @return 不为-1则成功
     */
    @Override
    public int delLockAlarmById(String alarmId) {
        if(alarmId == null){
            log.error("告警id为空");
            return CommonConstants.UPDATE_ERROR;
        }
        UpdateWrapper<Alarm> wrapper = new UpdateWrapper<>();
        return alarmDao.delete(wrapper.eq("alarm_id" , alarmId));
    }

    /**
     * 告警批量删除
     * @param alarmIds 告警id列表
     * @return 不为-1则成功
     */
    @Override
    public int delAlarmByIdBatch(List<String> alarmIds) {
        if(alarmIds == null || alarmIds.isEmpty()){
            log.error("告警id为空");
            return CommonConstants.UPDATE_ERROR;
        }
        return alarmDao.deleteBatchIds(alarmIds);
    }

    /**
     * 查询所有
     * @param start 开始时间
     * @param end 截止时间
     * @return 告警记录列表
     */
    @Override
    public List<Alarm> selectAll(Date start, Date end, Integer current, Integer pageSize) {
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<Alarm> pageParam = null;
        if (current != null && pageSize != null) {
            pageParam = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(current, pageSize);
        }
        return alarmDao.selectCondition(pageParam,null, start, end, UserAuthUtils.getRoles());
    }

    /**
     * 根据告警Id查询唯一告警记录
     * @param alarmId 告警id
     * @return 唯一确定的告警记录
     */
    @Override
    public Alarm selectAlarmById(String alarmId) {
        return alarmDao.selectAlarmById(alarmId, null);
    }

    /**
     * 根据告警Idid查询唯一门禁信息,不考虑权限
     * @param alarmId 告警id
     * @return 唯一告警对象
     */
    @Override
    public Alarm selectAlarmByIdBeyondAuthority(String alarmId) {
        if(alarmId == null){
            log.error("告警id为空");
            return null;
        }
        return alarmDao.selectById(alarmId);
    }

    /**
     * 根据告警name（或者说类型）查询，用selectCondition实现，可指定时间段
     * @param alarmName 告警名
     * @param start 开始时间
     * @param end 截止时间
     * @return 告警记录列表
     */
    @Override
    public List<Alarm> selectAlarmByName(String alarmName, Date start, Date end, Integer current, Integer pageSize) {
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<Alarm> pageParam = null;

        if (current != null && pageSize != null) {
            pageParam = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(current, pageSize);
        }
        Alarm alarm = new Alarm();
        alarm.setAlarmName(alarmName);

        return alarmDao.selectCondition(pageParam,alarm, start, end, UserAuthUtils.getRoles());
    }

    /**
     * 根据告警name（或者说类型）查询，用selectCondition实现，不带时间重载
     * @param alarmName 告警名
     * @return 告警记录列表
     */
    @Override
    public List<Alarm> selectAlarmByName(String alarmName,Integer current, Integer pageSize) {
        return selectAlarmByName(alarmName, null, null, current,pageSize);
    }

    /**
     * 根据告警状态查询，用selectCondition实现，可指定时间段
     * @param alarmStatus 告警状态
     * @param start 开始时间
     * @param end 截止时间
     * @return 告警记录列表
     */
    @Override
    public List<Alarm> selectAlarmByStatus(Integer alarmStatus, Date start, Date end, Integer current, Integer pageSize) {
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<Alarm> pageParam = null;
        if (current != null && pageSize != null) {
            pageParam = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(current, pageSize);
        }
        Alarm alarm = new Alarm();
        alarm.setAlarmStatus(alarmStatus);
        return alarmDao.selectCondition(pageParam,alarm, start, end, UserAuthUtils.getRoles());
    }

    /**
     * 根据告警状态查询，用selectCondition实现，不带时间重载
     * @param alarmStatus 告警状态
     * @return 告警记录列表
     */
    @Override
    public List<Alarm> selectAlarmByStatus(Integer alarmStatus, Integer current, Integer pageSize) {

        return selectAlarmByStatus(alarmStatus, null, null, current,pageSize);
    }

    /**
     * 根据状态查询告警数量
     * @param alarmStatus 告警状态
     * @return 告警数量
     */
    @Override
    public Integer selectAlarmCountByStatus(Integer alarmStatus) throws Exception {
        if(alarmStatus == null){
            log.error("告警状态为空");
            return null;
        }
        List<String> dept_ids =new ArrayList<>();
        String currentDeptService = deptsService.getCurrentDeptService();
        JSONObject paramJson=new JSONObject();
        paramJson.put("pdept",currentDeptService);
        List<String> depts = deptsService.getDeptsByPdept(paramJson);
        if (!CommonUtil.isEmptyList(depts)){
            for (String dept_id:depts){
                dept_ids.add(dept_id);
            }
        }
        if (!CommonUtil.isEmptyList(dept_ids)){
            return alarmDao.selectAlarmCountByStatus(alarmStatus,dept_ids);
        }
        return null;

    }

    /**
     * 根据锁操作记录对应的锁编号查询告警，可指定时间段
     * @param lockCode 锁边号
     * @param start 开始时间
     * @param end 截止时间
     * @param page 分页对象
     * @return 告警记录列表
     */
    @Override
    public List<Alarm> selectAlarmByLockCode(String lockCode, Date start, Date end, SimplePage page) {
        if(lockCode == null){
            log.error("锁编号为空");
            return null;
        }
        PageConverter.convertPage(page);
        List<Alarm> alarms = new ArrayList<>();
        List<String> roles = UserAuthUtils.getRoles();
        List<LockProcess> lockProcesses = lockProcessDao.selectByLockCode(lockCode, page,roles);
        if (!CommonUtil.isEmptyList(lockProcesses)){
            for (LockProcess lockProcess: lockProcesses) {
                alarms.add(alarmDao.selectByAccCtrlProId(lockProcess.getProcessId(), roles));
            }
        }

        return alarms;
    }

    /**
     * 根据锁操作记录对应的锁编号查询告警，不带时间重载
     * @param lockCode 锁边号
     * @param page 分页对象
     * @return 告警记录列表
     */
    @Override
    public List<Alarm> selectAlarmByLockCode(String lockCode, SimplePage page) {
        if(lockCode == null){
            log.error("锁编号为空");
            return null;
        }
        return selectAlarmByLockCode(lockCode, null, null, page);
    }

    /**
     * 根据锁操作记录对应的设备ip地址查询告警，可指定时间段
     * @param deviceIp 摄像头ip地址
     * @param start 开始时间
     * @param end 截止时间
     * @param page 分页对象
     * @return 告警记录列表
     */
    @Override
    public List<Alarm> selectAlarmByDeviceIp(String deviceIp, Date start, Date end, SimplePage page) {
        if(deviceIp == null){
            log.error("设备ip地址为空");
            return null;
        }
        PageConverter.convertPage(page);
        List<Alarm> alarms = new ArrayList<>();
        LockProcess lp = new LockProcess();
        lp.setDeviceIp(deviceIp);
        List<String> roles = UserAuthUtils.getRoles();
        List<LockProcess> lockProcesses = lockProcessDao.selectCondition(lp, start, end, page,roles);
        if (!CommonUtil.isEmptyList(lockProcesses)){
            for (LockProcess lockProcess: lockProcesses) {
                alarms.add(alarmDao.selectByAccCtrlProId(lockProcess.getProcessId(), roles));
            }
        }
        return alarms;
    }

    /**
     * 根据锁操作记录对应的设备ip地址查询告警，不带时间重载
     * @param deviceIp 摄像头ip地址
     * @param page 分页对象
     * @return 告警记录列表
     */
    @Override
    public List<Alarm> selectAlarmByDeviceIp(String deviceIp, SimplePage page) {
        if(deviceIp == null){
            log.error("设备ip地址为空");
            return null;
        }
        return selectAlarmByDeviceIp(deviceIp, null, null, page);
    }

    /**
     * 根据锁操作记录对应的设备id查询告警，可指定时间段
     * @param devId 摄像头设备id
     * @param start 开始时间
     * @param end 截止时间
     * @param page 分页对象
     * @return 告警记录列表
     */
    @Override
    public List<Alarm> selectAlarmByDevId(String devId, Date start, Date end, SimplePage page) {
        if(devId == null){
            log.error("设备id为空");
            return null;
        }
        PageConverter.convertPage(page);
        List<Alarm> alarms = new ArrayList<>();
        CameraDevice cameraDevice = deviceDao.selectDeviceById(devId);
        if(cameraDevice == null){
            log.warn("此设备id不存在");
            return null;
        }
        LockProcess lp = new LockProcess();
        lp.setDeviceIp(cameraDevice.getDeviceIp());
        List<String> roles = UserAuthUtils.getRoles();
        List<LockProcess> lockProcesses = lockProcessDao.selectCondition(lp, start, end, page,roles);
        if (!CommonUtil.isEmptyList(lockProcesses)){
            for (LockProcess lockProcess: lockProcesses) {
                alarms.add(alarmDao.selectByAccCtrlProId(lockProcess.getProcessId(), roles));
            }
        }
        return alarms;
    }

    /**
     * 根据锁操作记录对应的设备id查询告警，不带时间重载
     * @param devId 摄像头设备id
     * @param page 分页对象
     * @return 告警记录列表
     */
    @Override
    public List<Alarm> selectAlarmByDevId(String devId, SimplePage page) {
        if(devId == null){
            log.error("设备id为空");
            return null;
        }
        return selectAlarmByDevId(devId, null, null, page);
    }

    /**
     * 根据alarm对象所带条件查询，可指定时间段
     * @param alarm 告警对象
     * @param start 开始时间
     * @param end 截止时间
     * @return 告警记录Page对象
     */
    @Override
    public Page<Alarm> selectAlarmConditionByPage(Alarm alarm, Date start, Date end,  Integer current, Integer pageSize) throws Exception {
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<Alarm> pageParam = null;
        if (current != null && pageSize != null) {
            pageParam = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(current, pageSize);
        }
        List<String> dept_ids =new ArrayList<>();
        String currentDeptService = deptsService.getCurrentDeptService();
        JSONObject paramJson=new JSONObject();
        paramJson.put("pdept",currentDeptService);
        List<String> depts = deptsService.getDeptsByPdept(paramJson);
        if (!CommonUtil.isEmptyList(depts)){
            for (String dept_id:depts){
                dept_ids.add(dept_id);
            }
        }
        if (!CommonUtil.isEmptyList(dept_ids)){
            List<Alarm> alarms = alarmDao.selectCondition(pageParam,alarm, start, end, dept_ids);
            Pageable pageable = null;
            if (pageParam != null) {
                pageable = new Pageable((int) pageParam.getTotal(), (int) pageParam.getPages(), (int) pageParam.getCurrent(), (int) pageParam.getSize()
                        , alarms.size());
            }
            return new Page<>(alarms, pageable);
        }
       return null;
    }

    /**
     * 根据alarm对象所带条件查询，不带时间段重载
     * @param alarm 告警对象
     * @return 告警记录Page对象
     */
    @Override
    public Page<Alarm> selectAlarmConditionByPage(Alarm alarm, Integer current, Integer pageSize) throws Exception {

        return selectAlarmConditionByPage(alarm, null, null, current,pageSize);
    }
}
