package com.summit.controller;

import com.summit.cbb.utils.page.Page;
import com.summit.common.entity.ResponseCodeEnum;
import com.summit.common.entity.RestfulEntityBySummit;
import com.summit.common.util.ResultBuilder;
import com.summit.constants.CommonConstants;
import com.summit.dao.entity.AccCtrlRole;
import com.summit.dao.entity.SimplePage;
import com.summit.service.AccCtrlRoleService;
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

import java.util.List;

@Slf4j
@Api(tags = "门禁授权管理接口")
@RestController
@RequestMapping("/accCtrlRole")
public class AccCtrlRoleController {

    @Autowired
    private AccCtrlRoleService accCtrlRoleService;

    @ApiOperation(value = "批量刷新指定角色关联的门禁", notes = "为指定角色更新门禁权限，所传角色之前没有关联某门禁且所传列表中有则添加，之前已关联某门禁权限而所传列表中有则不添加，之前已关联某门禁权限而所传列表中没有则删除")
    @PostMapping(value = "/refreshAccCtrlRoleBatch")
    public RestfulEntityBySummit<String> refreshAccCtrlRoleBatch(@ApiParam(value = "门禁id列表", required = true) @RequestParam(value = "accessControlIds") List<String> accessControlIds,
                                                           @ApiParam(value = "角色code", required = true) @RequestParam(value = "roleCode") String roleCode) {
        if(accessControlIds == null){
            log.error("门禁id列表为空");
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9993,"门禁id列表为空",null);
        }
        if(roleCode == null){
            log.error("门禁角色信息为空");
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9993,"门禁角色信息为空",null);
        }
        int result = accCtrlRoleService.refreshAccCtrlRoleBatch(accessControlIds, roleCode);
        if(result == CommonConstants.UPDATE_ERROR){
            log.error("角色门禁授权失败");
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999,"角色门禁授权失败",null);
        }
        log.error("角色门禁授权成功");
        return ResultBuilder.buildError(ResponseCodeEnum.CODE_0000,"角色门禁授权成功",null);
    }

    @ApiOperation(value = "分页查询角色门禁权限", notes = "分页参数为空则查全部，current和pageSize有一个为null则查询不到结果，current<=0则置为1，pageSize<=0则查不到结果")
    @GetMapping(value = "/selectAccCtrlRolesByPage")
    public RestfulEntityBySummit<Page<AccCtrlRole>> selectAccCtrlRolesByPage(@ApiParam(value = "当前页，大于等于1")  @RequestParam(value = "current", required = false) Integer current,
                                                      @ApiParam(value = "每页条数，大于等于0")  @RequestParam(value = "pageSize", required = false) Integer pageSize){


        Page<AccCtrlRole> accCtrlRolePage = null;
        try {
            accCtrlRolePage = accCtrlRoleService.selectAccCtrlRolesByPage(new SimplePage(current, pageSize));
        } catch (Exception e) {
            log.error("分页查询角色门禁权限失败");
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999,"分页查询角色门禁权限失败",accCtrlRolePage);
        }
        log.error("分页查询角色门禁权限成功");
        return ResultBuilder.buildError(ResponseCodeEnum.CODE_0000,"分页查询角色门禁权限成功",accCtrlRolePage);
    }

}
