package com.summit.util;

import com.alibaba.fastjson.JSONObject;
import com.summit.common.Common;
import com.summit.common.entity.RestfulEntityBySummit;
import com.summit.service.ICbbUserAuthService;
import jodd.util.StringUtil;

import java.util.List;

public class DeptUtil {

    public static JSONObject getAllDeptByPdept(JSONObject paramJson, ICbbUserAuthService cbbUserAuthService) {
        List<String> deptData=null;
        String pdept="";
        if(paramJson!=null && paramJson.containsKey("pdept") &&  !StringUtil.isEmpty(paramJson.getString("pdept")) ){
            pdept=paramJson.getString("pdept");
        }else{
            if(Common.getLogUser().getDepts()!=null && Common.getLogUser().getDepts().length>0){
                pdept=Common.getLogUser().getDepts()[0];
            }
        }
        if(pdept!=null && !StringUtil.isEmpty(pdept)){
            RestfulEntityBySummit<List<String>> alldeptList=cbbUserAuthService.queryAllDeptByPdept(pdept);
            if(alldeptList!=null && "CODE_0000".equals(alldeptList.getCode())){
                deptData=alldeptList.getData();
            }
        }
        JSONObject jsonOject=new JSONObject();
        jsonOject.put("pdept", pdept);
        jsonOject.put("deptList", deptData);
        return jsonOject;
    }
}