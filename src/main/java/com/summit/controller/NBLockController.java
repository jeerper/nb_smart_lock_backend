package com.summit.controller;


import com.summit.common.entity.ResponseCodeEnum;
import com.summit.common.entity.RestfulEntityBySummit;
import com.summit.common.entity.UserInfo;
import com.summit.common.util.ResultBuilder;
import com.summit.common.web.filter.UserContextHolder;
import com.summit.constants.CommonConstants;
import com.summit.dao.entity.AccCtrlProcess;
import com.summit.dao.entity.AccessControlInfo;
import com.summit.dao.repository.AccCtrlProcessDao;
import com.summit.dao.repository.LockInfoDao;
import com.summit.entity.BackLockInfo;
import com.summit.entity.LockRequest;
import com.summit.entity.ReportParam;
import com.summit.sdk.huawei.model.LcokProcessResultType;
import com.summit.sdk.huawei.model.LockProcessMethod;
import com.summit.sdk.huawei.model.LockStatus;
import com.summit.service.AccCtrlProcessService;
import com.summit.service.AccessControlService;
import com.summit.service.CameraDeviceService;
import com.summit.service.impl.NBLockServiceImpl;
import com.summit.util.AccCtrlProcessUtil;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@Slf4j
@Api(tags = "NB智能锁操作接口")
@RestController
@RequestMapping("/nbLock")
public class NBLockController {


    @Autowired
    private NBLockServiceImpl nbLockServiceImpl;
    @Autowired
    private AccCtrlProcessUtil accCtrlProcessUtil;

    @PostMapping(value = "/queryLockStatus")
    public RestfulEntityBySummit queryLockStatus(@RequestBody LockRequest lockRequest){
        return nbLockServiceImpl.toQueryLockStatus(lockRequest);
    }

    @PostMapping(value = "/unLock")
    public RestfulEntityBySummit unLock(@RequestBody LockRequest lockRequest){
        RestfulEntityBySummit result;
        boolean isUnLock;
        if(lockRequest == null){
            log.error("请求参数为空");
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9993,"请求参数为空", null);

        }
        result = nbLockServiceImpl.toUnLock(lockRequest);
        //查询调用查询锁状态接口，插入一条锁操作记录，改变锁、门禁状态
        if(result != null)
            accCtrlProcessUtil.toInsertAndUpdateData(result.getData(),lockRequest);
        return result;
    }


    @PostMapping(value = "/safeReport")
    public RestfulEntityBySummit safeReport(@RequestBody ReportParam reportInfo){
        return nbLockServiceImpl.toSafeReport(reportInfo);
    }



}
