package com.summit.controller;

import com.summit.common.entity.ResponseCodeEnum;
import com.summit.common.entity.RestfulEntityBySummit;
import com.summit.common.util.ResultBuilder;
import com.summit.constants.CommonConstants;
import com.summit.service.AccCtrlDeptService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@Api(tags = "门禁给部门授权接口")
@RestController
@RequestMapping("/accCtrlDept")
public class AccCtrlDeptController {

    @Autowired
    private AccCtrlDeptService accCtrlDeptService;

    @ApiOperation(value = "批量刷新指定部门关联的门禁", notes = "为指定部门更新门禁权限，所传部门之前没有关联某门禁且所传列表中有则添加，之前已关联某门禁权限而所传列表中有则不添加，之前已关联某门禁权限而所传列表中没有则删除")
    @PostMapping(value = "/refreshAccCtrlDeptBatch")
    public RestfulEntityBySummit<String> refreshAccCtrlDeptBatch(@ApiParam(value = "门禁id列表", required = true) @RequestParam(value = "accessControlIds") List<String> accessControlIds,
                                                                 @ApiParam(value = "部门id", required = true) @RequestParam(value = "deptId") String deptId) {
        if(accessControlIds == null ){
            log.error("门禁id列表为空");
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9993,"门禁id列表为空",null);
        }
        if(deptId == null){
            log.error("部门id为空");
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9993,"部门id为空",null);
        }
        int result = accCtrlDeptService.refreshAccCtrlDeptBatch(accessControlIds, deptId);
        if(result == CommonConstants.UPDATE_ERROR){
            log.error("部门门禁授权失败");
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999,"部门门禁授权失败",null);
        }
        log.error("角色门禁授权成功");
        return ResultBuilder.buildError(ResponseCodeEnum.CODE_0000,"部门门禁授权成功",null);
    }
}

