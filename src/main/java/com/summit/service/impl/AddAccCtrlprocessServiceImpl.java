package com.summit.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.summit.dao.entity.AddAccCtrlprocess;
import com.summit.dao.entity.FaceInfo;
import com.summit.dao.repository.AddAccCtrlprocessDao;
import com.summit.service.AddAccCtrlprocessService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        AddAccCtrlprocess  addAccCtrlprocess= addAccCtrlprocessDao.selectAccCtrlByAccCtrlName(accessControlName);
        return addAccCtrlprocess;
    }

    @Override
    public int update(AddAccCtrlprocess addAccCtrlprocess) {
        UpdateWrapper<AddAccCtrlprocess> updateWrapper=new UpdateWrapper<>();
        int i = addAccCtrlprocessDao.update(addAccCtrlprocess, updateWrapper.eq("access_control_id", addAccCtrlprocess.getAccessControlId()));
        return i;
    }
}
