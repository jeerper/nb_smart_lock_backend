package com.summit.service;

import com.summit.cbb.utils.page.Page;
import com.summit.dao.entity.AccCtrlRealTimeEntity;
import com.summit.dao.entity.SimplePage;

import java.util.Date;
import java.util.List;


public interface AccCtrlRealTimeService {

    /**
     * 第一次是插入，后面都是更新
     * @param accCtrlRealTimeEntity 门禁实时信息对象
     * @return 返回-1则失败
     */
    int insertOrUpdate(AccCtrlRealTimeEntity accCtrlRealTimeEntity);

    /**
     * 门禁实时信息批量删除
     * @param accCrtlRealTimeIds 门禁实时信息id列表
     * @return 不为-1则成功
     */
    int delRealTimeInfoByIdBatch(List<String> accCrtlRealTimeIds);


    /**
     * 条件分页查询门禁实时信息
     * @param accCtrlRealTimeEntity 门禁实时信息对象
     * @param start 最近更新时间起始时间
     * @param end 最近更新时间截止时间
     * @param page 简单分页对象
     * @return 门禁实时信息分页
     */
    Page<AccCtrlRealTimeEntity> selectByConditionPage(AccCtrlRealTimeEntity accCtrlRealTimeEntity,
                                                Date start, Date end, SimplePage page);


    /**
     * 根据门禁实时id查询最近更新时间毫秒值
     * @param accCrtlRealTimeId 门禁实时id
     * @return 门禁实时信息最近更新时间毫秒值
     */
    Long selectUpdatetimeById(String accCrtlRealTimeId);


}
