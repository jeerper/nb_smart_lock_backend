package com.summit.dao.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.summit.dao.entity.AccCtrlRealTimeEntity;
import com.summit.dao.entity.SimplePage;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface AccCtrlRealTimeDao extends BaseMapper<AccCtrlRealTimeEntity> {

    /**
     * 根据门禁id查询门禁实时信息
     * @param accessControlId 门禁id
     * @param roles 当前用户角色列表
     * @return 门禁实时信息
     */
    AccCtrlRealTimeEntity selectRealTimeInfoByAccCtrlId(@Param("accessControlId")  String accessControlId, @Param("roles") List<String> roles);

    /**
     * 条件分页查询门禁实时信息，可指定时间段
     * @param accCtrlRealTimeEntity
     * @param start 最近更新时间起始时间
     * @param end 最近更新时间截止时间
     * @param page 简单分页对象
     * @param roles 当前用户管理角色列表
     * @return
     */
    List<AccCtrlRealTimeEntity> selectCondition(@Param("accCtrlRealTimeEntity") AccCtrlRealTimeEntity accCtrlRealTimeEntity,
                                         @Param("start") Date start,
                                         @Param("end") Date end,
                                         @Param("page") SimplePage page,
                                         @Param("roles") List<String> roles);

    /**
     * 第一次是插入，后面都是更新
     * @param accCtrlRealTimeEntity 门禁实时信息对象
     * @return 返回-1则失败
     */
    int insertOrUpdate(AccCtrlRealTimeEntity accCtrlRealTimeEntity);
}
