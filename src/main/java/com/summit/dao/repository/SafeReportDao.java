package com.summit.dao.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.summit.dao.entity.SafeReport;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface SafeReportDao  extends BaseMapper<SafeReport> {

    SafeReport selectById(String safeRepId);

    List<SafeReport> selectByLockCode(String lockCode);

    List<SafeReport> selectCondition(@Param("safeReport") SafeReport safeReport,
                                  @Param("start") Date start,
                                  @Param("end") Date end);

}