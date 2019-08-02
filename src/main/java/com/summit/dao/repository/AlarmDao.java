package com.summit.dao.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.summit.dao.entity.Alarm;
import com.summit.dao.entity.Page;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface AlarmDao  extends BaseMapper<Alarm> {

    Alarm selectAlarmById(String alarmId);

    Alarm selectByProcessId(String processId);

    List<Alarm> selectCondition(@Param("alarm") Alarm alarm,
                                  @Param("start") Date start,
                                  @Param("end") Date end,
                                @Param("page") Page page);
}