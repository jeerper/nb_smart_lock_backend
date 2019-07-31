package com.summit.service;

import com.summit.dao.entity.Alarm;
import com.summit.dao.entity.Page;
import com.summit.dao.entity.SafeReport;

import java.util.Date;
import java.util.List;

public interface SafeReportService {

    //###平安报插入
    int insertSafeReport(SafeReport safeReport);

    //###平安报更新
    int updateSafeReport(SafeReport safeReport);

    //###平安报删除
    int delSafeReport(SafeReport safeReport);

    //###平安报查询
    //查询所有
    List<SafeReport> selectAll(Page page);
    //根据Id查询
    SafeReport selectReportById(String safeRepId);
    //根据锁编号查询，可指定时间段
    List<SafeReport> selectReportByLockCode(String lockCode, Date start, Date end, Page page);
    //不带时间重载
    List<SafeReport> selectReportByLockCode(String lockCode, Page page);
    //指定条件查询，可指定时间段
    List<SafeReport> selectReportCondition(SafeReport safeReport, Date start, Date end, Page page);
    //指定条件查询，不带日期的重载
    List<SafeReport> selectReportCondition(SafeReport safeReport, Page page);
}
