package com.summit.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.summit.common.Common;
import com.summit.dao.entity.AccessControlInfo;
import com.summit.dao.entity.AddAccCtrlprocess;
import com.summit.dao.repository.AddAccCtrlprocessDao;
import com.summit.dao.repository.FaceInfoAccCtrlDao;
import com.summit.exception.ErrorMsgException;
import com.summit.service.AddAccCtrlprocessService;
import com.summit.service.DeptsService;
import com.summit.service.FaceInfoAccCtrlService;
import com.summit.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2019/9/16.
 */
@Service
@Slf4j
public class AddAccCtrlprocessServiceImpl implements AddAccCtrlprocessService {
    @Autowired
    private AddAccCtrlprocessDao addAccCtrlprocessDao;
    @Autowired
    private FaceInfoAccCtrlService faceInfoAccCtrlService;
    @Autowired
    private DeptsService deptsService;
    @Autowired
    private FaceInfoAccCtrlDao faceInfoAccCtrlDao;
    /**
     * 分析统计记录插入
     * @param addAccCtrlprocess
     * @return -1则为不成功
     */
    @Override
    public int insert(AddAccCtrlprocess addAccCtrlprocess) {
        int add = addAccCtrlprocessDao.insert(addAccCtrlprocess);
        return add;
    }


    /**
     * 修改统计分析记录
     * @param addAccCtrlprocess
     * @return -1则为不成功
     */
    @Override
    public int update(AddAccCtrlprocess addAccCtrlprocess) {
        UpdateWrapper<AddAccCtrlprocess> updateWrapper=new UpdateWrapper<>();
        int i = addAccCtrlprocessDao.update(addAccCtrlprocess, updateWrapper.eq("access_control_id", addAccCtrlprocess.getAccessControlId()));
        return i;
    }

    /**
     * 根据门禁的id查询当前的统计分析记录
     * @param accCtrlProId
     * @return  一条统计分析记录
     */
    @Override
    public List<AddAccCtrlprocess> selectAddAccCtrlprocessByAccCtrlProId(String accCtrlProId) {
        QueryWrapper<AddAccCtrlprocess> wrapper=new QueryWrapper<>();
        List<AddAccCtrlprocess> addAccCtrlprocesses = addAccCtrlprocessDao.selectList(wrapper.eq("access_control_id", accCtrlProId));
        return addAccCtrlprocesses;
    }

    /**
     * 查询所有的统计分析记录
     * @return 所有的统计分析记录
     */
    @Override
    public List<AddAccCtrlprocess> selectAddAccCtrlprocess(List<String> accCtrlIds) {
        List<AddAccCtrlprocess> addAccCtrlprocesses=null;
        if (!CommonUtil.isEmptyList(accCtrlIds)){
             addAccCtrlprocesses=addAccCtrlprocessDao.selectAddAcpByAcIds(accCtrlIds);
        }else {
            if(Common.getLogUser().getDepts()!=null && Common.getLogUser().getDepts().length>0){
                String pdept=Common.getLogUser().getDepts()[0];
                JSONObject paramJson=new JSONObject();
                paramJson.put("pdept",pdept);
                List<String> depts = deptsService.getDeptsByPdept(paramJson);
                List<AccessControlInfo> accessControlInfos= faceInfoAccCtrlDao.selectAllAccCtrlByDeptId(depts);
                List<String> actIds=new ArrayList<>();
                for (AccessControlInfo accessControlInfo:accessControlInfos){
                    actIds.add(accessControlInfo.getAccessControlId());
                }
                addAccCtrlprocesses=addAccCtrlprocessDao.selectAddAcpByAcIds(actIds);
            }
        }
        /*List<String> depts = UserDeptAuthUtils.getDepts();
        List<AddAccCtrlprocess> addAccCtrlprocesses=addAccCtrlprocessDao.selectAddAccCtrlprocessDesc(depts);*/
        return addAccCtrlprocesses;
    }
    /**
     * 根据门禁id修改统计记录表
     * @return 所有的统计分析记录
     */
    @Override
    public int updateAddAccCtrlprocess(String accessControlId ) {
       int i= addAccCtrlprocessDao.updateAddAccCtrlprocess(accessControlId);
        return i;
    }

    /**
     * 根据门禁id查询
     * @return 所有的统计分析记录
     */
    @Override
    public AddAccCtrlprocess selectAddAccCtrlByAccCtrlID(String accessControlId) {
        QueryWrapper<AddAccCtrlprocess> wrapper=new QueryWrapper<>();
        AddAccCtrlprocess addAccCtrlprocess;
        List<AddAccCtrlprocess> addAccCtrlprocesses = addAccCtrlprocessDao.selectList(wrapper.eq("access_control_id", accessControlId));
        if(!CommonUtil.isEmptyList(addAccCtrlprocesses)){
             addAccCtrlprocess = addAccCtrlprocesses.get(0);
        }else {
            addAccCtrlprocess=null;
        }
        return addAccCtrlprocess;
    }

    /**
     * 根据统计分析记录表id删除统计分析字段
     * @return -1则没有删除成功
     */
    @Override
    public int deladdAccCtrlprocessByAccCtrlId(List<String> needDeladdAccCtrlprocessIds) {
        int result = -1;
        try {
            result = addAccCtrlprocessDao.deleteBatchIds(needDeladdAccCtrlprocessIds);
        } catch (Exception e) {
            log.error("删除统计信息失败");
            throw new ErrorMsgException("删除统计信息失败");
        }
        return result;
    }

    /**
     * 修改统计分析记录报警次数
     * @param accessControlId
     * @return
     */
    @Override
    public int updateAddAccProcessAlarmCount(String accessControlId) {
        int i=addAccCtrlprocessDao.updateAddAccProcessAlarmCount(accessControlId);
        return i;
    }
}
