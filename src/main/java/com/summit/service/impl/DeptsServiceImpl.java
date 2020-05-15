package com.summit.service.impl;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.summit.common.entity.DeptBean;
import com.summit.service.DeptsService;
import com.summit.service.ICbbUserAuthService;
import com.summit.util.CommonUtil;
import com.summit.util.DeptUtil;
import jodd.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

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
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String getCurrentDeptService() throws Exception {
        JSONObject current_dept = DeptUtil.getCurrentDeptByPDept(null);
        String currentDept=current_dept.getString("currentDept");
        return currentDept;
    }

    /**
     * 根据当前节点查询当前节点以上所有的父节点(部门id)(包括不当前节点、多级)
     * @param paramJson
     * @return
     */
    @Override
    public List<String> getParentDeptsByCurrentDept(JSONObject paramJson) {
        try{
            List<String> depts=new ArrayList<>();
            JSONObject objct= DeptUtil.getParentAllDeptByCurrentDept(paramJson, cbbUserAuthService);
            String pdept=objct.getString("pdept");
            List<DeptBean> deptData=(List)objct.getJSONArray("deptList");
            if(deptData!=null && deptData.size()>0) {
                for(int i = 0; i < deptData.size() ; i++){
                    String dept = String.valueOf(deptData.get(i));
                    depts.add(dept);
                }
            }
            /*if (null !=pdept && !StringUtil.isEmpty(pdept)){
                depts.add(pdept);
            }*/
            return depts;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据当前节点查询当前节点以上所有的父节点(部门名称)(包括不当前节点、多级)
     * @param paramJson
     * @return
     */
    @Override
    public List<String> getParentDeptNamesByCurrentDept(JSONObject paramJson) {
        try{
            List<String> depts=new ArrayList<>();
            JSONObject objct= DeptUtil.getParentDeptNamesByCurrentDept(paramJson, cbbUserAuthService);
            String pdept=objct.getString("pdept");
            List<DeptBean> deptData=(List)objct.getJSONArray("deptList");
            if(deptData!=null && deptData.size()>0) {
                for(int i = 0; i < deptData.size() ; i++){
                    String dept = String.valueOf(deptData.get(i));
                    depts.add(dept);
                }
            }
            /*if (null !=pdept && !StringUtil.isEmpty(pdept)){
                depts.add(pdept);
            }*/
            return depts;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据所传部门Id或者当前登录人Id获取所有子部门Id
     * @param deptIds
     * @return
     */
    public Map<Integer,List<String>> getAllDeptIdByLoginDeptIdOrParameterDeptId(String deptIds) throws Exception {
        List<String> dept_ids =new ArrayList<>();
        String currentDeptService = getCurrentDeptService();
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("pdept",currentDeptService);
        List<String> userDepts = getDeptsByPdept(jsonObject);
        if (StrUtil.isNotBlank(deptIds)){
            if (deptIds.contains(",")){//多个部门
                String[] list = deptIds.split(",");
                List<String> deptIdList = Arrays.asList(list);
                for (String deptId:deptIdList){
                    JSONObject paramJson=new JSONObject();
                    paramJson.put("pdept",deptId);
                    List<String> depts = getDeptsByPdept(paramJson);
                    if (!CommonUtil.isEmptyList(depts)){
                        for (String dept_id:depts){
                            dept_ids.add(dept_id);
                        }
                    }
                }
            }else {//一个部门
                JSONObject paramJson=new JSONObject();
                paramJson.put("pdept",deptIds);
                List<String> depts = getDeptsByPdept(paramJson);
                if (!CommonUtil.isEmptyList(depts)){
                    for (String dept_id:depts){
                        dept_ids.add(dept_id);
                    }
                }
            }
        }else {
            if (!CommonUtil.isEmptyList(userDepts)){
                for (String dept_id:userDepts){
                    dept_ids.add(dept_id);
                }
            }
        }
        Map<Integer,List<String>> map=new HashMap<>();
        map.put(1,dept_ids);
        map.put(2,userDepts);
        return map;
    }
}
