package com.summit.service.impl;

import com.summit.dao.entity.Alarm;
import com.summit.dao.entity.Page;
import com.summit.dao.entity.SafeReport;
import com.summit.service.SafeReportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class SafeReportServiceImpl implements SafeReportService {
    @Override
    public int insertSafeReport(SafeReport safeReport) {
        return 0;
    }

    @Override
    public int updateSafeReport(SafeReport safeReport) {
        return 0;
    }

    @Override
    public int delSafeReport(SafeReport safeReport) {
        return 0;
    }

    @Override
    public List<SafeReport> selectAll(Page page) {
        return null;
    }

    @Override
    public SafeReport selectReportById(String safeRepId) {
        return null;
    }

    @Override
    public List<SafeReport> selectReportByLockCode(String lockCode, Date start, Date end, Page page) {
        return null;
    }

    @Override
    public List<SafeReport> selectReportByLockCode(String lockCode, Page page) {
        return null;
    }

    @Override
    public List<SafeReport> selectReportCondition(SafeReport safeReport, Date start, Date end, Page page) {
        return null;
    }

    @Override
    public List<SafeReport> selectReportCondition(SafeReport safeReport, Page page) {
        return null;
    }
}
