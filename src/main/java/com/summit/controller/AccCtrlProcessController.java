package com.summit.controller;

import com.summit.cbb.utils.page.Page;
import com.summit.common.entity.ResponseCodeEnum;
import com.summit.common.entity.RestfulEntityBySummit;
import com.summit.common.util.ResultBuilder;
import com.summit.constants.CommonConstants;
import com.summit.dao.entity.AccCtrlProcess;
import com.summit.dao.entity.AccessControlInfo;
import com.summit.dao.entity.AddAccCtrlprocess;
import com.summit.service.AccCtrlProcessService;
import com.summit.service.AccessControlService;
import com.summit.service.AddAccCtrlprocessService;
import com.summit.util.CommonUtil;
import com.summit.util.SummitTools;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.PropertyAccessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

@Slf4j
@Api(tags = "门禁操作记录接口")
@RestController
@RequestMapping("/accCtrlProcess")
public class AccCtrlProcessController {

    @Autowired
    private AccCtrlProcessService accCtrlProcessService;
    @Autowired
    private AddAccCtrlprocessService addAccCtrlprocessService;
    @Autowired
    private AccessControlService accessControlService;

    @ApiOperation(value = "根据id查询门禁操作记录", notes = "accCtrlProId不能为空。查询唯一一条门禁操作记录")
    @GetMapping(value = "/queryAccCtrlProcessById")
    public RestfulEntityBySummit<AccCtrlProcess> queryAccCtrlProcessById(@ApiParam(value = "门禁操作记录id", required = true) @RequestParam(value =
            "accCtrlProId") String accCtrlProId) {
        if (accCtrlProId == null) {
            log.error("门禁操作记录id为空");
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9993, "门禁操作记录id为空", null);
        }
        try {
            AccCtrlProcess accCtrlProcess = accCtrlProcessService.selectAccCtrlProcessById(accCtrlProId);
            return ResultBuilder.buildSuccess(accCtrlProcess);
        } catch (Exception e) {
            log.error("根据id查询门禁操作记录失败");
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999);
        }
    }

    @ApiOperation(value = "根据所传一个或多个条件组合分页查询门禁操作记录", notes = "各字段都为空则查询全部。时间信息为空或不合法则无时间限制。分页参数为空则查全部，current和pageSize有一个为null则查询不到结果，current<=0" +
            "则置为1，pageSize<=0则查不到结果")
    @GetMapping(value = "/queryAccCtrlProcessCondition")
    public RestfulEntityBySummit<Page<AccCtrlProcess>> queryAccCtrlProcessCondition(
            @ApiParam(value = "门禁名称") @RequestParam(value = "accessControlName", required = false) String accessControlName,
            @ApiParam(value = "门禁操作类型，1：开锁，2：关锁，3：告警") @RequestParam(value = "processType", required = false) Integer processType,
            @ApiParam(value = "起始时间") @RequestParam(value = "startTime", required = false) String startTime,
            @ApiParam(value = "结束时间") @RequestParam(value = "endTime", required = false) String endTime,
            @ApiParam(value = "当前页，大于等于1") @RequestParam(value = "current", required = false) Integer current,
            @ApiParam(value = "每页条数，大于等于0") @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        try {
            Date start = CommonUtil.strToDate(startTime, CommonConstants.STARTTIMEMARK);
            Date end = CommonUtil.strToDate(endTime, CommonConstants.ENDTIMEMARK);
            AccCtrlProcess accCtrlProcess = new AccCtrlProcess();
            accCtrlProcess.setAccessControlName(accessControlName);
            accCtrlProcess.setProcessType(processType);
            Page<AccCtrlProcess> accCtrlProcessPage = accCtrlProcessService.selectAccCtrlProcessCondition(accCtrlProcess, start, end, current, pageSize);
            return ResultBuilder.buildSuccess(accCtrlProcessPage);
        } catch (Exception e) {
            log.error("条件查询门禁操作记录失败", e);
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999);
        }
    }

    @ApiOperation(value = "用id列表批量删除门禁操作记录", notes = "accCtrlProIds不能为空")
    @DeleteMapping(value = "/delAccCtrlProcessByIdBatch")
    public RestfulEntityBySummit<String> delAccCtrlProcessByIdBatch(@ApiParam(value = "门禁告警id数组", required = true) @RequestParam(value =
            "accCtrlProIds") List<String> accCtrlProIds) {
        if (CommonUtil.isEmptyList(accCtrlProIds)) {
            log.error("门禁操作记录id列表为空");
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9993, "门禁操作记录id列表为空", null);
        }
        try {
            accCtrlProcessService.delAccCtrlProcessByIdBatch(accCtrlProIds);
        } catch (Exception e) {
            log.error("删除门禁操作记录失败");
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999, "删除门禁操作记录失败", null);
        }
        //同时删除分析统计表中门禁所对应的开关锁记录数量
        for(String accCtrlProId:accCtrlProIds){
            AccessControlInfo accessControlInfo = accessControlService.selectAccCtrlByIdBeyondAuthority(accCtrlProId);
            if (accessControlInfo.getStatus()== 1 || accessControlInfo.getStatus()== 2) {
                List<AddAccCtrlprocess> addAccCtrlprocesses = addAccCtrlprocessService.selectAddAccCtrlprocessByAccCtrlProId(accCtrlProId);
                if (!CommonUtil.isEmptyList(addAccCtrlprocesses)) {
                    AddAccCtrlprocess addAccCtrlprocess = addAccCtrlprocesses.get(0);
                    Integer accessControlStatusCount = addAccCtrlprocess.getAccessControlStatusCount();
                    accessControlStatusCount = accessControlStatusCount - 1;
                    AddAccCtrlprocess updateAddAccCtrlprocess = new AddAccCtrlprocess();
                    updateAddAccCtrlprocess.setId(addAccCtrlprocess.getId());
                    updateAddAccCtrlprocess.setAccessControlId(addAccCtrlprocess.getAccessControlId());
                    updateAddAccCtrlprocess.setAccessControlStatusCount(accessControlStatusCount);
                    updateAddAccCtrlprocess.setAccessControlName(addAccCtrlprocess.getAccessControlName());
                    addAccCtrlprocessService.update(updateAddAccCtrlprocess);
                }
            }
        }
        return ResultBuilder.buildError(ResponseCodeEnum.CODE_0000, "删除门禁操作记录成功", null);
    }
}
