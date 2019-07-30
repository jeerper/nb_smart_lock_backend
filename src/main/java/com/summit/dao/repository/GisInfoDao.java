package com.summit.dao.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.summit.dao.entity.GisInfo;

public interface GisInfoDao  extends BaseMapper<GisInfo> {

    GisInfo selectById(String gisId);

    int deleteById(String gisId);

    int insert(GisInfo record);

    int update(GisInfo record);
}