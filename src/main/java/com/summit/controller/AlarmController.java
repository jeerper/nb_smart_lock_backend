package com.summit.controller;

import com.summit.cbb.utils.page.Page;
import com.summit.common.entity.ResponseCodeEnum;
import com.summit.common.entity.RestfulEntityBySummit;
import com.summit.common.entity.UserInfo;
import com.summit.common.util.ResultBuilder;
import com.summit.common.web.filter.UserContextHolder;
import com.summit.constants.CommonConstants;
import com.summit.dao.entity.Alarm;
import com.summit.dao.entity.SimplePage;
import com.summit.entity.BackLockInfo;
import com.summit.entity.LockRequest;
import com.summit.entity.UpdateAlarmParam;
import com.summit.exception.ErrorMsgException;
import com.summit.sdk.huawei.model.LcokProcessResultType;
import com.summit.service.AlarmService;
import com.summit.service.impl.NBLockServiceImpl;
import com.summit.util.CommonUtil;
import com.summit.util.AccCtrlProcessUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

@Slf4j
@Api(tags = "门禁告警接口")
@RestController
@RequestMapping("/accessControlAlarm")
public class AlarmController {

    @Autowired
    private AlarmService alarmService;
    @Autowired
    private NBLockServiceImpl nbLockServiceImpl;
    @Autowired
    private AccCtrlProcessUtil accCtrlProcessUtil;

    @ApiOperation(value = "更新门禁告警状态", notes = "alarmId为null或空串则不进行更新告警操作")
    @PutMapping(value = "/updateAlarmStatus")
        public RestfulEntityBySummit<String> updateAlarmStatus(@RequestBody UpdateAlarmParam updateAlarmParam) {
        if(updateAlarmParam == null){
            log.error("参数为空");
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9993,"参数为空",null);
        }
        String alarmId = updateAlarmParam.getAlarmId();
        String accCtrlProId = updateAlarmParam.getAccCtrlProId();
//        if(alarmId == null && accCtrlProId == null){
//            log.error("alarmId和processId不能同时为空");
//            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9993,"alarmId和processId不能同时为空",null);
//        }

        String msg = "操作成功";
        boolean noUpdatedStatus = true;
        boolean needUnLock = updateAlarmParam.isNeedUnLock();
        String lockId = updateAlarmParam.getLockId();
        Integer alarmStatus = updateAlarmParam.getAlarmStatus();
        String processRemark = updateAlarmParam.getProcessRemark();
        String operName = null;
        UserInfo userInfo = UserContextHolder.getUserInfo();
        if(userInfo != null)
            operName = userInfo.getName();
        if(needUnLock){
            LockRequest lockRequest = new LockRequest();
            lockRequest.setLockId(lockId);
            lockRequest.setOperName(operName);
            lockRequest.setAccCtrlProId(accCtrlProId);
            RestfulEntityBySummit result = null;
            try {
                result = nbLockServiceImpl.toUnLock(lockRequest);
            } catch (Exception e) {
                log.error("开锁失败,{}",e.getMessage());
                if(e instanceof ErrorMsgException)
                    return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999, ((ErrorMsgException) e).getErrorMsg(),null);
                return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999);
            }
            if(result == null){
                return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999,msg + "开锁失败",null);
            }
            Object data = result.getData();
            accCtrlProcessUtil.toInsertAndUpdateData(data,lockRequest);
            noUpdatedStatus = false;
            BackLockInfo backLockInfo = null;
            msg = "开锁结果：" + result.getMsg() + ";";
            if((data instanceof BackLockInfo)){
                backLockInfo = (BackLockInfo) data;
                msg = "开锁结果：" + backLockInfo.getContent();
                if(!LcokProcessResultType.SUCCESS.getCode().equals(backLockInfo.getType())){
                    return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999,result.getMsg(),null);
                }
            }
        }

        if(alarmId == null){
            log.info(msg);
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_0000, msg,null);
        }
        Alarm alarm = new Alarm();
        alarm.setAlarmId(alarmId);
        alarm.setAccCtrlProId(accCtrlProId);
        alarm.setAlarmStatus(alarmStatus);
        alarm.setProcessPerson(operName);
        alarm.setProcessRemark(processRemark);
        alarm.setUpdatetime(new Date());
        LockRequest lockRequest = new LockRequest();
        lockRequest.setLockId(lockId);
        lockRequest.setOperName(operName);
        if(noUpdatedStatus){
            //若上面未更新过告警，这里更新锁和门禁为当前真实状态
            String lockCodeById = accCtrlProcessUtil.getLockCodeById(lockId);
            try {
                Integer status = accCtrlProcessUtil.getLockStatus(lockRequest);
                accCtrlProcessUtil.toUpdateAccCtrlAndLockStatus(status,lockCodeById);
            } catch (Exception e) {
                log.error(msg + "获取锁状态失败");
            }
        }
        try {
            alarmService.updateAlarm(alarm);
        } catch (Exception e) {
            log.error(msg + "更新告警状态失败");
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999,msg + "更新告警状态失败",null);
        }
        return ResultBuilder.buildError(ResponseCodeEnum.CODE_0000,msg + "更新告警状态成功",null);
    }


    @ApiOperation(value = "用告警id列表批量删除门禁告警信息", notes = "alarmIds不能为空")
    @DeleteMapping(value = "/delAlarmByIdBatch")
    public RestfulEntityBySummit<String> delAlarmByIdBatch(@ApiParam(value = "门禁告警id数组", required = true) @RequestParam(value = "alarmIds") List<String> alarmIds) {
        if(CommonUtil.isEmptyList(alarmIds)){
            log.error("告警id为空");
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9993,"告警id为空",null);
        }
        try {
            alarmService.delAlarmByIdBatch(alarmIds);
        } catch (Exception e) {
            log.error("删除告警信息失败");
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999,"删除告警信息失败",null);
        }
        return ResultBuilder.buildError(ResponseCodeEnum.CODE_0000,"删除告警信息成功",null);
    }



    @ApiOperation(value = "查询当前用户所有未处理的告警数量", notes = "查询当前用户所有未处理的告警数量")
    @GetMapping(value = "/queryUntreatedAlarmCount")
    public RestfulEntityBySummit<Integer> queryUntreatedAlarmCount() {

        Integer alarmCount;
        try {
            alarmCount = alarmService.selectAlarmCountByStatus(1);
        } catch (Exception e) {
            log.error("查询未处理告警数量失败");
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999,"查询未处理告警数量失败", null);
        }
        return ResultBuilder.buildError(ResponseCodeEnum.CODE_0000,"查询未处理告警数量成功", alarmCount);
    }

    @ApiOperation(value = "根据告警id查询告警信息", notes = "alarmId不能为空。查询唯一一条告警信息")
    @GetMapping(value = "/queryAlarmById")
    public RestfulEntityBySummit<Alarm> queryAlarmById(@ApiParam(value = "门禁告警id", required = true) @RequestParam(value = "alarmId") String alarmId){
        if(alarmId == null){
            log.error("告警id为空");
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9993,"告警id为空",null);
        }
        Alarm alarm = null;
        try {
            alarm = alarmService.selectAlarmById(alarmId);
        } catch (Exception e) {
            log.error("根据告警id查询告警信息失败");
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999,"根据告警id查询告警信息失败", alarm);
        }
        return ResultBuilder.buildError(ResponseCodeEnum.CODE_0000,"根据告警id查询告警信息成功", alarm);
    }


    @ApiOperation(value = "根据所传一个或多个条件组合分页查询告警信息", notes = "各字段都为空则查询全部。时间信息为空或不合法则无时间限制。分页参数为空则查全部，current和pageSize有一个为null则查询不到结果，current<=0则置为1，pageSize<=0则查不到结果")
    @GetMapping(value = "/queryAlarmCondition")
    public RestfulEntityBySummit<Page<Alarm>> queryAlarmCondition(@ApiParam(value = "门禁id，精确匹配") @RequestParam(value = "accessControlId", required = false) String accessControlId,
                                                                  @ApiParam(value = "门禁名称，模糊匹配") @RequestParam(value = "accessControlName", required = false) String accessControlName,
                                                                  @ApiParam(value = "门禁告警处理状态，，0已处理，1未处理") @RequestParam(value = "alarmStatus", required = false) Integer alarmStatus,
                                                                  @ApiParam(value = "起始时间")  @RequestParam(value = "startTime", required = false) String startTime,
                                                                  @ApiParam(value = "结束时间")  @RequestParam(value = "endTime", required = false) String endTime,
                                                                  @ApiParam(value = "当前页，大于等于1")  @RequestParam(value = "current", required = false) Integer current,
                                                                  @ApiParam(value = "每页条数，大于等于0")  @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        Page<Alarm> alarms = null;
        Date start = CommonUtil.parseStrToDate(startTime,CommonConstants.STARTTIMEMARK);
        Date end = CommonUtil.parseStrToDate(endTime,CommonConstants.ENDTIMEMARK);
        try {
            Alarm alarm = new Alarm();
            if(!CommonUtil.isEmptyStr(accessControlId))
                alarm.setAccessControlId(accessControlId);
            if(!CommonUtil.isEmptyStr(accessControlName))
                alarm.setAccessControlName(accessControlName);
            alarm.setAlarmStatus(alarmStatus);
            alarms = alarmService.selectAlarmConditionByPage(alarm, start, end, new SimplePage(current,pageSize));
        } catch (Exception e) {
            log.error("条件查询告警信息失败");
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999,"条件查询告警信息失败", alarms);
        }
        return ResultBuilder.buildError(ResponseCodeEnum.CODE_0000,"条件查询告警信息成功", alarms);
    }

}
