package com.summit.controller;


import com.summit.common.entity.ResponseCodeEnum;
import com.summit.common.entity.RestfulEntityBySummit;
import com.summit.common.util.ResultBuilder;
import com.summit.entity.LockRequest;
import com.summit.entity.ReportParam;
import com.summit.exception.ErrorMsgException;
import com.summit.service.impl.NBLockServiceImpl;
import com.summit.util.AccCtrlProcessUtil;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


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
        try {
            return nbLockServiceImpl.toQueryLockStatus(lockRequest);
        } catch (Exception e) {
            log.error("查询锁状态失败,{}",e.getMessage());
            if(e instanceof ErrorMsgException)
                return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999, ((ErrorMsgException) e).getErrorMsg(),null);
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999);
        }
    }

    @PostMapping(value = "/unLock")
    public RestfulEntityBySummit unLock(@RequestBody LockRequest lockRequest){
        RestfulEntityBySummit result;
        if(lockRequest == null){
            log.error("请求参数为空");
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9993,"请求参数为空", null);

        }
        try {
            result = nbLockServiceImpl.toUnLock(lockRequest);
            //查询调用查询锁状态接口，插入一条锁操作记录，改变锁、门禁状态
            if(result != null)
                accCtrlProcessUtil.toInsertAndUpdateData(result.getData(),lockRequest);
        } catch (Exception e) {
            log.error("开锁失败,{}",e.getMessage());
            if(e instanceof ErrorMsgException)
                return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999, ((ErrorMsgException) e).getErrorMsg(),null);
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999);
        }
        return result;
    }


    @PostMapping(value = "/safeReport")
    public RestfulEntityBySummit safeReport(@RequestBody ReportParam reportInfo){
        return nbLockServiceImpl.toSafeReport(reportInfo);
    }



}
