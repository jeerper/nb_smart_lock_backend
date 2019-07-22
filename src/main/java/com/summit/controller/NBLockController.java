package com.summit.controller;


import com.summit.common.entity.ResponseCodeEnum;
import com.summit.common.entity.RestfulEntityBySummit;
import com.summit.common.util.ResultBuilder;
import com.summit.entity.LockInfo;
import com.summit.entity.LockRequest;
import com.summit.entity.ReportParam;
import com.summit.service.impl.NBLockServiceImpl;
import com.summit.util.HttpClient;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import retrofit2.HttpException;
import rx.Observable;
import rx.Observer;
import rx.functions.Func1;

import java.util.concurrent.CountDownLatch;

@Slf4j
@Api(tags = "NB智能锁接口")
@RestController
@RequestMapping("/nbLock")
public class NBLockController {


    @Autowired
    private HttpClient httpClient;
    @Autowired
    private NBLockServiceImpl nbLockServiceImpl;

    @PostMapping(value = "/queryLockStatus")
    public RestfulEntityBySummit queryLockStatus(@RequestBody LockRequest lockRequest){
        return nbLockServiceImpl.toQueryLockStatus(lockRequest);
    }

    @PostMapping(value = "/unLock")
    public RestfulEntityBySummit unLock(@RequestBody LockRequest lockRequest){

        return nbLockServiceImpl.toUnLock(lockRequest);
    }

    @PostMapping(value = "/safeReport")
    public RestfulEntityBySummit safeReport(@RequestBody ReportParam reportInfo){
        return nbLockServiceImpl.toSafeReport(reportInfo);
    }

}
