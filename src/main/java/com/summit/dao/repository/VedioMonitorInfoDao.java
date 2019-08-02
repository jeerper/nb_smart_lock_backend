package com.summit.dao.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.summit.dao.entity.VedioMonitorInfo;

public interface VedioMonitorInfoDao  extends BaseMapper<VedioMonitorInfo> {

    VedioMonitorInfo selectVedioMonitorById(String id);

}