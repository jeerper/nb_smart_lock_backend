package com.summit.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.summit.common.entity.DeptBean;
import com.summit.service.DeptsService;
import com.summit.service.ICbbUserAuthService;
import com.summit.util.DeptUtil;
import jodd.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DeptsServiceImpl implements DeptsService {

    @Autowired
    private ICbbUserAuthService cbbUserAuthService;

    @Override
    public List<String> getDeptsByPdept(JSONObject paramJson) {
        try{
            List<String> depts=new ArrayList<>();
            JSONObject objct= DeptUtil.getAllDeptByPdept(paramJson, cbbUserAuthService);
            String pdept=objct.getString("pdept");
            List<DeptBean> deptData=(List)objct.getJSONArray("deptList");
            if(deptData!=null && deptData.size()>0) {
                for(int i = 0; i < deptData.size() ; i++){
                    String dept = String.valueOf(deptData.get(i));
                    depts.add(dept);
                }
            }
            if (null !=pdept && !StringUtil.isEmpty(pdept)){
                depts.add(pdept);
            }
            return depts;
           /* if(deptData!=null && deptData.size()>0) {
                String depts = "'"+pdept+"'";
                for(int i = 0; i < deptData.size() ; i++){
                    String dept = String.valueOf(deptData.get(i));
                    depts = depts+",'"+ dept+"'";
                }
                String ss = depts.substring(0,1);
                String es = depts.substring(depts.length()-1,depts.length());
                if(ss.equals("'") && es.equals("'")){
                    return depts.substring(1,depts.length()-1);
                }else{
                    return "";
                }
            }else{
                return "";
            }*/
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
