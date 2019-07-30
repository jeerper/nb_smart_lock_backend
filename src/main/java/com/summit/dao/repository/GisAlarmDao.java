package com.summit.dao.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.summit.dao.entity.GisAlarm;

public interface GisAlarmDao  extends BaseMapper<GisAlarm> {

    GisAlarm selectById(String gisAlarmId);

    int deleteById(String gisAlarmId);

    int insert(GisAlarm record);

    int update(GisAlarm record);
}