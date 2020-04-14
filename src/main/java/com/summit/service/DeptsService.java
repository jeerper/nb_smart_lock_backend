package com.summit.service;

import com.alibaba.fastjson.JSONObject;

import java.util.List;

public interface DeptsService  {

    /**
     * 根据pdept查询下面所有的子节点(不包括父节点、多级)
     * @param paramJson
     * @return
     */
    List<String> getDeptsByPdept(JSONObject paramJson);

    //查找当前登录人所在的部门
    String  getCurrentDeptService() throws Exception;
}
