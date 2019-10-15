package com.summit.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.summit.dao.entity.AddAccCtrlprocess;
import com.summit.dao.entity.FaceInfo;
import com.summit.dao.entity.SimplePage;
import com.summit.dao.repository.AddAccCtrlprocessDao;
import com.summit.service.AddAccCtrlprocessService;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Administrator on 2019/9/16.
 */
@Service
@Slf4j
public class AddAccCtrlprocessServiceImpl implements AddAccCtrlprocessService {
    @Autowired
    private AddAccCtrlprocessDao addAccCtrlprocessDao;

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
     * 查询分析统计记录
     * @param accessControlName
     * @return 单个分析统计记录
     */
    @Override
    public AddAccCtrlprocess selectAccCtrlByAccCtrlName(String accessControlName) {
        QueryWrapper<AddAccCtrlprocess> wrapper=new QueryWrapper<>();
        List<AddAccCtrlprocess> addAccCtrlprocesses = addAccCtrlprocessDao.selectList(wrapper.eq("access_control_name", accessControlName));
        AddAccCtrlprocess addAccCtrlprocess = addAccCtrlprocesses.get(0);
       // AddAccCtrlprocess  addAccCtrlprocess= addAccCtrlprocessDao.selectAccCtrlByAccCtrlName(accessControlName);
        return addAccCtrlprocess;
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
    public List<AddAccCtrlprocess> selectAddAccCtrlprocess(SimplePage page) {
        List<AddAccCtrlprocess> addAccCtrlprocesses=addAccCtrlprocessDao.selectAddAccCtrlprocessDesc();
        return addAccCtrlprocesses;
    }

    @Override
    public int updateAddAccCtrlprocess(String accessControlId,String accessControlName) {
       int i= addAccCtrlprocessDao.updateAddAccCtrlprocess(accessControlId,accessControlName);
        return i;
    }
}
