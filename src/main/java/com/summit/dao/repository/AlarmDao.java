package com.summit.dao.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.summit.dao.entity.Alarm;
import com.summit.dao.entity.Page;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface AlarmDao  extends BaseMapper<Alarm> {

    Alarm selectAlarmById(@Param("alarmId") String alarmId, @Param("roles") List<String> roles);

    Alarm selectByAccCtrlProId(@Param("accCtrlProId") String accCtrlProId, @Param("roles") List<String> roles);

    List<Alarm> selectCondition(@Param("alarm") Alarm alarm,
                                @Param("start") Date start,
                                @Param("end") Date end,
                                @Param("page") Page page,
                                @Param("roles") List<String> roles);

    Integer selectAlarmCountByStatus(@Param("alarmStatus") Integer alarmStatus, @Param("roles") List<String> roles);

}