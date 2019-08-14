package com.summit.controller;

import com.summit.cbb.utils.page.Page;
import com.summit.common.entity.RestfulEntityBySummit;
import com.summit.dao.entity.AccessControlInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@Api(tags = "门禁信息接口")
@RestController
@RequestMapping("/accessControlInfo")
public class AccessControlInfoController {

    @ApiOperation(value = "分页查询全部门禁信息", notes = "分页参数为空则查全部，current和pageSize有一个为null则查询不到结果，current<=0则置为1，pageSize<=0则查不到结果")
    @GetMapping(value = "/queryAccessControlsByPage")
    public RestfulEntityBySummit<Page<AccessControlInfo>> queryAccessControlsByPage(){


        return null;
    }
    @ApiOperation(value = "录入门禁信息", notes = "录入门禁信息时同时录入锁信息和设备信息")
    @GetMapping(value = "/insertAccessControl")
    public RestfulEntityBySummit<Page<AccessControlInfo>> insertAccessControl(@RequestBody AccessControlInfo accessControlInfo){

        System.out.println(accessControlInfo);
        return null;
    }


}
