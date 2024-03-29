package com.summit.dao.repository;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.summit.dao.entity.AccCtrlRealTimeEntity;
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
     * @param page 简单分页对象
     * @param depts 当前部门门禁列表
     * @return 门禁实时信息list
     */
    List<AccCtrlRealTimeEntity> selectCondition(Page page, @Param("accCtrlRealTimeEntity") AccCtrlRealTimeEntity accCtrlRealTimeEntity,
                                                 @Param("depts") List<String> depts,
                                                @Param("userDepts") List<String> userDepts,
                                                @Param("start") Date start,
                                                @Param("end") Date end);

    /**
     * 根据门禁实时id查询最近更新时间
     * @param accCrtlRealTimeId 门禁实时id
     * @return 门禁实时信息最近更新时间
     */
    Date selectUpdatetimeById(@Param("accCrtlRealTimeId")  String accCrtlRealTimeId);

    /**
     * 根据门禁实时id查询快照时间
     * @param accCrtlRealTimeId 门禁实时id
     * @return 门禁实时信息快照时间
     */
    Date selectSnapshotTimeById(@Param("accCrtlRealTimeId")  String accCrtlRealTimeId);

    /**
     * 从所有门禁实时信息中查询最后更新时间
     * @return 所有门禁实时信息中的最后更新时间
     */
    Date selectLastUpdatetime();


    List<AccCtrlRealTimeEntity> selectConditionRefresh(Page page, @Param("accCtrlRealTimeEntity") AccCtrlRealTimeEntity accCtrlRealTimeEntity,
                                                       @Param("depts") List<String> depts,
                                                       @Param("start") Date start,
                                                       @Param("end") Date end);
}
