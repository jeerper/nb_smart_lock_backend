package com.summit.dao.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.summit.dao.entity.Alarm;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface AlarmDao  extends BaseMapper<Alarm> {

    Alarm selectAlarmById(@Param("alarmId") String alarmId, @Param("depts") List<String> depts);

    Alarm selectByAccCtrlProId(@Param("accCtrlProId") String accCtrlProId, @Param("roles") List<String> roles);

    List<Alarm> selectCondition(Page page,
                                @Param("alarm") Alarm alarm,
                                @Param("start") Date start,
                                @Param("end") Date end,
                                @Param("alarmStatus") Integer alarmStatus,
                                @Param("userDepts") List<String> userDepts,
                                @Param("depts") List<String> depts);

    Integer selectAlarmCountByStatus(@Param("alarmStatus") Integer alarmStatus, @Param("depts") List<String> depts);

    List<Alarm> selectAlarmsByAccCtrlProIds(@Param("accCtrlProIds") List<String> accCtrlProIds);

    Integer selectCountByCondition(@Param("alarm") Alarm alarm,
                                   @Param("start") Date start,
                                   @Param("end") Date end,
                                   @Param("roles") List<String> roles);
}