package com.summit.controller;

import com.summit.cbb.utils.page.Page;
import com.summit.common.entity.ResponseCodeEnum;
import com.summit.common.entity.RestfulEntityBySummit;
import com.summit.common.util.ResultBuilder;
import com.summit.constants.CommonConstants;
import com.summit.dao.entity.AccCtrlProcess;
import com.summit.dao.entity.SimplePage;
import com.summit.sdk.huawei.model.LockProcessType;
import com.summit.service.AccCtrlProcessService;
import com.summit.util.CommonUtil;
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

@Slf4j
@Api(tags = "门禁操作记录接口")
@RestController
@RequestMapping("/accCtrlProcess")
public class AccCtrlProcessController {

    @Autowired
    private AccCtrlProcessService accCtrlProcessService;

    @ApiOperation(value = "根据所传一个或多个条件组合分页查询门禁操作记录", notes = "各字段都为空则查询全部。时间信息为空或不合法则无时间限制。分页参数为空则查全部，current和pageSize有一个为null则查询不到结果，current<=0则置为1，pageSize<=0则查不到结果")
    @GetMapping(value = "/queryAlarmCondition")
    public RestfulEntityBySummit<Page<AccCtrlProcess>> queryAlarmCondition(@ApiParam(value = "门禁操作记录id") @RequestParam(value = "accCtrlProId", required = false) String accCtrlProId,
                                                                           @ApiParam(value = "门禁名称") @RequestParam(value = "accessControlName", required = false) String accessControlName,
                                                                           @ApiParam(value = "门禁操作类型，1：开锁，2：关锁，3：告警") @RequestParam(value = "processType", required = false) Integer processType,
                                                                           @ApiParam(value = "起始时间")  @RequestParam(value = "startTime", required = false) String startTime,
                                                                           @ApiParam(value = "结束时间")  @RequestParam(value = "endTime", required = false) String endTime,
                                                                           @ApiParam(value = "当前页，大于等于1")  @RequestParam(value = "current", required = false) Integer current,
                                                                           @ApiParam(value = "每页条数，大于等于0")  @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        Page<AccCtrlProcess> accCtrlProcessPage = null;
        Date start = CommonUtil.parseStrToDate(startTime, CommonConstants.STARTTIMEMARK);
        Date end = CommonUtil.parseStrToDate(endTime,CommonConstants.ENDTIMEMARK);
        try {
            AccCtrlProcess accCtrlProcess = new AccCtrlProcess();
            if(!"".equals(accCtrlProId))
                accCtrlProcess.setAccCtrlProId(accCtrlProId);
            if(!"".equals(accessControlName))
                accCtrlProcess.setAccessControlName(accessControlName);
            accCtrlProcess.setProcessType(processType);
            accCtrlProcessPage = accCtrlProcessService.selectAccCtrlProcessCondition(accCtrlProcess, start, end, new SimplePage(current, pageSize));
        } catch (Exception e) {
            log.error("条件查询门禁操作记录失败");
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999,"条件查询门禁操作记录失败", accCtrlProcessPage);
        }
        return ResultBuilder.buildError(ResponseCodeEnum.CODE_0000,"条件查询门禁操作记录成功", accCtrlProcessPage);
    }

}
