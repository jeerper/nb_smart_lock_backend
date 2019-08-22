package com.summit.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.summit.cbb.utils.page.Page;
import com.summit.cbb.utils.page.Pageable;
import com.summit.common.entity.UserInfo;
import com.summit.common.web.filter.UserContextHolder;
import com.summit.constants.CommonConstants;
import com.summit.dao.entity.AccCtrlProcess;
import com.summit.dao.entity.AccCtrlRole;
import com.summit.dao.entity.AccessControlInfo;
import com.summit.dao.entity.Alarm;
import com.summit.dao.entity.CameraDevice;
import com.summit.dao.entity.LockInfo;
import com.summit.dao.entity.SimplePage;
import com.summit.dao.repository.AccCtrlProcessDao;
import com.summit.dao.repository.AccCtrlRoleDao;
import com.summit.dao.repository.AccessControlDao;
import com.summit.dao.repository.AlarmDao;
import com.summit.dao.repository.CameraDeviceDao;
import com.summit.dao.repository.LockInfoDao;
import com.summit.sdk.huawei.model.AlarmType;
import com.summit.sdk.huawei.model.DeviceType;
import com.summit.service.AccCtrlProcessService;
import com.summit.service.AccessControlService;
import com.summit.service.AlarmService;
import com.summit.service.CameraDeviceService;
import com.summit.service.LockInfoService;
import com.summit.util.CommonUtil;
import com.summit.util.LockAuthCtrl;
import com.summit.util.PageConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class AccessControlServiceImpl implements AccessControlService {

    @Autowired
    private AccessControlDao accessControlDao;
    @Autowired
    private LockInfoDao lockInfoDao;
    @Autowired
    private CameraDeviceDao cameraDeviceDao;
    @Autowired
    private AccCtrlRoleDao accCtrlRoleDao;

    @Autowired
    private LockInfoService lockInfoService;
    @Autowired
    private CameraDeviceService cameraDeviceService;
    @Autowired
    private AlarmDao alarmDao;
    @Autowired
    private AccCtrlProcessDao accCtrlProcessDao;

    /**
     * 根据门禁id查询唯一门禁信息
     * @param accessControlId 门禁id
     * @return 唯一门禁信息对象
     */
    @Override
    public AccessControlInfo selectAccCtrlById(String accessControlId) {
        if(accessControlId == null){
            log.error("门禁id为空");
            return null;
        }
        return accessControlDao.selectAccCtrlById(accessControlId, LockAuthCtrl.getRoles());
    }

    /**
     * 根据门禁id查询唯一门禁信息,不考虑权限
     * @param accessControlId 门禁id
     * @return 唯一门禁信息对象
     */
    @Override
    public AccessControlInfo selectAccCtrlByIdBeyondAuthority(String accessControlId) {
        if(accessControlId == null){
            log.error("门禁id为空");
            return null;
        }
        return accessControlDao.selectById(accessControlId);
    }

    /**
     * 根据锁编号查询唯一门禁信息
     * @param lockCode 锁编号
     * @return 唯一门禁信息对象
     */
    @Override
    public AccessControlInfo selectAccCtrlByLockCode(String lockCode) {
        if(lockCode == null){
            log.error("锁编号为空");
            return null;
        }
        return accessControlDao.selectAccCtrlByLockCode(lockCode, LockAuthCtrl.getRoles());
    }

    /**
     * 分页查询全部门禁信息
     * @param page 分页对象
     * @return 门禁信息列表
     */
    @Override
    public Page<AccessControlInfo> selectAccCtrlByPage(SimplePage page) {

        List<AccessControlInfo> accessControls = accessControlDao.selectCondition(new AccessControlInfo(), null, LockAuthCtrl.getRoles());
        int rowsCount = accessControls == null ? 0 : accessControls.size();
        Pageable pageable = PageConverter.getPageable(page, rowsCount);
        PageConverter.convertPage(page);
        List<AccessControlInfo> accessControlInfos = accessControlDao.selectCondition(new AccessControlInfo(), page, LockAuthCtrl.getRoles());
        Page<AccessControlInfo> backPage = new Page<>();
        backPage.setContent(accessControlInfos);
        backPage.setPageable(pageable);
        return backPage;
    }

    /**
     * 分页查询全部有操作记录的门禁信息
     * @param page 分页对象
     * @return 门禁信息列表
     */
    @Override
    public Page<AccessControlInfo> selectHaveHistoryByPage(SimplePage page) {
        //转换当前页之前设置当前页
        List<String> roles = LockAuthCtrl.getRoles();
        Integer rowsCount = accessControlDao.selectHaveHistoryCountByPage(null, roles);
        Pageable pageable = PageConverter.getPageable(page,rowsCount);
        PageConverter.convertPage(page);
        List<AccessControlInfo> accessControlInfos = accessControlDao.selectHaveHistoryByPage(page, roles);
        Page<AccessControlInfo> backPage = new Page<>();
        backPage.setContent(accessControlInfos);
        backPage.setPageable(pageable);
        return backPage;
    }

    /**
     * 越权查询所有门禁
     * @param page 分页对象
     * @return 门禁信息列表
     */
    @Override
    public List<AccessControlInfo> selectAllAccessControl(SimplePage page) {
        //分页参数暂时不用
        QueryWrapper<AccessControlInfo> wrapper = new QueryWrapper<>();
        return accessControlDao.selectList(wrapper);
    }

    /**
     * 条件查询门禁信息
     * @param accessControlInfo 门禁信息对象
     * @param page 分页对象
     * @return 门禁信息列表
     */
    @Override
    public List<AccessControlInfo> selectCondition(AccessControlInfo accessControlInfo, SimplePage page) {
        PageConverter.convertPage(page);
        if(accessControlInfo == null){
            accessControlInfo = new AccessControlInfo();
        }
        return accessControlDao.selectCondition(accessControlInfo, page, LockAuthCtrl.getRoles());
    }

    /**
     * 插入门禁信息
     * @param accessControlInfo 门禁信息对象
     * @return 返回不为-1则为成功
     */
    @Transactional(propagation= Propagation.REQUIRED,rollbackFor = {Exception.class} )
    @Override
    public int insertAccCtrl(AccessControlInfo accessControlInfo) {
        if(accessControlInfo == null){
            log.error("门禁信息为空");
            return CommonConstants.UPDATE_ERROR;
        }
        Date time = new Date();
        accessControlInfo.setAccessControlId(null);
        Integer status = accessControlInfo.getStatus();
        if(status == null){
            accessControlInfo.setStatus(2);
        }else{
            AlarmType alarmType = AlarmType.codeOf(status);
            accessControlInfo.setStatus(alarmType == null ? null : alarmType.getAlarmCode());
        }
        accessControlInfo.setCreatetime(time);
        accessControlInfo.setUpdatetime(time);
        UserInfo uerInfo = UserContextHolder.getUserInfo();
        String name = null;
        if(uerInfo != null){
            name = uerInfo.getName();
        }
        //使前台传入创建人无效
        accessControlInfo.setCreateby(name);
        LockInfo lockInfo = accessControlInfo.getLockInfo();
        String lockId = null;
        String lockCode = null;
        if(lockInfo != null){
            lockInfo.setLockId(null);
            if(lockInfo.getStatus() == null)
                lockInfo.setStatus(2);
            lockInfo.setCreateby(name);
            lockId = lockInfo.getLockId();
            lockCode = lockInfo.getLockCode();
            accessControlInfo.setLockCode(lockCode);
            lockInfo.setCreatetime(time);
            lockInfo.setUpdatetime(time);
            try {
                lockInfoService.insertLock(lockInfo);
                lockId = lockInfo.getLockId();
                accessControlInfo.setLockId(lockId);
            } catch (Exception e) {
                log.error("录入锁信息失败");
                throw new RuntimeException();
            }
        }
        CameraDevice entryCamera = accessControlInfo.getEntryCamera();
        if(entryCamera != null){
            entryCamera.setDevId(null);
            entryCamera.setCreateby(name);
            entryCamera.setType(DeviceType.ENTRY.getCode());
            entryCamera.setCreatetime(time);
            entryCamera.setUpdatetime(time);
            if(entryCamera.getStatus() == null)
                entryCamera.setStatus(0);
            entryCamera.setLockId(lockId);
            entryCamera.setLockCode(lockCode);
            accessControlInfo.setEntryCameraIp(entryCamera.getDeviceIp());
            try {
                cameraDeviceService.insertDevice(entryCamera);
                accessControlInfo.setEntryCameraId(entryCamera.getDevId());
            } catch (Exception e) {
                log.error("录入入口摄像头信息失败");
                throw new RuntimeException();
            }
        }
        CameraDevice exitCamera = accessControlInfo.getExitCamera();
        if(exitCamera != null){
            exitCamera.setDevId(null);
            exitCamera.setCreateby(name);
            exitCamera.setType(DeviceType.EXIT.getCode());
            exitCamera.setCreatetime(time);
            exitCamera.setUpdatetime(time);
            if(exitCamera.getStatus() == null)
                exitCamera.setStatus(0);
            exitCamera.setLockId(lockId);
            exitCamera.setLockCode(lockCode);
            accessControlInfo.setExitCameraIp(exitCamera.getDeviceIp());
            try {
                cameraDeviceService.insertDevice(exitCamera);
                accessControlInfo.setExitCameraId(exitCamera.getDevId());
            } catch (Exception e) {
                log.error("录入出口摄像头信息失败");
                throw new RuntimeException();
            }
        }
        return accessControlDao.insert(accessControlInfo);
    }

    /**
     * 更新门禁信息,同步更新门禁操作记录表,并且将设备的锁编号更新
     * @param accessControlInfo 门禁信息对象
     * @return 返回不为-1则为成功
     */
    @Transactional(propagation= Propagation.REQUIRED, rollbackFor = {Exception.class} )
    @Override
    public int updateAccCtrl(AccessControlInfo accessControlInfo) {
        if(accessControlInfo == null){
            log.error("门禁信息为空");
            return CommonConstants.UPDATE_ERROR;
        }
        String oldControlName = null;
        AccessControlInfo controlInfo = accessControlDao.selectById(accessControlInfo.getAccessControlId());
        if(controlInfo != null){
            oldControlName = controlInfo.getAccessControlName();
        }
        String oldEntryCameraIp = null;
        String oldExitCameraIp = null;
        String oldLockCode = null;

        Date time = new Date();
        accessControlInfo.setUpdatetime(time);
        LockInfo lockInfo = accessControlInfo.getLockInfo();
        String lockId = null;
        String lockCode = null;
        if(lockInfo != null){
            lockId = lockInfo.getLockId();
            accessControlInfo.setLockId(lockId);
            lockCode = lockInfo.getLockCode();
            accessControlInfo.setLockCode(lockCode);
            LockInfo oldLock = lockInfoDao.selectById(lockId);
            if(oldLock != null)
                oldLockCode = oldLock.getLockCode();
            try {
                lockInfo.setUpdatetime(time);
                lockInfoService.updateLock(lockInfo);
            } catch (Exception e) {
                log.error("更新锁信息失败");
                throw new RuntimeException();
            }
        }
        CameraDevice entryCamera = accessControlInfo.getEntryCamera();
        if(entryCamera != null){
            entryCamera.setLockId(lockId);
            entryCamera.setLockCode(lockCode);
            entryCamera.setType(DeviceType.ENTRY.getCode());
            entryCamera.setUpdatetime(time);
            accessControlInfo.setEntryCameraId(entryCamera.getDevId());
            accessControlInfo.setEntryCameraIp(entryCamera.getDeviceIp());
            CameraDevice oldEntryCameraDevice = cameraDeviceDao.selectById(accessControlInfo.getEntryCameraId());
            if(oldEntryCameraDevice != null)
                oldEntryCameraIp = oldEntryCameraDevice.getDeviceIp();

            try {
                cameraDeviceService.updateDevice(entryCamera);
            } catch (Exception e) {
                log.error("更新入口摄像头信息失败");
                throw new RuntimeException();
            }
        }
        CameraDevice exitCamera = accessControlInfo.getExitCamera();
        if(exitCamera != null){
            exitCamera.setLockId(lockId);
            exitCamera.setLockCode(lockCode);
            exitCamera.setType(DeviceType.EXIT.getCode());
            exitCamera.setUpdatetime(time);
            accessControlInfo.setExitCameraId(exitCamera.getDevId());
            accessControlInfo.setExitCameraIp(exitCamera.getDeviceIp());
            CameraDevice oldExitcameraDevice = cameraDeviceDao.selectById(accessControlInfo.getExitCameraId());
            if(oldExitcameraDevice != null)
                oldExitCameraIp = oldExitcameraDevice.getDeviceIp();

            try {
                cameraDeviceService.updateDevice(exitCamera);
            } catch (Exception e) {
                log.error("更新出口摄像头信息失败");
                throw new RuntimeException();
            }
        }
        UpdateWrapper<AccessControlInfo> updateWrapper = new UpdateWrapper<>();
        int result = 0;
        try {
            result = accessControlDao.update(accessControlInfo, updateWrapper.eq("access_control_id", accessControlInfo.getAccessControlId()));
        } catch (Exception e) {
            log.error("更新门禁{}失败", accessControlInfo.getAccessControlId());
            throw new RuntimeException();
        }

        try {
            //更新门禁信息成功后需要同步更新门禁操作记录表(access_control_name,device_ip,lock_code)，并且将设备的锁编号更新
            UpdateWrapper<AccCtrlProcess> wrapper = new UpdateWrapper<>();
            //accessControlName
            String newControlName = accessControlInfo.getAccessControlName();
//            AccCtrlProcess oldAccCtrlProcess = new AccCtrlProcess();
            AccCtrlProcess newNameAccCtrlProcess = new AccCtrlProcess();
            newNameAccCtrlProcess.setAccessControlName(newControlName);
            accCtrlProcessDao.update(newNameAccCtrlProcess,wrapper.eq("access_control_name",oldControlName));
            //dviceIp,先更新ip为入口ip的记录
            String newEntryCameraIp = accessControlInfo.getEntryCameraIp();
            AccCtrlProcess newEntryCameraAccCtrlProcess = new AccCtrlProcess();
            newEntryCameraAccCtrlProcess.setDeviceIp(newEntryCameraIp);
            accCtrlProcessDao.update(newEntryCameraAccCtrlProcess,new UpdateWrapper<AccCtrlProcess>().eq("device_ip",oldEntryCameraIp));
            AccCtrlProcess newExitAccCtrlProcess = new AccCtrlProcess();
            newExitAccCtrlProcess.setDeviceIp(accessControlInfo.getExitCameraIp());
            accCtrlProcessDao.update(newExitAccCtrlProcess,new UpdateWrapper<AccCtrlProcess>().eq("device_ip",oldExitCameraIp));

            //lockCode
            AccCtrlProcess newLockAccCtrlProcess = new AccCtrlProcess();
            newLockAccCtrlProcess.setLockCode(accessControlInfo.getLockCode());
            accCtrlProcessDao.update(newLockAccCtrlProcess,new UpdateWrapper<AccCtrlProcess>().eq("lock_code",oldLockCode));

            CameraDevice updateDevice = new CameraDevice();
            updateDevice.setDevId(accessControlInfo.getEntryCameraId());
            updateDevice.setLockCode(accessControlInfo.getLockCode());
            //更新摄像头关联的lockCode
            cameraDeviceService.updateDevice(updateDevice);
            updateDevice.setDevId(accessControlInfo.getExitCameraId());
            cameraDeviceService.updateDevice(updateDevice);
        } catch (Exception e) {
            log.error("更新门禁操作记录失败");
            throw new RuntimeException();
        }
        return result;
    }

    /**
     * 根据id删除门禁信息
     * @param accessControlId 门禁id
     * @return 返回不为-1则为成功
     */
    @Override
    public int delAccCtrlByAccCtrlId(String accessControlId) {
        if(accessControlId == null){
            log.error("门禁id为空");
            return CommonConstants.UPDATE_ERROR;
        }
        UpdateWrapper<AccessControlInfo> wrapper = new UpdateWrapper<>();
        return accessControlDao.delete(wrapper.eq("access_control_id",accessControlId));
    }

    /**
     * 根据id List批量删除门禁信息
     * @param accessControlIds 门禁id列表
     * @return 返回不为-1则为成功
     */
    @Override
    public int delBatchAccCtrlByAccCtrlId(List<String> accessControlIds) {
        if(accessControlIds == null || accessControlIds.isEmpty()){
            log.error("门禁id列表为空");
            return CommonConstants.UPDATE_ERROR;
        }
        List<String> lockIds = new ArrayList<>();
        List<String> cameraIds = new ArrayList<>();
        List<String> authIds = new ArrayList<>();
        List<String> accCtrlProIds = new ArrayList<>();
        List<String> alarmIds = new ArrayList<>();
        List<AccCtrlProcess> accCtrlProcesses = accCtrlProcessDao.selectRecodByAccessControlIds(accessControlIds);
        if(accCtrlProcesses != null){
            for(AccCtrlProcess accCtrlPro : accCtrlProcesses) {
                if(accCtrlPro == null)
                    continue;
                String accCtrlProId = accCtrlPro.getAccCtrlProId();
                if(accCtrlProId == null)
                    continue;
                accCtrlProIds.add(accCtrlProId);
            }
        }
        if(!accCtrlProIds.isEmpty()){
            List<Alarm> alarms = alarmDao.selectAlarmsByAccCtrlProIds(accCtrlProIds);
            if(alarms != null){
                for(Alarm alarm : alarms) {
                    if(alarm == null)
                        continue;
                    String alarmId = alarm.getAlarmId();
                    if(alarmId == null)
                        continue;
                    alarmIds.add(alarmId);
                }
            }
        }

        for(String acId : accessControlIds) {
            AccessControlInfo accessControlInfo = selectAccCtrlByIdBeyondAuthority(acId);
            if(accessControlInfo == null)
                continue;
            lockIds.add(accessControlInfo.getLockId());
            cameraIds.add(accessControlInfo.getEntryCameraId());
            cameraIds.add(accessControlInfo.getExitCameraId());
            List<AccCtrlRole> accessControls = accCtrlRoleDao.selectList(new QueryWrapper<AccCtrlRole>().eq("access_control_id", acId));
            if(accessControls != null){
                for(AccCtrlRole ar : accessControls) {
                    authIds.add(ar.getAccessControlId());
                }
            }
        }
        CommonUtil.removeDuplicate(authIds);
        try {
            lockInfoDao.deleteBatchIds(lockIds);
        } catch (Exception e) {
            log.error("批量删除锁信息失败");
        }
        try {
            cameraDeviceDao.deleteBatchIds(cameraIds);
        } catch (Exception e) {
            log.error("批量删除摄像头信息失败");
        }
        int result = -1;
        try {
            result = accessControlDao.deleteBatchIds(accessControlIds);
        } catch (Exception e) {
            log.error("删除门禁信息失败");
        }
        //删除门禁授权信息
        try {
            accCtrlRoleDao.deleteBatchIds(authIds);
        } catch (Exception e) {
            log.error("删除门禁授权信息失败");
        }
        //删除相应的门禁操作记录
        try {
            accCtrlProcessDao.deleteBatchIds(accCtrlProIds);
        } catch (Exception e) {
            log.error("删除相应的门禁操作记录失败");
        }
        //删除相应的门禁告警
        try {
            alarmDao.deleteBatchIds(alarmIds);
        } catch (Exception e) {
            log.error("删除相应的门禁告警失败");
        }
        return result;
    }

    /**
     * 根据锁编号删除门禁信息
     * @param lockCode 锁编号
     * @return 返回不为-1则为成功
     */
    @Override
    public int delAccCtrlByLockCode(String lockCode) {
        if(lockCode == null){
            log.error("锁编号为空");
            return CommonConstants.UPDATE_ERROR;
        }
        UpdateWrapper<AccessControlInfo> wrapper = new UpdateWrapper<>();
        return accessControlDao.delete(wrapper.eq("lock_code",lockCode));
    }
}
