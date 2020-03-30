package com.summit.controller;

import com.summit.common.entity.ResponseCodeEnum;
import com.summit.common.entity.RestfulEntityBySummit;
import com.summit.common.util.ResultBuilder;
import com.summit.dao.entity.AddAccCtrlprocess;
import com.summit.service.AddAccCtrlprocessService;
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

    @ApiOperation(value = "查询全部统计分析信息，其中包括统计分析信息的id,门禁的id,门禁的名称,开锁或者关锁的次数,电池的电量,报警次数")
    @GetMapping(value = "/selectAddAccCtrlprocess")
    public RestfulEntityBySummit<List<AddAccCtrlprocess>> selectAddAccCtrlprocess(
            @ApiParam(value = "门禁id") @RequestParam(value = "accCtrlIds", required =false) List<String> accCtrlIds) {
        List<AddAccCtrlprocess> addAccCtrlprocesses=null;
        try {
            addAccCtrlprocesses=addAccCtrlprocessService.selectAddAccCtrlprocess(accCtrlIds);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("查询全部统计分析信息失败");
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999,"查询全部统计分析信息失败", addAccCtrlprocesses);
        }
        return ResultBuilder.buildError(ResponseCodeEnum.CODE_0000,"查询全部统计分析信息成功",addAccCtrlprocesses);
    }
}
