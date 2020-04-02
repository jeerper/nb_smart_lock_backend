package com.summit.controller;

import cn.hutool.core.date.DateUtil;
import com.summit.cbb.utils.page.Page;
import com.summit.common.entity.ResponseCodeEnum;
import com.summit.common.entity.RestfulEntityBySummit;
import com.summit.common.util.ResultBuilder;
import com.summit.dao.entity.AccCtrlRealTimeEntity;
import com.summit.sdk.huawei.model.AlarmStatus;
import com.summit.service.AccCtrlRealTimeService;
import com.summit.service.AccessControlService;
import com.summit.service.AlarmService;
import com.summit.util.AccCtrlProcessUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Api(tags = "门禁实时信息接口")
@RestController
@RequestMapping("/accCtrlRealTimeInfo")
public class AccCrtlRealTimeInfoController {

    @Autowired
    private AlarmService alarmService;
    @Autowired
    private AccessControlService accessControlService;
    @Autowired
    private AccCtrlProcessUtil accCtrlProcessUtil;
    @Autowired
    private AccCtrlRealTimeService accCtrlRealTimeService;


    @ApiOperation(value = "分页查询门禁操作实时信息", notes = "分页参数为空则查全部，current和pageSize有一个为null则查询不到结果，current<=0则置为1，pageSize<=0则查不到结果")
    @GetMapping(value = "/selectAccCtrlRealTimeByPage")
    public RestfulEntityBySummit<Map<String, Object>> selectAllLockRealTimeInfo(AccCtrlRealTimeEntity accCtrlRealTimeEntity,
                                                                                @ApiParam(value = "当前页，大于等于1") @RequestParam(value = "current", required = false) Integer current,
                                                                                @ApiParam(value = "每页条数，大于等于0") @RequestParam(value = "pageSize", required = false) Integer pageSize,
                                                                                @ApiParam(value = "起始时间") @RequestParam(value = "startTime", required = false) String startTime,
                                                                                @ApiParam(value = "结束时间") @RequestParam(value = "endTime", required = false) String endTime
                                                                                /*@ApiParam(value = "部门id")@RequestParam(value = "deptId", required = false)String deptId,
                                                                                @ApiParam(value = "门禁名称")@RequestParam(value = "accCtrlName", required = false)String accCtrlName,
                                                                                @ApiParam(value = "工作状态") @RequestParam(value = "workStatus(1:实时在线,2：忙时在线，3：闲时在线)", required = false) Integer workStatus,
                                                                                @ApiParam(value = "告警状态") @RequestParam(value = "alarmStatus(1:非法开锁告警,2：低电量告警，3：掉线告警,4:低电压告警,5:故障告警)", required = false) Integer alarmStatus,
                                                                                @ApiParam(value = "开关锁状态") @RequestParam(value = "lockStatus(1:开锁,2：关锁，3：不在线)", required = false) Integer lockStatus*/) {
        Map<String, Object> data = new HashMap<>();
        try {
            Date start = DateUtil.parse(startTime);
            Date end = DateUtil.parse(endTime);
            //查出有操作记录的门禁并再分页
            Page<AccCtrlRealTimeEntity> accCtrlRealTimePage = accCtrlRealTimeService.selectByConditionPage(accCtrlRealTimeEntity, start, end, current, pageSize);
            Integer integer = alarmService.selectAlarmCountByStatus(AlarmStatus.UNPROCESSED.getCode());
            int allAlarmCount = integer == null ? 0 : integer;
            data.put("allAlarmCount", allAlarmCount);
          /*  List<AccCtrlRealTimeEntity> accCtrlRealTimeEntities = accCtrlRealTimePage.getContent();
            for (AccCtrlRealTimeEntity ctrlRealTimeEntity :accCtrlRealTimeEntities){
                AlarmProcessType alarmProcessType = AlarmProcessType.codeOf(ctrlRealTimeEntity.getAlarmStatus());
                ctrlRealTimeEntity.setAlarmStatusName( alarmProcessType.getDescription());
            }*/
            data.put("content", accCtrlRealTimePage.getContent());
            data.put("pageable", accCtrlRealTimePage.getPageable());
        } catch (Exception e) {
            log.error("查询门禁操作记录失败", e);
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999, "查询门禁操作记录失败", data);
        }
        return ResultBuilder.buildError(ResponseCodeEnum.CODE_0000, "查询门禁操作记录成功", data);

    }

    @ApiOperation(value = "从所有门禁实时信息中查询最后更新时间毫秒值", notes = "从所有门禁实时信息中查询最后更新时间毫秒值")
    @GetMapping(value = "/selectLastUpdatetime")
    public RestfulEntityBySummit<Long> selectLastUpdatetime() {
        Long updatetime = null;
        try {
            updatetime = accCtrlRealTimeService.selectLastUpdatetime();
        } catch (Exception e) {
            log.error("查询门禁最近更新时间失败");
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999, "查询门禁最近更新时间失败", updatetime);
        }
        return ResultBuilder.buildError(ResponseCodeEnum.CODE_0000, "查询门禁最近更新时间成功", updatetime);
    }


    @ApiOperation(value = "查询告警次数，其中告警状态为未处理，门禁操作类型为告警")
    @GetMapping(value = "/selectAllAlarmCount")
    public RestfulEntityBySummit<Integer> selectAllAlarmCount() {
        Integer allAlarmCount=0;
        try{
            allAlarmCount = alarmService.selectAlarmCountByStatus(AlarmStatus.UNPROCESSED.getCode());
        }catch (Exception e){
            log.error("查询告警次数失败", e);
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999, "查询告警次数失败", allAlarmCount);
        }
        return ResultBuilder.buildError(ResponseCodeEnum.CODE_0000, "查询告警次数成功", allAlarmCount);
    }
}
