package com.summit.controller;

import com.summit.common.entity.ResponseCodeEnum;
import com.summit.common.entity.RestfulEntityBySummit;
import com.summit.common.util.ResultBuilder;
import com.summit.dao.entity.LockProcess;
import com.summit.dao.entity.SimplePage;
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

import java.util.List;

@Slf4j
@Api(tags = "锁操作历史接口")
@RestController
@RequestMapping("/lockHistory")
public class LockHistoryController {

    @Autowired
    private LockRecordService lockRecordService;

    @ApiOperation(value = "查询全部锁操作记录", notes = "分页参数为空则查全部，不合法则查询不到结果")
    @GetMapping(value = "/selectAllLockHistory")
    public RestfulEntityBySummit<List<LockProcess>> selectAllLockHistory(@ApiParam(value = "当前页，大于等于1")  @RequestParam("current") Integer current,
                                                                          @ApiParam(value = "每页条数，大于等于0")  @RequestParam("pageSize") Integer pageSize) {

        List<LockProcess> lockProcesses = null;
        try {
            lockProcesses = lockRecordService.selectAll(new SimplePage(current,pageSize));
        } catch (Exception e) {
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999,"查询锁操作记录失败", lockProcesses);
        }
        return ResultBuilder.buildError(ResponseCodeEnum.CODE_0000,"查询锁操作记录成功", lockProcesses);

    }

}
