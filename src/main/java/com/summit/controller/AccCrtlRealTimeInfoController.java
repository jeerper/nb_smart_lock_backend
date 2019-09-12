package com.summit.controller;

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
    public RestfulEntityBySummit<Map<String,Object>> selectAllLockRealTimeInfo(@ApiParam(value = "当前页，大于等于1")  @RequestParam(value = "current", required = false) Integer current,
                                                                               @ApiParam(value = "每页条数，大于等于0")  @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        Map<String,Object> data = new HashMap<>();
        try {
            //查出有操作记录的门禁并再分页
            Page<AccCtrlRealTimeEntity> accCtrlRealTimePage = accCtrlRealTimeService.selectByConditionPage(null, null, null, current,pageSize);
            Integer integer = alarmService.selectAlarmCountByStatus(AlarmStatus.UNPROCESSED.getCode());
            int allAlarmCount = integer == null ? 0 : integer;
            data.put("allAlarmCount",allAlarmCount);
            data.put("content", accCtrlRealTimePage.getContent());
            data.put("pageable",accCtrlRealTimePage.getPageable());
        } catch (Exception e) {
            log.error("查询门禁操作记录失败",e);
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999,"查询门禁操作记录失败", data);
        }
        return ResultBuilder.buildError(ResponseCodeEnum.CODE_0000,"查询门禁操作记录成功", data);

    }

    @ApiOperation(value = "从所有门禁实时信息中查询最后更新时间毫秒值", notes = "从所有门禁实时信息中查询最后更新时间毫秒值")
    @GetMapping(value = "/selectLastUpdatetime")
    public RestfulEntityBySummit<Long> selectLastUpdatetime(){
        Long updatetime = null;
        try {
            updatetime = accCtrlRealTimeService.selectLastUpdatetime();
        } catch (Exception e) {
            log.error("查询门禁最近更新时间失败");
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999,"查询门禁最近更新时间失败", updatetime);
        }
        return ResultBuilder.buildError(ResponseCodeEnum.CODE_0000,"查询门禁最近更新时间成功", updatetime);
    }





}
