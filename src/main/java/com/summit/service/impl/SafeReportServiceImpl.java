package com.summit.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.summit.dao.entity.Page;
import com.summit.dao.entity.SafeReport;
import com.summit.dao.repository.LockRoleDao;
import com.summit.dao.repository.SafeReportDao;
import com.summit.service.SafeReportService;
import com.summit.util.PageConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class SafeReportServiceImpl implements SafeReportService {

    @Autowired
    private LockRoleDao lockRoleDao;
    @Autowired
    private SafeReportDao safeReportDao;

    @Override
    public int insertSafeReport(SafeReport safeReport) {
        if(safeReport == null){
            log.error("工况信息为空");
            return -1;
        }
        return safeReportDao.insert(safeReport);
    }

    @Override
    public int updateSafeReport(SafeReport safeReport) {
        if(safeReport == null){
            log.error("工况信息为空");
            return -1;
        }
        UpdateWrapper<SafeReport> updateWrapper = new UpdateWrapper<>();
        return safeReportDao.update(safeReport,updateWrapper.eq("safe_rep_id",safeReport.getSafeRepId()));
    }

    @Override
    public int delSafeReport(SafeReport safeReport) {
        if(safeReport == null){
            log.error("工况信息为空");
            return -1;
        }
        UpdateWrapper<SafeReport> wrapper = new UpdateWrapper<>();
        return safeReportDao.delete(wrapper.eq("safe_rep_id",safeReport.getSafeRepId()));
    }

    @Override
    public List<SafeReport> selectAll(Page page) {
        PageConverter.convertPage(page);
        return safeReportDao.selectCondition(new SafeReport(),null,null,page);
    }

    @Override
    public SafeReport selectReportById(String safeRepId) {
        if(safeRepId == null){
            log.error("工况id为空");
            return null;
        }
        return safeReportDao.selectSafeReportById(safeRepId);
    }

    @Override
    public List<SafeReport> selectReportByLockCode(String lockCode, Date start, Date end, Page page) {
        if(lockCode == null){
            log.error("锁编号为空");
            return null;
        }
        PageConverter.convertPage(page);
        return safeReportDao.selectByLockCode(lockCode,start,end,page);
    }

    @Override
    public List<SafeReport> selectReportByLockCode(String lockCode, Page page) {
        if(lockCode == null){
            log.error("锁编号为空");
            return null;
        }
        return selectReportByLockCode(lockCode,null,null,page);
    }

    @Override
    public List<SafeReport> selectReportCondition(SafeReport safeReport, Date start, Date end, Page page) {
        if(safeReport == null){
            log.error("工况信息为空");
            return null;
        }
        PageConverter.convertPage(page);
        return safeReportDao.selectCondition(safeReport, start, end, page);
    }

    @Override
    public List<SafeReport> selectReportCondition(SafeReport safeReport, Page page) {
        if(safeReport == null){
            log.error("工况信息为空");
            return null;
        }
        return selectReportCondition(safeReport, null, null, page);
    }

    @Override
    public List<SafeReport> selectReportByRoles(List<String> roles, Date start, Date end, Page page) {
        if(roles == null || roles.size() == 0){
            log.error("角色信息为空");
            return null;
        }
        PageConverter.convertPage(page);
        return safeReportDao.selectReportByRoles(roles, start, end, page);
    }

    @Override
    public List<SafeReport> selectReportByRoles(List<String> roles, Page page) {
        if(roles == null || roles.size() == 0){
            log.error("角色信息为空");
            return null;
        }
        return selectReportByRoles(roles,null,null,page);
    }
}
