package com.summit.dao.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.summit.dao.entity.Page;
import com.summit.dao.entity.SafeReport;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface SafeReportDao  extends BaseMapper<SafeReport> {

    SafeReport selectSafeReportById(String safeRepId);

    List<SafeReport> selectByLockCode(@Param("lockCode") String lockCode,
                                      @Param("start") Date start,
                                      @Param("end") Date end,
                                      @Param("page") Page page);

    List<SafeReport> selectCondition(@Param("safeReport") SafeReport safeReport,
                                  @Param("start") Date start,
                                  @Param("end") Date end,
                                  @Param("page") Page page);

    List<SafeReport> selectReportByRoles(@Param("roles") List<String> roles,
                                         @Param("start") Date start,
                                         @Param("end") Date end,
                                         @Param("page") Page page);

}