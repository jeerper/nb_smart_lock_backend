package com.summit.service;

/**
 * Created by Administrator on 2019/9/16.
 */

import com.summit.dao.entity.AddAccCtrlprocess;

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
    List<AddAccCtrlprocess> selectAddAccCtrlprocess(List<String> accCtrlIds);
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
    /**
     * 根据统计分析记录表id删除统计分析字段
     * @return -1则没有删除成功
     */
    int  deladdAccCtrlprocessByAccCtrlId(List<String> needDeladdAccCtrlprocessIds);

    /**
     * 修改统计分析记录报警次数
     * @param accessControlId
     * @return
     */
    int updateAddAccProcessAlarmCount(String accessControlId);


    /**
     * 修改统计分析进出频次
     * @param accessControlId
     * @return
     */
    int insertOrUpdateEnterOrExitCount(String accessControlId);
}
