package com.summit.controller;

import com.summit.common.entity.ResponseCodeEnum;
import com.summit.common.entity.RestfulEntityBySummit;
import com.summit.common.util.ResultBuilder;
import com.summit.dao.entity.Alarm;
import com.summit.dao.entity.CameraDevice;
import com.summit.dao.entity.LockProcess;
import com.summit.dao.entity.Page;
import com.summit.service.AlarmService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Api(tags = "锁告警接口")
@RestController
@RequestMapping("/lockAlarm")
public class AlarmController {

    @Autowired
    private AlarmService alarmService;

    /**
     * 增删改操作异常
     */
    private static final Integer UPDATE_ERROR = -1;

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @ApiOperation(value = "添加告警信息", notes = "alarmId和processId不能为空，时间若为空则取当前时间")
    @PostMapping(value = "/insertAlarm")
    public RestfulEntityBySummit<String> insertAlarm(@ApiParam(value = "锁告警信息", required = true) @RequestBody Alarm alarm) {
        if(alarm == null){
            log.error("告警信息为空");
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9993,"告警信息为空",null);
        }
        if(alarm.getProcessId() == null){
            log.error("告警对应所操作记录id为空");
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9993,"告警对应所操作记录id为空",null);
        }

        int result = alarmService.insertAlarm(alarm);
        if(result == UPDATE_ERROR){
            log.error("插入告警信息失败");
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999,"告警信息为空",null);
        }
        return ResultBuilder.buildError(ResponseCodeEnum.CODE_0000,"插入告警信息成功",null);
    }

    @ApiOperation(value = "更新锁告警信息", notes = "alarmId和processId不能同时为空，若两个都不为空以alarmId作为更新条件，若alarmId为空则以processId作为更新条件，时间若为空则取当前时间")
    @PutMapping(value = "/updateAlarm")
    public RestfulEntityBySummit<String> updateAlarm(@ApiParam(value = "锁告警各字段", required = true) @RequestBody Alarm alarm) {
        if(alarm == null){
            log.error("告警信息为空");
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9993,"告警信息为空",null);
        }
        if(alarm.getAlarmId() == null && alarm.getProcessId() == null){
            log.error("alarmId和processId不能同时为空");
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9993,"alarmId和processId不能同时为空",null);
        }
        int result = alarmService.updateAlarm(alarm);
        if(result == UPDATE_ERROR){
            log.error("更新告警信息失败");
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999,"告警信息为空",null);
        }
        return ResultBuilder.buildError(ResponseCodeEnum.CODE_0000,"更新告警信息成功",null);
    }

    @ApiOperation(value = "更新锁告警状态", notes = "alarmId和processId不能同时为空，若两个都不为空以alarmId作为更新条件，若alarmId为空则以processId作为更新条件，时间取当前时间")
    @PutMapping(value = "/updateAlarmStatus")
        public RestfulEntityBySummit<String> updateAlarmStatus(@ApiParam(value = "锁告警状态", required = true) @RequestParam("alarmStatus") Integer alarmStatus,
                                                           @ApiParam(value = "锁告警状态") @RequestParam(value = "alarmId",required = false) String alarmId,
                                                           @ApiParam(value = "锁告警状态") @RequestParam(value = "processId",required = false) String processId) {
        if(alarmStatus == null){
            log.error("锁告警状态为空");
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9993,"锁告警状态为空",null);
        }
        if(alarmId == null && processId == null){
            log.error("alarmId和processId不能同时为空");
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9993,"alarmId和processId不能同时为空",null);
        }
        Alarm alarm = new Alarm();
        if(alarmId != null){
            alarm.setAlarmId(alarmId);
        }else{
            alarm.setProcessId(processId);
        }
        alarm.setAlarmStatus(alarmStatus);
        alarm.setAlarmTime(new Date());
        int result = alarmService.updateAlarm(alarm);
        if(result == UPDATE_ERROR){
            log.error("更新告警状态失败");
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999,"更新告警状态失败",null);
        }
        return ResultBuilder.buildError(ResponseCodeEnum.CODE_0000,"更新告警状态成功",null);
    }


    @ApiOperation(value = "用告警id删除锁告警信息", notes = "alarmId和processId不能为空，时间若为空则取当前时间")
    @DeleteMapping(value = "/delLockAlarmById")
    public RestfulEntityBySummit<String> delLockAlarmById(@ApiParam(value = "锁告警id", required = true) @RequestParam("alarmId") String alarmId) {
        if(alarmId == null){
            log.error("告警id为空");
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9993,"告警id为空",null);
        }
        int result = alarmService.delLockAlarmById(alarmId);
        if(result == UPDATE_ERROR){
            log.error("删除告警信息失败");
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999,"删除告警信息失败",null);
        }
        return ResultBuilder.buildError(ResponseCodeEnum.CODE_0000,"删除告警信息成功",null);
    }

    @ApiOperation(value = "查询全部告警信息", notes = "分页参数为空则查全部，不合法则取第一条")
    @PostMapping(value = "/queryAll")
    public RestfulEntityBySummit<List<Alarm>> queryAll(@ApiParam(value = "当前页，大于等于1")  @RequestParam("alarmName") Integer current,
                                                       @ApiParam(value = "每页条数，大于等于1")  @RequestParam("alarmName") Integer pageSize) {
        List<Alarm> alarms = null;
        try {
            if(current == null && pageSize == null){
                alarms = alarmService.selectAll(null);
                return ResultBuilder.buildError(ResponseCodeEnum.CODE_0000,"查询告警信息成功", alarms);
            }
            alarms = alarmService.selectAll(new Page(current,pageSize));
        } catch (Exception e) {
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999,"查询告警信息失败", alarms);
        }
        return ResultBuilder.buildError(ResponseCodeEnum.CODE_0000,"查询告警信息成功", alarms);
    }

    @ApiOperation(value = "根据告警status查询告警信息", notes = "alarmStatus不能为空，0已处理，1未处理，传其他值则无法查到记录。时间信息为空或不合法则无时间限制。分页参数为空则查全部，不合法则取第一条")
    @GetMapping(value = "/queryAlarmByStatus")
    public RestfulEntityBySummit<List<Alarm>> queryAlarmByStatus(@ApiParam(value = "锁告警状态，0已处理，1未处理", required = true)  @RequestParam("alarmStatus") Integer alarmStatus,
                                                                 @ApiParam(value = "起始时间")  @RequestParam("alarmName") String startTime,
                                                                 @ApiParam(value = "结束时间")  @RequestParam("alarmName") String endTime,
                                                                 @ApiParam(value = "当前页，大于等于1")  @RequestParam("alarmName") Integer current,
                                                                 @ApiParam(value = "每页条数，大于等于1")  @RequestParam("alarmName") Integer pageSize) {
        if(alarmStatus == null){
            log.error("告警状态为空");
            return null;
        }
        List<Alarm> alarms = null;
        Date start = null;
        Date end = null;
        if(startTime != null){
            try {
                start = dateFormat.parse(startTime);
            } catch (ParseException e) {
                log.warn("开始时间不合法");
            }
        }
        if(endTime != null){
            try {
                end = dateFormat.parse(endTime);
            } catch (ParseException e) {
                log.warn("结束时间不合法");
            }
        }
        
        try {
            if(current == null && pageSize == null){
                alarms = alarmService.selectAlarmByStatus(alarmStatus,start,end,null);
                return ResultBuilder.buildError(ResponseCodeEnum.CODE_0000,"查询告警信息成功", alarms);
            }
            alarms = alarmService.selectAlarmByStatus(alarmStatus,start,end,new Page(current,pageSize));
        } catch (Exception e) {
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999,"查询告警信息失败", alarms);
        }
        return ResultBuilder.buildError(ResponseCodeEnum.CODE_0000,"查询告警信息成功", alarms);
    }

    @ApiOperation(value = "根据告警status查询告警信息", notes = "时间信息为空或不合法则无时间限制。分页参数为空则查全部，不合法则取第一条")
    @GetMapping(value = "/queryUntreatedAlarm")
    public RestfulEntityBySummit<List<Alarm>> queryUntreatedAlarm(@ApiParam(value = "起始时间")  @RequestParam("alarmName") String startTime,
                                                                 @ApiParam(value = "结束时间")  @RequestParam("alarmName") String endTime,
                                                                 @ApiParam(value = "当前页，大于等于1")  @RequestParam("alarmName") Integer current,
                                                                 @ApiParam(value = "每页条数，大于等于1")  @RequestParam("alarmName") Integer pageSize) {
        return queryAlarmByStatus(1,startTime,endTime,current,pageSize);
    }

    @ApiOperation(value = "根据告警status查询告警信息", notes = "时间信息为空或不合法则无时间限制。分页参数为空则查全部，不合法则取第一条")
    @GetMapping(value = "/queryUntreatedAlarmCount")
    public RestfulEntityBySummit<Integer> queryUntreatedAlarmCount(@ApiParam(value = "起始时间")  @RequestParam("alarmName") String startTime,
                                                              @ApiParam(value = "结束时间")  @RequestParam("alarmName") String endTime,
                                                              @ApiParam(value = "当前页，大于等于1")  @RequestParam("alarmName") Integer current,
                                                              @ApiParam(value = "每页条数，大于等于1")  @RequestParam("alarmName") Integer pageSize) {

        RestfulEntityBySummit<List<Alarm>> alarms = queryAlarmByStatus(1,startTime,endTime,current,pageSize);
        if(alarms == null || alarms.getData() == null){
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_0000,"查询未处理告警数量成功", 0);
        }
        return ResultBuilder.buildError(ResponseCodeEnum.CODE_0000,"查询未处理告警数量成功", alarms.getData().size());
    }



    //TODO --------------------------------------------------------------------------------------
    //TODO --------------------------------------------------------------------------------------
    @ApiOperation(value = "根据告警id查询告警信息", notes = "alarmId不能为空。查询唯一一条告警信息")
    @GetMapping(value = "/queryAlarmById")
    public RestfulEntityBySummit<Alarm> queryAlarmById(@ApiParam(value = "锁告警id", required = true) @RequestParam("alarmId") String alarmId){
        if(alarmId == null){
            log.error("告警id为空");
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9993,"告警id为空",null);
        }
        return null;
    }

    @ApiOperation(value = "根据告警名称查询告警信息", notes = "alarmName不能为空。时间信息为空或不合法则无时间限制。分页参数为空则查全部，不合法则取第一条")
    @GetMapping(value = "/queryAlarmByName")
    public RestfulEntityBySummit<List<Alarm>> queryAlarmByName(@ApiParam(value = "锁告警名称", required = true)  @RequestParam("alarmName") String alarmName,
                                                               @ApiParam(value = "起始时间")  @RequestParam("alarmName") String startTime,
                                                               @ApiParam(value = "结束时间")  @RequestParam("alarmName") String endTime,
                                                               @ApiParam(value = "当前页，大于等于1")  @RequestParam("alarmName") Integer current,
                                                               @ApiParam(value = "每页条数，大于等于1")  @RequestParam("alarmName") Integer pageSize) {
        if(alarmName == null){
            log.error("告警名称为空");
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9993,"告警名称为空",null);
        }
        return null;
    }


    @ApiOperation(value = "根据锁编号查询告警信息", notes = "lockCode不能为空。时间信息为空或不合法则无时间限制。分页参数为空则查全部，不合法则取第一条")
    @GetMapping(value = "/queryAlarmByLockCode")
    public RestfulEntityBySummit<List<Alarm>> queryAlarmByLockCode(@ApiParam(value = "锁编号", required = true)  @RequestParam("lockCode") String lockCode,
                                                                   @ApiParam(value = "起始时间")  @RequestParam("alarmName") String startTime,
                                                                   @ApiParam(value = "结束时间")  @RequestParam("alarmName") String endTime,
                                                                   @ApiParam(value = "当前页，大于等于1")  @RequestParam("alarmName") Integer current,
                                                                   @ApiParam(value = "每页条数，大于等于1")  @RequestParam("alarmName") Integer pageSize) {
        if(lockCode == null){
            log.error("锁编号为空");
            return null;
        }
        return null;
    }

    @ApiOperation(value = "根据告警对应的设备ip地址查询告警信息", notes = "deviceIp不能为空。时间信息为空或不合法则无时间限制。分页参数为空则查全部，不合法则取第一条")
    @GetMapping(value = "/queryAlarmByDeviceIp")
    public RestfulEntityBySummit<List<Alarm>> queryAlarmByDeviceIp(@ApiParam(value = "设备ip地址", required = true)  @RequestParam("deviceIp") String deviceIp,
                                                                   @ApiParam(value = "起始时间")  @RequestParam("alarmName") String startTime,
                                                                   @ApiParam(value = "结束时间")  @RequestParam("alarmName") String endTime,
                                                                   @ApiParam(value = "当前页，大于等于1")  @RequestParam("alarmName") Integer current,
                                                                   @ApiParam(value = "每页条数，大于等于1")  @RequestParam("alarmName") Integer pageSize) {
        if(deviceIp == null){
            log.error("设备ip地址为空");
            return null;
        }
        return null;
    }

    @ApiOperation(value = "根据告警对应的设备id查询告警信息", notes = "devId不能为空。时间信息为空或不合法则无时间限制。分页参数为空则查全部，不合法则取第一条")
    @GetMapping(value = "/queryAlarmByDevId")
    public RestfulEntityBySummit<List<Alarm>> queryAlarmByDevId(@ApiParam(value = "设备id", required = true)  @RequestParam("devId") String devId,
                                                                @ApiParam(value = "起始时间")  @RequestParam("alarmName") String startTime,
                                                                @ApiParam(value = "结束时间")  @RequestParam("alarmName") String endTime,
                                                                @ApiParam(value = "当前页，大于等于1")  @RequestParam("alarmName") Integer current,
                                                                @ApiParam(value = "每页条数，大于等于1")  @RequestParam("alarmName") Integer pageSize) {
        if(devId == null){
            log.error("设备id为空");
            return null;
        }
        return null;
    }

    @ApiOperation(value = "根据所传一个或多个条件组合查询告警信息", notes = "alarm为空或各字段都为空则查询全部。时间信息为空或不合法则无时间限制。分页参数为空则查全部，不合法则取第一条")
    @GetMapping(value = "/queryAlarmCondition")
    public RestfulEntityBySummit<List<Alarm>> queryAlarmCondition(@ApiParam(value = "锁告警信息") @RequestBody Alarm alarm,
                                                                  @ApiParam(value = "起始时间")  @RequestParam("alarmName") String startTime,
                                                                  @ApiParam(value = "结束时间")  @RequestParam("alarmName") String endTime,
                                                                  @ApiParam(value = "当前页，大于等于1")  @RequestParam("alarmName") Integer current,
                                                                  @ApiParam(value = "每页条数，大于等于1")  @RequestParam("alarmName") Integer pageSize) {
        if(alarm == null){
            log.error("告警信息为空");
            return null;
        }
        return null;
    }


}
