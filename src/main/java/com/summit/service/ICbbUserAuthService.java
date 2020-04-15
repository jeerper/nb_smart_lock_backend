package com.summit.service;

import com.summit.common.entity.DeptBean;
import com.summit.common.entity.RestfulEntityBySummit;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 用户管理组件远程通信接口
 */
@FeignClient(value = "cbb-userauth")
public interface ICbbUserAuthService {

    /**
     *
     * @param pdept
     * @return 根据pdept查询下面所有的子节点
     */
    @GetMapping("/dept/queryLowerAllDeptByPdept")
    RestfulEntityBySummit<List<String>> queryAllDeptByPdept(@RequestParam("pdept") String pdept);

    /**
     * 查询所有的部门信息
     * @return
     */
    @GetMapping("/dept/queryAllDept")
    RestfulEntityBySummit<List<DeptBean>> queryAllDept();

}
