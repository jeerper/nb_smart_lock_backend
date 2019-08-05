package com.summit.controller;

import com.summit.common.entity.ResponseCodeEnum;
import com.summit.common.entity.RestfulEntityBySummit;
import com.summit.common.util.ResultBuilder;
import com.summit.dao.entity.LockProcess;
import com.summit.dao.entity.Page;
import com.summit.service.LockRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.List;

@Slf4j
@Api(tags = "锁操作历史接口")
@RestController
@RequestMapping("/lockHistory")
public class LockHistoryController {

    @Autowired
    private LockRecordService lockRecordService;

    /**
     * 增删改操作异常
     */
    private static final Integer UPDATE_ERROR = -1;

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @ApiOperation(value = "查询全部锁操作记录", notes = "分页参数为空则查全部，不合法则取第一条")
    @GetMapping(value = "/selectAll")
    public RestfulEntityBySummit<List<LockProcess>> selectAll(@ApiParam(value = "当前页，大于等于1")  @RequestParam("alarmName") Integer current,
                                                                          @ApiParam(value = "每页条数，大于等于1")  @RequestParam("alarmName") Integer pageSize) {

        List<LockProcess> lockProcesses = null;
        try {
            if(current == null && pageSize == null){
                lockProcesses = lockRecordService.selectAll(null);
                return ResultBuilder.buildError(ResponseCodeEnum.CODE_0000,"查询锁操作记录成功", lockProcesses);
            }
            lockProcesses = lockRecordService.selectAll(new Page(current,pageSize));
        } catch (Exception e) {
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999,"查询锁操作记录失败", lockProcesses);
        }
        return ResultBuilder.buildError(ResponseCodeEnum.CODE_0000,"查询锁操作记录成功", lockProcesses);

    }

}
