package com.summit.service;

/**
 * Created by Administrator on 2019/9/16.
 */

import com.summit.dao.entity.AddAccCtrlprocess;
import com.summit.dao.entity.SimplePage;

import java.util.List;

/**
 * 统计分析接口
 */
public interface AddAccCtrlprocessService {
    /**
     * 分析统计记录插入
     * @param addAccCtrlprocess
     * @return -1则为不成功
     */
    int insert(AddAccCtrlprocess addAccCtrlprocess);

    /**
     * 修改统计分析记录
     * @param addAccCtrlprocess
     * @return -1则为不成功
     */
    int update(AddAccCtrlprocess addAccCtrlprocess);

    /**
     * 根据门禁的id查询当前的统计分析记录
     * @param accCtrlProId
     * @return
     */
    List<AddAccCtrlprocess> selectAddAccCtrlprocessByAccCtrlProId(String accCtrlProId);

    /**
     * 查询所有的统计分析记录
     * @return 所有的统计分析记录
     */
    List<AddAccCtrlprocess> selectAddAccCtrlprocess(SimplePage page);
    /**
     * 根据门禁id修改统计记录表
     * @return 所有的统计分析记录
     */
    int updateAddAccCtrlprocess(String accessControlId);
    /**
     * 根据门禁id查询
     * @return 所有的统计分析记录
     */
    AddAccCtrlprocess selectAddAccCtrlByAccCtrlID(String accessControlId);
}
