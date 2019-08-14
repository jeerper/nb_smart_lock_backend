package com.summit.controller;

import com.summit.cbb.utils.page.Page;
import com.summit.cbb.utils.page.Pageable;
import com.summit.common.entity.ResponseCodeEnum;
import com.summit.common.entity.RestfulEntityBySummit;
import com.summit.common.util.ResultBuilder;
import com.summit.constants.CommonConstants;
import com.summit.dao.entity.LockInfo;
import com.summit.dao.entity.SimplePage;
import com.summit.service.FaceInfoService;
import com.summit.service.LockInfoService;
import com.summit.service.LockRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@Api(tags = "锁信息接口")
@RestController
@RequestMapping("/lockInfo")
public class LockInfoController {

    @Autowired
    private LockRecordService lockRecordService;
    @Autowired
    private LockInfoService lockInfoService;
    @Autowired
    private FaceInfoService faceInfoService;


    @ApiOperation(value = "分页查询全部锁信息", notes = "分页参数为空则查全部，current和pageSize有一个为null则查询不到结果，current<=0则置为1，pageSize<=0则查不到结果")
    @GetMapping(value = "/selectLockInfoByPage")
    public RestfulEntityBySummit<Page<LockInfo>> selectLockInfoByPage(@ApiParam(value = "当前页，大于等于1")  @RequestParam(value = "current", required = false) Integer current,
                                                                   @ApiParam(value = "每页条数，大于等于0")  @RequestParam(value = "pageSize", required = false) Integer pageSize) {

        Page<LockInfo> lockInfos = null;
        try {
            lockInfos = lockInfoService.selectLockInfoByPage(new SimplePage(current, pageSize));
            filterInfo(lockInfos);
        } catch (Exception e) {
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999,"查询全部锁信息失败", lockInfos);
        }
        return ResultBuilder.buildError(ResponseCodeEnum.CODE_0000,"查询全部锁信息成功", lockInfos);
    }


    @ApiOperation(value = "根据id查询锁信息", notes = "查询唯一一条锁信息")
    @GetMapping(value = "/selectLockById")
    public RestfulEntityBySummit<LockInfo> selectLockById(@ApiParam(value = "锁id", required = true)  @RequestParam(value = "lockId") String lockId) {
        if(lockId == null){
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9993,"锁id为空", null);
        }
        LockInfo lockInfo = null;
        try {
            lockInfo = lockInfoService.selectLockById(lockId);
            filterInfo(lockInfo);
        } catch (Exception e) {
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999,"查询锁信息失败", lockInfo);
        }
        return ResultBuilder.buildError(ResponseCodeEnum.CODE_0000,"查询锁信息成功", lockInfo);
    }


    @ApiOperation(value = "根据锁编号查询唯一锁信息", notes = "查询唯一一条锁信息")
    @GetMapping(value = "/selectBylockCode")
    public RestfulEntityBySummit<LockInfo> selectBylockCode(@ApiParam(value = "锁编号", required = true)  @RequestParam(value = "lockCode") String lockCode) {
        if(lockCode == null){
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9993,"锁编号为空", null);
        }
        LockInfo lockInfo = null;
        try {
            lockInfo = lockInfoService.selectBylockCode(lockCode);
            filterInfo(lockInfo);
        } catch (Exception e) {
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999,"查询锁信息失败", lockInfo);
        }
        return ResultBuilder.buildError(ResponseCodeEnum.CODE_0000,"查询锁信息成功", lockInfo);
    }


    @ApiOperation(value = "分页查询全部有操作记录的锁信息", notes = "分页查询全部有操作记录的锁信息")
    @GetMapping(value = "/selectHaveHistoryByPage")
    public RestfulEntityBySummit<Page<LockInfo>> selectHaveHistoryByPage(@ApiParam(value = "当前页，大于等于1")  @RequestParam(value = "current", required = false) Integer current,
                                                                      @ApiParam(value = "每页条数，大于等于0")  @RequestParam(value = "pageSize", required = false) Integer pageSize) {

        Page<LockInfo> lockInfos = null;
        try {
            lockInfos = lockInfoService.selectHaveHistoryByPage(new SimplePage(current, pageSize));
            filterInfo(lockInfos);
        } catch (Exception e) {
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999,"分页查询全部有操作记录的锁信息失败", lockInfos);
        }
        return ResultBuilder.buildError(ResponseCodeEnum.CODE_0000,"分页查询全部有操作记录的锁信息成功", lockInfos);
    }


    @ApiOperation(value = "条件查询锁信息", notes = "条件查询锁信息")
    @GetMapping(value = "/selectCondition")
    public RestfulEntityBySummit<Page<LockInfo>> selectCondition(@ApiParam(value = "锁信息参数")  LockInfo lockInfo,
                                                                 @ApiParam(value = "当前页，大于等于1")  @RequestParam(value = "current", required = false) Integer current,
                                                                 @ApiParam(value = "每页条数，大于等于0")  @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        if(lockInfo == null){
            log.error("锁信息为空");
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9993,"锁信息为空", null);
        }
        Page<LockInfo> lockInfos = null;
        try {
            lockInfos = lockInfoService.selectCondition(lockInfo, new SimplePage(current,pageSize));
            filterInfo(lockInfos);
        } catch (Exception e) {
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999,"条件查询锁信息失败", lockInfos);
        }
        return ResultBuilder.buildError(ResponseCodeEnum.CODE_0000,"条件查询锁信息成功", lockInfos);
    }

    private void filterInfo(Page<LockInfo> lockInfos) {
        if(lockInfos == null)
            return;
        List<LockInfo> infoList = lockInfos.getContent();
        if(infoList == null)
            return;
        for(LockInfo lock : infoList) {
            lock.setDevices(null);
            lock.setRoles(null);
        }
    }
    private void filterInfo(LockInfo lockInfo) {
        if(lockInfo == null)
            return;
        lockInfo.setDevices(null);
        lockInfo.setRoles(null);
    }


    @ApiOperation(value = "插入锁信息", notes = "返回不为-1则为成功")
    @PostMapping(value = "/insertLock")
    public RestfulEntityBySummit<Integer> insertLock(@ApiParam(value = "锁信息参数") LockInfo lockInfo) {
        if(lockInfo == null){
            log.error("锁信息为空");
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9993,"锁信息为空", CommonConstants.UPDATE_ERROR);
        }
        if(lockInfoService.insertLock(lockInfo) == CommonConstants.UPDATE_ERROR)
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999,"插入锁信息失败", CommonConstants.UPDATE_ERROR);
        return ResultBuilder.buildError(ResponseCodeEnum.CODE_0000,"", CommonConstants.UPDATE_SUS);
    }


    @ApiOperation(value = "更新锁信息", notes = "返回不为-1则为成功")
    @PutMapping(value = "/updateLock")
    public RestfulEntityBySummit<Integer> updateLock(@ApiParam(value = "锁信息参数") LockInfo lockInfo) {
        if(lockInfo == null){
            log.error("锁信息为空");
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9993,"锁信息为空", CommonConstants.UPDATE_ERROR);
        }
        if(lockInfoService.updateLock(lockInfo) == CommonConstants.UPDATE_ERROR)
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999,"更新锁信息失败", CommonConstants.UPDATE_ERROR);
        return ResultBuilder.buildError(ResponseCodeEnum.CODE_0000,"更新锁信息成功", CommonConstants.UPDATE_SUS);
    }


    @ApiOperation(value = "根据id删除锁信息", notes = "返回不为-1则为成功")
    @DeleteMapping(value = "/delLockByLockId")
    public RestfulEntityBySummit<Integer>  delLockByLockId(@ApiParam(value = "锁id", required = true)  @RequestParam(value = "lockId") String lockId) {
        if(lockId == null){
            log.error("锁id为空");
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999,"锁id为空", CommonConstants.UPDATE_ERROR);
        }
        if(lockInfoService.delLockByLockId(lockId) == CommonConstants.UPDATE_ERROR)
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999,"根据id删除锁信息失败", CommonConstants.UPDATE_ERROR);
        return ResultBuilder.buildError(ResponseCodeEnum.CODE_0000,"根据id删除锁信息成功", CommonConstants.UPDATE_SUS);
    }


    @ApiOperation(value = "根据锁编号删除锁信息", notes = "返回不为-1则为成功")
    @DeleteMapping(value = "/delLockByLockCod")
    public RestfulEntityBySummit<Integer> delLockByLockCode(@ApiParam(value = "锁编号", required = true)  @RequestParam(value = "lockCode") String lockCode) {
        if(lockCode == null){
            log.error("锁编号为空");
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999,"锁编号为空", CommonConstants.UPDATE_ERROR);
        }
        if(lockInfoService.delLockByLockId(lockCode) == CommonConstants.UPDATE_ERROR)
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999,"根据锁编号删除锁信息失败", CommonConstants.UPDATE_ERROR);
        return ResultBuilder.buildError(ResponseCodeEnum.CODE_0000,"根据锁编号删除锁信息成功", CommonConstants.UPDATE_SUS);
    }
    
    
}
