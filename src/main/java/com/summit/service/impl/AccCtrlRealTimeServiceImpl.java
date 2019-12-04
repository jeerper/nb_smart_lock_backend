package com.summit.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.summit.cbb.utils.page.Pageable;
import com.summit.common.util.UserAuthUtils;
import com.summit.constants.CommonConstants;
import com.summit.dao.entity.AccCtrlRealTimeEntity;
import com.summit.dao.repository.AccCtrlRealTimeDao;
import com.summit.service.AccCtrlRealTimeService;
import com.summit.util.CommonUtil;
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
     *
     * @param accCtrlRealTimeEntity 门禁实时信息对象
     * @return 返回-1则失败
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    @Override
    public int insertOrUpdate(AccCtrlRealTimeEntity accCtrlRealTimeEntity) {
        if (accCtrlRealTimeEntity == null) {
            log.error("门禁实时信息对象为空");
            return CommonConstants.UPDATE_ERROR;
        }
        String accCrtlRealTimeId = accCtrlRealTimeEntity.getAccCrtlRealTimeId();
        if (accCrtlRealTimeId == null) {
            //插入
            return accCtrlRealTimeDao.insert(accCtrlRealTimeEntity);
        }
        //id不为空则是更新
        return accCtrlRealTimeDao.updateById(accCtrlRealTimeEntity);
    }

    /**
     * 门禁实时信息批量删除
     *
     * @param accCrtlRealTimeIds 门禁实时信息id列表
     * @return 不为-1则成功
     */
    @Override
    public int delRealTimeInfoByIdBatch(List<String> accCrtlRealTimeIds) {
        if (CommonUtil.isEmptyList(accCrtlRealTimeIds)) {
            log.error("门禁实时信息id为空");
            return CommonConstants.UPDATE_ERROR;
        }
        return accCtrlRealTimeDao.deleteBatchIds(accCrtlRealTimeIds);
    }

    /**
     * 条件分页查询门禁实时信息
     *
     * @param accCtrlRealTimeEntity 门禁实时信息对象
     * @param start                 最近更新时间起始时间
     * @param end                   最近更新时间截止时间
     * @return 门禁实时信息分页
     */
    @Override
    public com.summit.cbb.utils.page.Page<AccCtrlRealTimeEntity> selectByConditionPage(AccCtrlRealTimeEntity accCtrlRealTimeEntity, Date start,
                                                                                       Date end, Integer current, Integer pageSize) {
        List<String> roles = UserAuthUtils.getRoles();
        Page<AccCtrlRealTimeEntity> pageParam = null;
        if (current != null && pageSize != null) {
            pageParam = new Page<>(current, pageSize);
        }
        List<AccCtrlRealTimeEntity> realTimeEntities = accCtrlRealTimeDao.selectCondition(pageParam, accCtrlRealTimeEntity, roles);
        Pageable pageable = null;
        if (pageParam != null) {
            pageable = new Pageable((int) pageParam.getTotal(), (int) pageParam.getPages(), (int) pageParam.getCurrent(), (int) pageParam.getSize()
                    , realTimeEntities.size());
        }
        return new com.summit.cbb.utils.page.Page<>(realTimeEntities, pageable);
    }

    /**
     * 根据门禁实时id查询最近更新时间毫秒值
     *
     * @param accCrtlRealTimeId 门禁实时id
     * @return 门禁实时信息最近更新时间毫秒值
     */
    @Override
    public Long selectUpdatetimeById(String accCrtlRealTimeId) {
        if (CommonUtil.isEmptyStr(accCrtlRealTimeId)) {
            log.error("门禁实时信息id为空");
            return null;
        }
        Date date = accCtrlRealTimeDao.selectUpdatetimeById(accCrtlRealTimeId);
        if (date == null)
            return null;
        return date.getTime();
    }

    /**
     * 根据门禁实时id查询快照时间毫秒值
     *
     * @param accCrtlRealTimeId 门禁实时id
     * @return 门禁实时信息快照时间毫秒值
     */
    @Override
    public Long selectSnapshotTimeById(String accCrtlRealTimeId) {
        if (CommonUtil.isEmptyStr(accCrtlRealTimeId)) {
            log.error("门禁实时信息id为空");
            return null;
        }
        Date date = accCtrlRealTimeDao.selectSnapshotTimeById(accCrtlRealTimeId);
        if (date == null)
            return null;
        return date.getTime();
    }

    @Override
    public Long selectLastUpdatetime() {
        Date date = accCtrlRealTimeDao.selectLastUpdatetime();
        if (date == null)
            return null;
        return date.getTime();
    }

    /**
     * 根据门禁实时id查询实时记录
     * @param accessControlId 门禁id
     * @return 门禁实时记录
     */
    @Override
    public AccCtrlRealTimeEntity selectRealTimeInfoByAccCtrlId(String accessControlId) {
        QueryWrapper<AccCtrlRealTimeEntity> wrapper=new QueryWrapper<>();
        List<AccCtrlRealTimeEntity> accCtrlRealTimeEntities = accCtrlRealTimeDao.selectList(wrapper.eq("access_control_id", accessControlId));
        AccCtrlRealTimeEntity accCtrlRealTimeEntity = accCtrlRealTimeEntities.get(0);
        return accCtrlRealTimeEntity;
    }
}
