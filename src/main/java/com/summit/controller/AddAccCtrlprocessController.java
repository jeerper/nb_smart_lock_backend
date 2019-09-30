package com.summit.controller;

import com.summit.common.entity.ResponseCodeEnum;
import com.summit.common.entity.RestfulEntityBySummit;
import com.summit.common.util.ResultBuilder;
import com.summit.dao.entity.AddAccCtrlprocess;
import com.summit.entity.BackLockInfo;
import com.summit.entity.LockRequest;
import com.summit.service.AddAccCtrlprocessService;
import com.summit.util.AccCtrlProcessUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by Administrator on 2019/9/18.
 */
@Slf4j
@Api(tags = "统计分析接口")
@RequestMapping("/addAccCtrlprocess")
@RestController
public class AddAccCtrlprocessController {
    @Autowired
    private AddAccCtrlprocessService addAccCtrlprocessService;
    @Autowired
    private AccCtrlProcessUtil accCtrlProcessUtil;
    @ApiOperation(value = "查询全部统计分析信息，其中包括统计分析信息的id,门禁的id,门禁的名称,开锁或者关锁的次数,电池的电量")
    @GetMapping(value = "/selectAddAccCtrlprocess")
    public RestfulEntityBySummit<List<AddAccCtrlprocess>> selectAddAccCtrlprocess() {
        List<AddAccCtrlprocess> addAccCtrlprocesses=null;
        try {
            addAccCtrlprocesses=addAccCtrlprocessService.selectAddAccCtrlprocess(null);
            LockRequest lockRequest = new LockRequest();
            for (AddAccCtrlprocess addAccCtrlprocess:addAccCtrlprocesses){
                    String lockCode = addAccCtrlprocess.getLockCode();
                    lockRequest.setTerminalNum(lockCode);
                    BackLockInfo backLockInfo= accCtrlProcessUtil.getLockStatus(lockRequest);
                    if (backLockInfo == null) {
                        continue;
                    }
                    Integer vol = backLockInfo.getVol();
                    if (vol == null) {
                        continue;
                    }
                    addAccCtrlprocess.setBatteryLeve(vol);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("查询全部统计分析信息失败");
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999,"查询全部统计分析信息失败", addAccCtrlprocesses);
        }
        return ResultBuilder.buildError(ResponseCodeEnum.CODE_0000,"查询全部的省份成功",addAccCtrlprocesses);
    }
}
