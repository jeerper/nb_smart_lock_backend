package com.summit.controller;

import com.summit.cbb.utils.page.Page;
import com.summit.common.entity.ResponseCodeEnum;
import com.summit.common.entity.RestfulEntityBySummit;
import com.summit.common.util.ResultBuilder;
import com.summit.constants.CommonConstants;
import com.summit.dao.entity.AccCtrlProcess;
import com.summit.dao.entity.AccessControlInfo;
import com.summit.dao.entity.Alarm;
import com.summit.dao.entity.FileInfo;
import com.summit.dao.entity.SimplePage;
import com.summit.dao.repository.AlarmDao;
import com.summit.entity.AccCtrlRealTimeInfo;
import com.summit.sdk.huawei.model.AlarmStatus;
import com.summit.service.AccCtrlProcessService;
import com.summit.service.AccessControlService;
import com.summit.service.AlarmService;
import com.summit.service.FaceInfoService;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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


    @ApiOperation(value = "分页查询门禁操作实时信息", notes = "分页参数为空则查全部，current和pageSize有一个为null则查询不到结果，current<=0则置为1，pageSize<=0则查不到结果")
    @GetMapping(value = "/selectAccCtrlRealTimeByPage")
    public RestfulEntityBySummit<Map<String,Object>> selectAllLockRealTimeInfo(@ApiParam(value = "当前页，大于等于1")  @RequestParam(value = "current", required = false) Integer current,
                                                                               @ApiParam(value = "每页条数，大于等于0")  @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        Map<String,Object> data = new HashMap<>();
        List<AccCtrlRealTimeInfo> accCtrlRealTimeInfos;
//        List<LockInfo> lockInfos;
        List<AccessControlInfo> accessControlInfos;
        try {
            //查出有操作记录的门禁并再分页
            Page<AccessControlInfo> acPage = accessControlService.selectHaveHistoryByPage(new SimplePage(current, pageSize));
            if(acPage == null){
                return ResultBuilder.buildError(ResponseCodeEnum.CODE_0000,"无门禁操作记录成功", null);
            }
            accessControlInfos = acPage.getContent();
            accCtrlRealTimeInfos = accCtrlProcessUtil.getLockRealTimeInfo(accessControlInfos);
            Integer integer = alarmService.selectAlarmCountByStatus(AlarmStatus.UNPROCESSED.getCode());
            int allAlarmCount = integer == null ? 0 : integer;
            data.put("allAlarmCount",allAlarmCount);
            data.put("content", accCtrlRealTimeInfos);
            data.put("pageable",acPage.getPageable());
        } catch (Exception e) {
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999,"查询门禁操作记录失败", data);
        }
        return ResultBuilder.buildError(ResponseCodeEnum.CODE_0000,"查询门禁操作记录成功", data);

    }

}
