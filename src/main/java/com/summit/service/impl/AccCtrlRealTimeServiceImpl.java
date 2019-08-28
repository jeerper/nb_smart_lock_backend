package com.summit.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.summit.cbb.utils.page.Page;
import com.summit.cbb.utils.page.Pageable;
import com.summit.constants.CommonConstants;
import com.summit.dao.entity.AccCtrlRealTimeEntity;
import com.summit.dao.entity.SimplePage;
import com.summit.dao.repository.AccCtrlRealTimeDao;
import com.summit.service.AccCtrlRealTimeService;
import com.summit.util.CommonUtil;
import com.summit.util.LockAuthCtrl;
import com.summit.util.PageConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 门禁实时信息service实现
 */
@Slf4j
@Service
public class AccCtrlRealTimeServiceImpl implements AccCtrlRealTimeService {

    @Autowired
    private AccCtrlRealTimeDao accCtrlRealTimeDao;

    /**
     * 第一次是插入，后面都是更新
     * @param accCtrlRealTimeEntity 门禁实时信息对象
     * @return 返回-1则失败
     */
    @Transactional(propagation= Propagation.REQUIRED, rollbackFor = {Exception.class} )
    @Override
    public int insertOrUpdate(AccCtrlRealTimeEntity accCtrlRealTimeEntity) {
        if(accCtrlRealTimeEntity == null){
            log.error("门禁实时信息对象为空");
            return CommonConstants.UPDATE_ERROR;
        }
        String accCrtlRealTimeId = accCtrlRealTimeEntity.getAccCrtlRealTimeId();
        if(accCrtlRealTimeId == null){
            //插入
            return accCtrlRealTimeDao.insert(accCtrlRealTimeEntity);
        }
        //id不为空则是更新
        accCtrlRealTimeDao.deleteById(accCrtlRealTimeId);
        return accCtrlRealTimeDao.insert(accCtrlRealTimeEntity);
    }

    /**
     * 门禁实时信息批量删除
     * @param accCrtlRealTimeIds 门禁实时信息id列表
     * @return 不为-1则成功
     */
    @Override
    public int delRealTimeInfoByIdBatch(List<String> accCrtlRealTimeIds) {
        if(CommonUtil.isEmptyList(accCrtlRealTimeIds)){
            log.error("门禁实时信息id为空");
            return CommonConstants.UPDATE_ERROR;
        }
        return accCtrlRealTimeDao.deleteBatchIds(accCrtlRealTimeIds);
    }

    /**
     * 条件分页查询门禁实时信息
     * @param accCtrlRealTimeEntity 门禁实时信息对象
     * @param start 最近更新时间起始时间
     * @param end 最近更新时间截止时间
     * @param page 简单分页对象
     * @return 门禁实时信息分页
     */
    @Override
    public Page<AccCtrlRealTimeEntity> selectByConditionPage(AccCtrlRealTimeEntity accCtrlRealTimeEntity, Date start, Date end, SimplePage page) {
        if(accCtrlRealTimeEntity == null) {
            accCtrlRealTimeEntity = new AccCtrlRealTimeEntity();
        }
        List<String> roles = LockAuthCtrl.getRoles();
        List<AccCtrlRealTimeEntity> realTimeEntities = accCtrlRealTimeDao.selectCondition(accCtrlRealTimeEntity, start, end, null, roles);
        int rowsCount = realTimeEntities == null ? 0 : realTimeEntities.size();
        Pageable pageable = PageConverter.getPageable(page, rowsCount);
        PageConverter.convertPage(page);
        List<AccCtrlRealTimeEntity> accCtrlRealTimeEntities = accCtrlRealTimeDao.selectCondition(accCtrlRealTimeEntity, start, end, page, roles);
        Page<AccCtrlRealTimeEntity> backPage = new Page<>();
        backPage.setContent(accCtrlRealTimeEntities);
        backPage.setPageable(pageable);
        return backPage;
    }

    /**
     * 根据门禁实时id查询最近更新时间毫秒值
     * @param accCrtlRealTimeId 门禁实时id
     * @return 门禁实时信息最近更新时间毫秒值
     */
    @Override
    public Long selectUpdatetimeById(String accCrtlRealTimeId) {
        if(CommonUtil.isEmptyStr(accCrtlRealTimeId)){
            log.error("门禁实时信息id为空");
            return null;
        }
        Date date = accCtrlRealTimeDao.selectUpdatetimeById(accCrtlRealTimeId);
        if(date == null)
            return null;
        return date.getTime();
    }

    /**
     * 根据门禁实时id查询快照时间毫秒值
     * @param accCrtlRealTimeId 门禁实时id
     * @return 门禁实时信息快照时间毫秒值
     */
    @Override
    public Long selectSnapshotTimeById(String accCrtlRealTimeId) {
        if(CommonUtil.isEmptyStr(accCrtlRealTimeId)){
            log.error("门禁实时信息id为空");
            return null;
        }
        Date date = accCtrlRealTimeDao.selectSnapshotTimeById(accCrtlRealTimeId);
        if(date == null)
            return null;
        return date.getTime();
    }
}
