package com.summit.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.summit.constants.CommonConstants;
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

    /**
     * 平安报插入
     * @param safeReport 平安报信息对象
     * @return 不为-1则为成功
     */
    @Override
    public int insertSafeReport(SafeReport safeReport) {
        if(safeReport == null){
            log.error("工况信息为空");
            return CommonConstants.UPDATE_ERROR;
        }
        return safeReportDao.insert(safeReport);
    }

    /**
     * 平安报更新
     * @param safeReport 平安报信息对象
     * @return 不为-1则为成功
     */
    @Override
    public int updateSafeReport(SafeReport safeReport) {
        if(safeReport == null){
            log.error("工况信息为空");
            return CommonConstants.UPDATE_ERROR;
        }
        UpdateWrapper<SafeReport> updateWrapper = new UpdateWrapper<>();
        return safeReportDao.update(safeReport,updateWrapper.eq("safe_rep_id",safeReport.getSafeRepId()));
    }

    /**
     * 平安报删除
     * @param safeReport 平安报信息对象
     * @return 不为-1则为成功
     */
    @Override
    public int delSafeReport(SafeReport safeReport) {
        if(safeReport == null){
            log.error("工况信息为空");
            return CommonConstants.UPDATE_ERROR;
        }
        UpdateWrapper<SafeReport> wrapper = new UpdateWrapper<>();
        return safeReportDao.delete(wrapper.eq("safe_rep_id",safeReport.getSafeRepId()));
    }

    /**
     * 查询所有平安报信息
     * @param page 分页对象
     * @return 平安报信息列表
     */
    @Override
    public List<SafeReport> selectAll(Page page) {
        PageConverter.convertPage(page);
        return safeReportDao.selectCondition(new SafeReport(),null,null,page);
    }

    /**
     * 根据Id查询平安报信息
     * @param safeRepId 平安报信息id
     * @return 唯一平安报信息对象
     */
    @Override
    public SafeReport selectReportById(String safeRepId) {
        if(safeRepId == null){
            log.error("工况id为空");
            return null;
        }
        return safeReportDao.selectSafeReportById(safeRepId);
    }

    /**
     * 根据锁编号查询，可指定时间段
     * @param lockCode 锁编号
     * @param start 开始时间
     * @param end 截止时间
     * @param page 分页对象
     * @return 平安报信息列表
     */
    @Override
    public List<SafeReport> selectReportByLockCode(String lockCode, Date start, Date end, Page page) {
        if(lockCode == null){
            log.error("锁编号为空");
            return null;
        }
        PageConverter.convertPage(page);
        return safeReportDao.selectByLockCode(lockCode,start,end,page);
    }

    /**
     * 根据锁编号查询，不带时间重载
     * @param lockCode 锁编号
     * @param page 分页对象
     * @return 平安报信息列表
     */
    @Override
    public List<SafeReport> selectReportByLockCode(String lockCode, Page page) {
        if(lockCode == null){
            log.error("锁编号为空");
            return null;
        }
        return selectReportByLockCode(lockCode,null,null,page);
    }

    /**
     * 指定条件查询，可指定时间段
     * @param safeReport 平安报信息对象
     * @param start 开始时间
     * @param end 截止时间
     * @param page 分页对象
     * @return 平安报信息列表
     */
    @Override
    public List<SafeReport> selectReportCondition(SafeReport safeReport, Date start, Date end, Page page) {
        if(safeReport == null){
            log.error("工况信息为空");
            return null;
        }
        PageConverter.convertPage(page);
        return safeReportDao.selectCondition(safeReport, start, end, page);
    }

    /**
     * 指定条件查询，不带日期的重载
     * @param safeReport 平安报信息对象
     * @param page 分页对象
     * @return 平安报信息列表
     */
    @Override
    public List<SafeReport> selectReportCondition(SafeReport safeReport, Page page) {
        if(safeReport == null){
            log.error("工况信息为空");
            return null;
        }
        return selectReportCondition(safeReport, null, null, page);
    }

    /**
     * 根据锁编号查询当前用户具有权限的平安报
     * @param roles 角色列表
     * @param start 开始时间
     * @param end 截止时间
     * @param page 分页对象
     * @return 平安报信息列表
     */
    @Override
    public List<SafeReport> selectReportByRoles(List<String> roles, Date start, Date end, Page page) {
        if(roles == null || roles.size() == 0){
            log.error("角色信息为空");
            return null;
        }
        PageConverter.convertPage(page);
        return safeReportDao.selectReportByRoles(roles, start, end, page);
    }

    /**
     * 根据锁编号查询当前用户具有权限的平安报，不带时间重载
     * @param roles 角色列表
     * @param page 分页对象
     * @return 平安报信息列表
     */
    @Override
    public List<SafeReport> selectReportByRoles(List<String> roles, Page page) {
        if(roles == null || roles.size() == 0){
            log.error("角色信息为空");
            return null;
        }
        return selectReportByRoles(roles,null,null,page);
    }
}
