package com.summit.controller;

import com.summit.cbb.utils.page.Page;
import com.summit.common.entity.ResponseCodeEnum;
import com.summit.common.entity.RestfulEntityBySummit;
import com.summit.common.entity.UserInfo;
import com.summit.common.util.ResultBuilder;
import com.summit.common.web.filter.UserContextHolder;
import com.summit.dao.entity.AccessControlInfo;
import com.summit.dao.entity.CameraDevice;
import com.summit.dao.entity.LockInfo;
import com.summit.dao.entity.SimplePage;
import com.summit.service.AccessControlService;
import com.summit.service.CameraDeviceService;
import com.summit.service.LockInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Slf4j
@Api(tags = "门禁信息接口")
@RestController
@RequestMapping("/accessControlInfo")
public class AccessControlInfoController {

    @Autowired
    private AccessControlService accessControlService;
    @Autowired
    private LockInfoService lockInfoService;
    @Autowired
    private CameraDeviceService cameraDeviceService;

    @ApiOperation(value = "分页查询全部门禁信息", notes = "分页参数为空则查全部，current和pageSize有一个为null则查询不到结果，current<=0则置为1，pageSize<=0则查不到结果")
    @GetMapping(value = "/selectAccCtrlByPage")
    public RestfulEntityBySummit<Page<AccessControlInfo>> selectAccCtrlByPage(@ApiParam(value = "当前页，大于等于1")  @RequestParam(value = "current",required = false) Integer current,
                                                                                    @ApiParam(value = "每页条数，大于等于0")  @RequestParam(value = "pageSize",required = false) Integer pageSize){
        Page<AccessControlInfo> controlInfoPage = null;
        try {
            controlInfoPage = accessControlService.selectAccCtrlByPage(new SimplePage(current, pageSize));
            filterAccCtrlInfo(controlInfoPage);
        } catch (Exception e) {
            log.error("分页查询全部门禁信息失败");
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999,"分页查询全部门禁信息失败", controlInfoPage);
        }
        return ResultBuilder.buildError(ResponseCodeEnum.CODE_0000,"分页查询全部门禁信息成功", controlInfoPage);
    }


    @ApiOperation(value = "录入门禁信息", notes = "录入门禁信息时同时录入锁信息和设备信息")
    @PostMapping(value = "/insertAccessControl")
    public RestfulEntityBySummit<String> insertAccessControl(@ApiParam(value = "包含锁信息和摄像头信息的门禁信息")  @RequestBody AccessControlInfo accessControlInfo){
        if(accessControlInfo == null){
            log.error("门禁信息为空");
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9993,"门禁信息为空",null);
        }
        Date time = new Date();
        accessControlInfo.setAccessControlId(null);
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
                log.error("插入锁信息失败");
            }
        }
        CameraDevice entryCamera = accessControlInfo.getEntryCamera();
        if(entryCamera != null){
            entryCamera.setDevId(null);
            entryCamera.setLockId(lockId);
            entryCamera.setLockCode(lockCode);
            accessControlInfo.setEntryCameraIp(entryCamera.getDeviceIp());
            try {
                cameraDeviceService.insertDevice(entryCamera);
                accessControlInfo.setEntryCameraId(entryCamera.getDevId());
            } catch (Exception e) {
                log.error("插入入口摄像头信息失败");
            }
        }
        CameraDevice exitCamera = accessControlInfo.getExitCamera();
        if(exitCamera != null){
            exitCamera.setDevId(null);
            exitCamera.setLockId(lockId);
            exitCamera.setLockCode(lockCode);
            accessControlInfo.setExitCameraIp(exitCamera.getDeviceIp());
            try {
                cameraDeviceService.insertDevice(exitCamera);
                accessControlInfo.setExitCameraId(exitCamera.getDevId());
            } catch (Exception e) {
                log.error("插入出口摄像头信息失败");
            }
        }
        try {
            accessControlService.insertAccCtrl(accessControlInfo);
        } catch (Exception e) {
            log.error("录入门禁信息失败");
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999,"录入门禁信息失败", null);
        }
        return ResultBuilder.buildError(ResponseCodeEnum.CODE_0000,"录入门禁信息成功", null);
    }

    @ApiOperation(value = "更新门禁信息", notes = "更新门禁信息时同时更新锁信息和设备信息")
    @PutMapping(value = "/updateAccessControl")
    public RestfulEntityBySummit<String> updateAccessControl(@ApiParam(value = "包含锁信息和摄像头信息的门禁信息")  @RequestBody AccessControlInfo accessControlInfo){
        if(accessControlInfo == null){
            log.error("门禁信息为空");
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9993,"门禁信息为空",null);
        }

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
            try {
                lockInfo.setUpdatetime(time);
                lockInfoService.updateLock(lockInfo);
            } catch (Exception e) {
                log.error("更新锁信息失败");
            }
        }
        CameraDevice entryCamera = accessControlInfo.getEntryCamera();
        if(entryCamera != null){
            entryCamera.setLockId(lockId);
            entryCamera.setLockCode(lockCode);
            accessControlInfo.setEntryCameraId(entryCamera.getDevId());
            accessControlInfo.setEntryCameraIp(entryCamera.getDeviceIp());
            try {
                cameraDeviceService.updateDevice(entryCamera);
            } catch (Exception e) {
                log.error("更新入口摄像头信息失败");
            }
        }
        CameraDevice exitCamera = accessControlInfo.getExitCamera();
        if(exitCamera != null){
            exitCamera.setLockId(lockId);
            exitCamera.setLockCode(lockCode);
            accessControlInfo.setExitCameraId(exitCamera.getDevId());
            accessControlInfo.setExitCameraIp(exitCamera.getDeviceIp());
            try {
                cameraDeviceService.updateDevice(exitCamera);
            } catch (Exception e) {
                log.error("更新出口摄像头信息失败");
            }
        }
        try {
            accessControlService.updateAccCtrl(accessControlInfo);
        } catch (Exception e) {
            log.error("更新门禁信息失败");
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999,"更新门禁信息失败", null);
        }
        return ResultBuilder.buildError(ResponseCodeEnum.CODE_0000,"更新门禁信息成功", null);
    }


    @ApiOperation(value = "删除门禁信息", notes = "根据门禁id删除门禁信息，时同删除锁关联的锁信息和设备信息")
    @DeleteMapping(value = "/delAccessControl")
    public RestfulEntityBySummit<String> delAccessControl(@ApiParam(value = "门禁id",required = true)  @RequestParam(value = "accessControlId",required = false) String accessControlId){
        if(accessControlId == null){
            log.error("门禁id为空");
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9993,"门禁id为空",null);
        }
        AccessControlInfo accessControlInfo = accessControlService.selectAccCtrlByIdBeyondAuthority(accessControlId);
        if(accessControlInfo != null){
            String lockId = accessControlInfo.getLockId();
            try {
                if(lockId != null){
                    lockInfoService.delLockByLockId(lockId);
                }else{
                    String lockCode = accessControlInfo.getLockCode();
                    lockInfoService.delLockByLockCode(lockCode);
                }
            } catch (Exception e) {
                log.error("删除锁信息失败");
            }
            String entryCameraId = accessControlInfo.getEntryCameraId();
            try {
                if(entryCameraId != null){
                    cameraDeviceService.delDeviceByDevId(entryCameraId);
                }else{
                    String entryCameraIp = accessControlInfo.getEntryCameraIp();
                    cameraDeviceService.delDeviceByIpAddress(entryCameraIp);
                }
            } catch (Exception e) {
                log.error("删除入口摄像头信息失败");
            }
            String exitCameraId = accessControlInfo.getExitCameraId();
            try {
                if(exitCameraId != null){
                    cameraDeviceService.delDeviceByDevId(exitCameraId);
                }else{
                    String exitCameraIp = accessControlInfo.getExitCameraIp();
                    cameraDeviceService.delDeviceByIpAddress(exitCameraIp);
                }
            } catch (Exception e) {
                log.error("删除出口摄像头信息失败");
            }
        }
        try {
            accessControlService.delAccCtrlByAccCtrlId(accessControlId);
        } catch (Exception e) {
            log.error("删除门禁信息失败");
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999,"删除门禁信息失败", null);
        }
        return ResultBuilder.buildError(ResponseCodeEnum.CODE_0000,"删除门禁信息成功", null);
    }


    private void filterAccCtrlInfo(Page<AccessControlInfo> controlInfoPage) {
        if (controlInfoPage != null) {
            List<AccessControlInfo> content = controlInfoPage.getContent();
            if (content != null && !content.isEmpty()){
                for(AccessControlInfo acInfos : content) {
                    acInfos.setRoles(null);
                }
            }
        }
    }

}
