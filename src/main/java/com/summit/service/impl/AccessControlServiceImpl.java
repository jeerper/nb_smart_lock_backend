package com.summit.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.summit.cbb.utils.page.Page;
import com.summit.cbb.utils.page.Pageable;
import com.summit.common.entity.UserInfo;
import com.summit.common.util.UserAuthUtils;
import com.summit.common.web.filter.UserContextHolder;
import com.summit.constants.CommonConstants;
import com.summit.dao.entity.*;
import com.summit.dao.repository.*;
import com.summit.exception.ErrorMsgException;
import com.summit.sdk.huawei.model.AlarmType;
import com.summit.service.AccessControlService;
import com.summit.service.CameraDeviceService;
import com.summit.service.LockInfoService;
import com.summit.util.CommonUtil;
import com.summit.util.ExcelUtil;
import com.summit.util.PageConverter;
import com.summit.util.UserDeptAuthUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
    @Autowired
    private AccCtrlRealTimeDao accCtrlRealTimeDao;
    @Autowired
    private ExcelUtil excelUtil;

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
        return accessControlDao.selectAccCtrlById(accessControlId, UserAuthUtils.getRoles());
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
        return accessControlDao.selectAccCtrlByLockCode(lockCode, null);
    }

    /**
     * 分页查询全部门禁信息
     * @return 门禁信息列表
     */
    @Override
    public Page<AccessControlInfo> selectAccCtrlByPage(AccessControlInfo accessControlInfo,Integer current, Integer pageSize) {
        if(accessControlInfo == null){
            accessControlInfo = new AccessControlInfo();
        }
        Page<AccessControlInfo> pageParam = null;

        if (current != null && pageSize != null) {
            pageParam = new Page<>(current, pageSize);
        }

        List<AccessControlInfo> accessControls = accessControlDao.selectCondition(pageParam,accessControlInfo, UserDeptAuthUtils.getDepts());

        if (pageParam != null) {
            pageParam.setRecords(accessControls);
            return pageParam;
        }
        return new Page<>(accessControls, null);
    }

    /**
     * 分页查询全部有操作记录的门禁信息
     * @param page 分页对象
     * @return 门禁信息列表
     */
    @Override
    public Page<AccessControlInfo> selectHaveHistoryByPage(SimplePage page) {
        //转换当前页之前设置当前页
        List<String> roles = UserAuthUtils.getRoles();
        Integer rowsCount = accessControlDao.selectHaveHistoryCountByPage(null, roles);
        Pageable pageable = PageConverter.getPageable(page,rowsCount);
        PageConverter.convertPage(page);
        List<AccessControlInfo> accessControlInfos = accessControlDao.selectHaveHistoryByPage(page, roles);
        Page<AccessControlInfo> backPage = new Page<>(accessControlInfos,pageable);
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
     * @return 门禁信息列表
     */
    @Override
    public List<AccessControlInfo> selectCondition(AccessControlInfo accessControlInfo, Integer current, Integer pageSize) {
        if(accessControlInfo == null){
            accessControlInfo = new AccessControlInfo();
        }
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<AccCtrlRealTimeEntity> pageParam = null;

        if (current != null && pageSize != null) {
            pageParam = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(current, pageSize);
        }
        return accessControlDao.selectCondition(pageParam,accessControlInfo,  UserAuthUtils.getRoles());
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
            LockInfo lock = lockInfoService.selectBylockCode(lockCode);
            if(lock != null){
                log.error("录入锁信息失败，锁{}已存在且已属于其他门禁", lock.getLockCode());
                throw new ErrorMsgException("录入锁信息失败，锁" + lock.getLockCode() + "已存在且已属于其他门禁");
            }
            accessControlInfo.setLockCode(lockCode);
            lockInfo.setCreatetime(time);
            lockInfo.setUpdatetime(time);
            try {
                lockInfoService.insertLock(lockInfo);
                lockId = lockInfo.getLockId();
                accessControlInfo.setLockId(lockId);
            } catch (Exception e) {
                log.error("录入锁信息失败，锁{}已存在且已属于其他门禁", lockCode);
                throw new ErrorMsgException("录入锁信息失败，锁" + lockCode + "已存在且已属于其他门禁");
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
        String accessControlId = accessControlInfo.getAccessControlId();
        if(accessControlId == null){
            log.error("门禁id为空");
            return CommonConstants.UPDATE_ERROR;
        }
        AccessControlInfo controlInfo = accessControlDao.selectById(accessControlId);
        if(controlInfo == null){
            log.error("未找到对应门禁");
            return CommonConstants.UPDATE_ERROR;
        }
        String oldEntryCameraIp = controlInfo.getEntryCameraIp();;
        String oldExitCameraIp = controlInfo.getExitCameraIp();
        String oldLockCode = controlInfo.getLockCode();

        Date time = new Date();
        accessControlInfo.setUpdatetime(time);
        LockInfo lockInfo = accessControlInfo.getLockInfo();
        String lockId = null;
        String lockCode = null;
        if(lockInfo != null){
            lockId = lockInfo.getLockId();
            accessControlInfo.setLockId(lockId);
            lockCode = lockInfo.getLockCode();
//            AccessControlInfo acInfo = accessControlService.selectAccCtrlByLockCode(lockCode);
//            if(acInfo != null){
//                if(acInfo.getAccessControlId() != null && !acInfo.getAccessControlId().equals(accessControlId)){
//                    log.error("更新锁信息失败，锁{}已存在且已属于其他门禁", lockCode);
//                    throw new ErrorMsgException("更新锁信息失败，锁" + lockCode + "已存在且已属于其他门禁");
//                }
//            }
            accessControlInfo.setLockCode(lockCode);
            if(lockCode != null){
                if(!lockCode.equals(oldLockCode)){
                    LockInfo lock = lockInfoService.selectBylockCode(lockCode);
                    if(lock != null){
                        log.error("更新锁信息失败，锁{}已存在且已属于其他门禁", lock.getLockCode());
                        throw new ErrorMsgException("更新锁信息失败，锁" + lock.getLockCode() + "已存在且已属于其他门禁");
                    }
                    try {
                        lockInfo.setUpdatetime(time);
                        lockInfoService.updateLock(lockInfo);
                    } catch (Exception e) {
                        log.error("更新锁信息失败，锁{}已存在且已属于其他门禁", lockCode);
                        throw new ErrorMsgException("更新锁信息失败，锁" + lockCode + "已存在且已属于其他门禁");
                    }
                }
            }
        }
        UpdateWrapper<AccessControlInfo> updateWrapper = new UpdateWrapper<>();
        int result = 0;
        try {
            accCtrlRealTimeDao.update(null,Wrappers.<AccCtrlRealTimeEntity>lambdaUpdate()
                    .set(AccCtrlRealTimeEntity::getLockId,  accessControlInfo.getLockId())
                    .set(AccCtrlRealTimeEntity::getLockCode,  accessControlInfo.getLockCode())
                    .eq(AccCtrlRealTimeEntity::getAccessControlId, accessControlId)
            );
            result = accessControlDao.update(accessControlInfo, updateWrapper.eq("access_control_id", accessControlId));
        } catch (Exception e) {
            log.error("更新门禁{}失败", accessControlId);
            throw new ErrorMsgException("更新门禁" + accessControlId +"失败");
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
    @Transactional(propagation= Propagation.REQUIRED, rollbackFor = {Exception.class} )
    @Override
    public int delBatchAccCtrlByAccCtrlId(List<String> accessControlIds) {
        if(CommonUtil.isEmptyList(accessControlIds)){
            log.error("门禁id列表为空");
            return CommonConstants.UPDATE_ERROR;
        }
        List<String> lockIds = new ArrayList<>();
        List<String> cameraIds = new ArrayList<>();
        List<String> authIds = new ArrayList<>();
        List<String> accCtrlProIds = new ArrayList<>();
        List<String> alarmIds = new ArrayList<>();
        List<String> accCrtlRealTimeIds = new ArrayList<>();
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
            if(CommonUtil.isEmptyStr(acId))
                continue;
            AccessControlInfo accessControlInfo = selectAccCtrlByIdBeyondAuthority(acId);
            if(accessControlInfo != null){
                lockIds.add(accessControlInfo.getLockId());
                cameraIds.add(accessControlInfo.getEntryCameraId());
                cameraIds.add(accessControlInfo.getExitCameraId());
            }

            AccCtrlRealTimeEntity accCtrlRealTimeEntity = accCtrlRealTimeDao.selectRealTimeInfoByAccCtrlId(acId, null);
            String accCrtlRealTimeId;
            if(accCtrlRealTimeEntity != null &&
                    !CommonUtil.isEmptyStr((accCrtlRealTimeId = accCtrlRealTimeEntity.getAccCrtlRealTimeId())))
                accCrtlRealTimeIds.add(accCrtlRealTimeId);
            List<AccCtrlRole> accessControls = accCtrlRoleDao.selectList(new QueryWrapper<AccCtrlRole>().eq("access_control_id", acId));
            if(accessControls != null){
                for(AccCtrlRole ar : accessControls) {
                    if(ar == null)
                        continue;
                    String authId = ar.getId();
                    if(authId != null)
                        authIds.add(authId);
                }
            }
        }
        CommonUtil.removeDuplicate(authIds);
        if(!lockIds.isEmpty()){
            try {
                lockInfoDao.deleteBatchIds(lockIds);
            } catch (Exception e) {
                log.error("批量删除锁信息失败");
                throw new ErrorMsgException("批量删除锁信息失败");
            }
        }
        if(!cameraIds.isEmpty()){
            try {
                cameraDeviceDao.deleteBatchIds(cameraIds);
            } catch (Exception e) {
                log.error("批量删除摄像头信息失败");
                throw new ErrorMsgException("批量删除摄像头信息失败");
            }
        }

        int result = -1;
        if(!accessControlIds.isEmpty()){
            try {
                result = accessControlDao.deleteBatchIds(accessControlIds);
            } catch (Exception e) {
                log.error("删除门禁信息失败");
                throw new ErrorMsgException("删除门禁信息失败");
            }
        }

        //删除门禁授权信息
        if(!authIds.isEmpty()){
            try {
                accCtrlRoleDao.deleteBatchIds(authIds);
            } catch (Exception e) {
                log.error("删除门禁授权信息失败");
                throw new ErrorMsgException("删除门禁授权信息失败");
            }
        }

        //删除相应的门禁操作记录
        if(!accCtrlProIds.isEmpty()){
            try {
                accCtrlProcessDao.deleteBatchIds(accCtrlProIds);
            } catch (Exception e) {
                log.error("删除相应的门禁操作记录失败");
                throw new ErrorMsgException("删除相应的门禁操作记录失败");
            }
        }

        //删除相应的门禁告警
        if(!alarmIds.isEmpty()){
            try {
                alarmDao.deleteBatchIds(alarmIds);
            } catch (Exception e) {
                log.error("删除相应的门禁告警失败");
                throw new ErrorMsgException("删除相应的门禁告警失败");
            }
        }
        //删除相应的门禁实时信息
        if(!accCrtlRealTimeIds.isEmpty()){
            try {
                accCtrlRealTimeDao.deleteBatchIds(accCrtlRealTimeIds);
            } catch (Exception e) {
                log.error("删除相应的门禁实时信息失败");
                throw new ErrorMsgException("删除相应的门禁实时信息失败");
            }
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

    @Override
    public List<AccessControlInfo> selectAccCtrlInfosByUserName(String userName) {
       /* StringBuffer sql=new StringBuffer("SELECT acc.access_control_id,acc.access_control_name,acc.createby,acc.createtime,acc.entry_camera_id,acc.entry_camera_ip,acc.exit_camera_id,acc.exit_camera_ip,acc.latitude,acc.lock_code,acc.lock_id,acc.longitude,acc.`status`,acc.updatetime ");
        sql.append("from access_control acc LEFT JOIN dept_accesscontrol_auth deptAcc on acc.access_control_id=deptAcc.access_control_id  ");
        sql.append("LEFT JOIN "+cbbuserdataBase+".sys_user_dept userDept on userDept.DEPTID=deptAcc.dept_id ");
        sql.append("LEFT JOIN "+cbbuserdataBase+".sys_user user on user.USERNAME=userDept.USERNAME WHERE USER.USERNAME= ?");
        LinkedMap lm=new LinkedMap();
        linkedMap.put(1, adcd);
        List dataList = ur.queryAllCustom(sql, linkedMap);*/
        if(userName == null){
            log.error("锁编号为空");
            return null;
        }
        List<AccessControlInfo> accessControlInfos=  accessControlDao.selectAccCtrlInfosByUserName(userName);
        return accessControlInfos;
    }

    @Override
    @Transactional(propagation= Propagation.REQUIRED, rollbackFor = {Exception.class} )
    public boolean batchImport(MultipartFile uploadFile) throws Exception {
        Map<String, Object> stringObjectMap = excelUtil.loadExcel(uploadFile);
        List<AccessControlInfo> accessControlInfos = (List<AccessControlInfo>)stringObjectMap.get("accessControlInfos");
        List<LockInfo> lockInfos=(List<LockInfo>)stringObjectMap.get("lockInfos");
        try{
            accessControlDao.insertAccessControlInfos(accessControlInfos);
            lockInfoDao.insertLockInfos(lockInfos);
        }catch (Exception e){
            log.error("批量插入数据失败");
            throw new ErrorMsgException("批量插入数据失败");
        }
        return true;
    }

    @Override
    public List<AccessControlInfo> loginAccessControlInfoExport(AccessControlInfo accessControlInfo) {
        if (accessControlInfo ==null){
            accessControlInfo=new AccessControlInfo();
        }
        List<AccessControlInfo>  accessControlInfos=  accessControlDao.loginAccessControlInfoExport(accessControlInfo);
        return accessControlInfos;
    }
}
