package com.summit.controller;

import com.summit.cbb.utils.page.Page;
import com.summit.common.entity.ResponseCodeEnum;
import com.summit.common.entity.RestfulEntityBySummit;
import com.summit.common.util.ResultBuilder;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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
        } catch (Exception e) {
            log.error("分页查询全部门禁信息失败");
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999,"分页查询全部门禁信息失败", controlInfoPage);
        }
        return ResultBuilder.buildError(ResponseCodeEnum.CODE_0000,"分页查询全部门禁信息成功", controlInfoPage);
    }


    @ApiOperation(value = "录入门禁信息", notes = "录入门禁信息时同时录入锁信息和设备信息")
    @PostMapping(value = "/insertAccessControl")
    public RestfulEntityBySummit<String> insertAccessControl(@RequestBody AccessControlInfo accessControlInfo){
        if(accessControlInfo == null){
            log.error("门禁信息为空");
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9993,"门禁信息为空",null);
        }
        accessControlInfo.setCreatetime(new Date());
        accessControlInfo.setUpdatetime(new Date());
        LockInfo lockInfo = accessControlInfo.getLockInfo();
        String lockId = null;
        String lockCode = null;
        if(lockInfo != null){
            lockId = lockInfo.getLockId();
            accessControlInfo.setLockId(lockId);
            lockCode = lockInfo.getLockCode();
            accessControlInfo.setLockCode(lockCode);
            try {
                lockInfo.setCreatetime(new Date());
                lockInfo.setUpdatetime(new Date());
                lockInfoService.insertLock(lockInfo);
            } catch (Exception e) {
                log.error("插入锁信息失败");
            }
        }
        CameraDevice entryCamera = accessControlInfo.getEntryCamera();
        if(entryCamera != null){
            entryCamera.setLockId(lockId);
            entryCamera.setLockCode(lockCode);
            accessControlInfo.setEntryCameraId(entryCamera.getDevId());
            accessControlInfo.setEntryCameraIp(entryCamera.getDeviceIp());
            try {
                cameraDeviceService.insertDevice(entryCamera);
            } catch (Exception e) {
                log.error("插入入口摄像头信息失败");
            }
        }
        CameraDevice exitCamera = accessControlInfo.getExitCamera();
        if(exitCamera != null){
            exitCamera.setLockId(lockId);
            exitCamera.setLockCode(lockCode);
            accessControlInfo.setExitCameraId(exitCamera.getDevId());
            accessControlInfo.setExitCameraIp(exitCamera.getDeviceIp());
            try {
                cameraDeviceService.insertDevice(exitCamera);
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


}
