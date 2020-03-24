package com.summit.service;

import com.alibaba.fastjson.JSONObject;

import java.util.List;

public interface DeptsService  {

    List<String> getDeptsByPdept(JSONObject paramJson);
}
