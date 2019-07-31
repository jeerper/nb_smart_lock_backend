package com.summit.service.impl;

import com.summit.dao.entity.SafeReport;
import com.summit.service.SafeReportService;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

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
    public SafeReport selectReportById(String safeRepId) {
        return null;
    }

    @Override
    public List<SafeReport> selectReportByLockCode(String lockCode, Date start, Date end) {
        return null;
    }

    @Override
    public List<SafeReport> selectReportByLockCode(String lockCode) {
        return null;
    }

    @Override
    public List<SafeReport> selectReportCondition(SafeReport safeReport, Date start, Date end) {
        return null;
    }

    @Override
    public List<SafeReport> selectReportCondition(SafeReport safeReport) {
        return null;
    }
}
