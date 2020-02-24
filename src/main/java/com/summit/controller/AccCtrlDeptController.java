package com.summit.controller;

import com.summit.cbb.utils.page.Page;
import com.summit.common.entity.ResponseCodeEnum;
import com.summit.common.entity.RestfulEntityBySummit;
import com.summit.common.util.ResultBuilder;
import com.summit.constants.CommonConstants;
import com.summit.dao.entity.SimplePage;
import com.summit.entity.AccCtrlDept;
import com.summit.service.AccCtrlDeptService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
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
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
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


    @ApiOperation(value = "分页查询部门门禁权限", notes = "分页参数为空则查全部，current和pageSize有一个为null则查询不到结果，current<=0则置为1，pageSize<=0则查不到结果")
    @GetMapping(value = "/selectAccCtrlDeptsByPage")
    public RestfulEntityBySummit<Page<AccCtrlDept>> selectAccCtrlDeptsByPage(@ApiParam(value = "当前页，大于等于1")@RequestParam(value ="current" ,required = false)Integer current,
                                                                             @ApiParam(value = "每页条数，大于等于0")  @RequestParam(value = "pageSize", required = false) Integer pageSize){
        Page<AccCtrlDept> accCtrlDeptPage=null;
        try{
            accCtrlDeptPage = accCtrlDeptService.selectAccCtrlDeptsByPage(new SimplePage(current, pageSize));
        }catch (Exception e ){

        }
        log.error("分页查询部门门禁权限成功");
        return ResultBuilder.buildError(ResponseCodeEnum.CODE_0000,"分页查询部门门禁权限成功",accCtrlDeptPage);
    }

    @ApiOperation(value = "根据部门id查询已经授权的门禁信息列表", notes = "查询已经和部门关联的门禁信息列表")
    @GetMapping(value = "/selectAccCtrlInfoByDeptId")
    public RestfulEntityBySummit<List<String>> selectAccCtrlInfoByDeptId(@ApiParam(value = "部门id") @RequestParam(value = "deptId", required =
            false) String deptId) {
        if (deptId == null) {
            log.error("部门id为空");
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999, "部门id为空", null);
        }
        List<String> ids = new ArrayList<>();
        try {
            List<AccCtrlDept> accCtrlDepts = accCtrlDeptService.selectAccCtrlInfoByDeptId(deptId);
            if (accCtrlDepts != null) {
                for (AccCtrlDept accCtrlDept : accCtrlDepts) {
                    ids.add(accCtrlDept.getAccessControlId());
                }
            }
        } catch (Exception e) {
            log.error("查询门禁信息列表失败");
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999, "查询门禁信息列表失败", ids);
        }
        return ResultBuilder.buildError(ResponseCodeEnum.CODE_0000, "查询门禁信息列表成功", ids);
    }






}

