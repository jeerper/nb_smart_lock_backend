package com.summit.controller;

import com.summit.common.entity.ResponseCodeEnum;
import com.summit.common.entity.RestfulEntityBySummit;
import com.summit.common.util.ResultBuilder;
import com.summit.constants.CommonConstants;
import com.summit.dao.entity.FaceInfoEntity;
import com.summit.dao.entity.LockInfo;
import com.summit.dao.entity.LockProcess;
import com.summit.dao.entity.Page;
import com.summit.entity.LockRealTimeInfo;
import com.summit.sdk.huawei.model.CardType;
import com.summit.service.FaceInfoService;
import com.summit.service.LockInfoService;
import com.summit.service.LockRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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


    @ApiOperation(value = "查询全部锁信息", notes = "分页参数为空则查全部，不合法则取第一条")
    @GetMapping(value = "/selectAllLockInfo")
    public RestfulEntityBySummit<List<LockInfo>> selectAllLockInfo(@ApiParam(value = "当前页，大于等于1")  @RequestParam(value = "alarmName", required = false) Integer current,
                                                                   @ApiParam(value = "每页条数，大于等于0")  @RequestParam(value = "alarmName", required = false) Integer pageSize) {

        List<LockInfo> lockInfos = null;
        try {
            lockInfos = lockInfoService.selectAll(new Page(current,pageSize));
        } catch (Exception e) {
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999,"查询锁操作记录失败", lockInfos);
        }
        return ResultBuilder.buildError(ResponseCodeEnum.CODE_0000,"查询锁操作记录成功", lockInfos);
    }

    /**
     * 根据锁id查询唯一锁信息
     * @param lockId 锁id
     * @return 唯一锁信息对象
     */
    public RestfulEntityBySummit<LockInfo> selectLockById(String lockId) {

        return ResultBuilder.buildError(ResponseCodeEnum.CODE_0000,"", null);
    }

    /**
     * 根据锁编号查询唯一锁信息
     * @param lockCode 锁编号
     * @return 唯一锁信息对象
     */
    public RestfulEntityBySummit<LockInfo> selectBylockCode(String lockCode) {

        return ResultBuilder.buildError(ResponseCodeEnum.CODE_0000,"", null);
    }


    public RestfulEntityBySummit<List<LockInfo>> selectAllHaveHistory(Page page) {


        return ResultBuilder.buildError(ResponseCodeEnum.CODE_0000,"", null);
    }

    /**
     * 条件查询锁信息
     * @param lockInfo 锁信息对象
     * @param page 分页对象
     * @return 锁信息列表
     */
    
    public RestfulEntityBySummit<List<LockInfo>> selectCondition(LockInfo lockInfo, Page page) {
        if(lockInfo == null){
            log.error("锁信息为空");
            return null;
        }

        return ResultBuilder.buildError(ResponseCodeEnum.CODE_0000,"", null);
    }

    /**
     * 插入锁信息
     * @param lockInfo 锁信息对象
     * @return 返回不为-1则为成功
     */
    public RestfulEntityBySummit<Integer> insertLock(LockInfo lockInfo) {
        if(lockInfo == null){
            log.error("锁信息为空");
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999,"插入锁信息失败", CommonConstants.UPDATE_ERROR);
        }
        return ResultBuilder.buildError(ResponseCodeEnum.CODE_0000,"", null);
    }

    /**
     * 更新锁信息
     * @param lockInfo 锁信息对象
     * @return 返回不为-1则为成功
     */
    
    public RestfulEntityBySummit<Integer>  updateLock(LockInfo lockInfo) {
        if(lockInfo == null){
            log.error("锁信息为空");
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999,"更新锁信息失败", CommonConstants.UPDATE_ERROR);
        }


        return ResultBuilder.buildError(ResponseCodeEnum.CODE_0000,"", null);
    }

    /**
     * 根据id删除锁信息
     * @param lockId 锁id
     * @return 返回不为-1则为成功
     */
    
    public RestfulEntityBySummit<Integer>  delLockByLockId(String lockId) {
        if(lockId == null){
            log.error("锁id为空");
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999,"更新锁信息失败", CommonConstants.UPDATE_ERROR);
        }

        return ResultBuilder.buildError(ResponseCodeEnum.CODE_0000,"", null);
    }

    /**
     * 根据锁编号删除锁信息
     * @param lockCode 锁编号
     * @return 返回不为-1则为成功
     */
    public RestfulEntityBySummit<Integer> delLockByLockCod(String lockCode) {
        if(lockCode == null){
            log.error("锁id为空");
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999,"更新锁信息失败", CommonConstants.UPDATE_ERROR);
        }

        return ResultBuilder.buildError(ResponseCodeEnum.CODE_0000,"", null);
    }
    
    
}
