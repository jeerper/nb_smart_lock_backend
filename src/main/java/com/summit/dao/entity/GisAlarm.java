package com.summit.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class GisAlarm {
    @TableId(value = "gis_alarm_id", type = IdType.ID_WORKER_STR)
    private String gisAlarmId;

}