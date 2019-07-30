package com.summit.dao.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.summit.dao.entity.VedioMonitorInfo;

public interface VedioMonitorInfoDao  extends BaseMapper<VedioMonitorInfo> {

    VedioMonitorInfo selectById(String id);

    int deleteById(String id);

    int insert(VedioMonitorInfo record);

    int update(VedioMonitorInfo record);
}