package com.summit.service;

import com.summit.dao.entity.SimplePage;
import com.summit.dao.entity.SafeReport;

import java.util.Date;
import java.util.List;

/**
 * 平安报信息记录service接口
 */
public interface SafeReportService {

    /**
     * 平安报插入
     * @param safeReport 平安报信息对象
     * @return 不为-1则为成功
     */
    int insertSafeReport(SafeReport safeReport);

    /**
     * 平安报更新
     * @param safeReport 平安报信息对象
     * @return 不为-1则为成功
     */
    int updateSafeReport(SafeReport safeReport);

    /**
     * 平安报删除
     * @param safeReport 平安报信息对象
     * @return 不为-1则为成功
     */
    int delSafeReport(SafeReport safeReport);

    //###平安报查询-----------------------------

    /**
     * 查询所有平安报信息
     * @param page 分页对象
     * @return 平安报信息列表
     */
    List<SafeReport> selectAll(SimplePage page);

    /**
     * 根据Id查询平安报信息
     * @param safeRepId 平安报信息id
     * @return 唯一平安报信息对象
     */
    SafeReport selectReportById(String safeRepId);

    /**
     * 根据锁编号查询，可指定时间段
     * @param lockCode 锁编号
     * @param start 开始时间
     * @param end 截止时间
     * @param page 分页对象
     * @return 平安报信息列表
     */
    List<SafeReport> selectReportByLockCode(String lockCode, Date start, Date end, SimplePage page);

    /**
     * 根据锁编号查询，不带时间重载
     * @param lockCode 锁编号
     * @param page 分页对象
     * @return 平安报信息列表
     */
    List<SafeReport> selectReportByLockCode(String lockCode, SimplePage page);

    /**
     * 指定条件查询，可指定时间段
     * @param safeReport 平安报信息对象
     * @param start 开始时间
     * @param end 截止时间
     * @param page 分页对象
     * @return 平安报信息列表
     */
    List<SafeReport> selectReportCondition(SafeReport safeReport, Date start, Date end, SimplePage page);

    /**
     * 指定条件查询，不带日期的重载
     * @param safeReport 平安报信息对象
     * @param page 分页对象
     * @return 平安报信息列表
     */
    List<SafeReport> selectReportCondition(SafeReport safeReport, SimplePage page);

    /**
     * 根据锁编号查询当前用户具有权限的平安报
     * @param roles 角色列表
     * @param start 开始时间
     * @param end 截止时间
     * @param page 分页对象
     * @return 平安报信息列表
     */
    List<SafeReport> selectReportByRoles(List<String> roles, Date start, Date end, SimplePage page);

    /**
     * 根据锁编号查询当前用户具有权限的平安报，不带时间重载
     * @param roles 角色列表
     * @param page 分页对象
     * @return 平安报信息列表
     */
    List<SafeReport> selectReportByRoles(List<String> roles, SimplePage page);

}
