package com.summit.controller;

import com.summit.common.entity.ResponseCodeEnum;
import com.summit.common.entity.RestfulEntityBySummit;
import com.summit.common.util.ResultBuilder;
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

    /**
     * 增删改操作异常
     */
    private static final Integer UPDATE_ERROR = -1;

    private static final SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @ApiOperation(value = "查询全部锁信息", notes = "分页参数为空则查全部，不合法则取第一条")
    @GetMapping(value = "/selectAllLockInfo")
    public RestfulEntityBySummit<List<LockInfo>> selectAllLockInfo(@ApiParam(value = "当前页，大于等于1")  @RequestParam("alarmName") Integer current,
                                                                           @ApiParam(value = "每页条数，大于等于1")  @RequestParam("alarmName") Integer pageSize) {



        List<LockInfo> lockInfos = null;
        try {
            lockInfos = lockInfoService.selectAll(new Page(current,pageSize));
        } catch (Exception e) {
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999,"查询锁操作记录失败", lockInfos);
        }
        return ResultBuilder.buildError(ResponseCodeEnum.CODE_0000,"查询锁操作记录成功", lockInfos);

    }

}
