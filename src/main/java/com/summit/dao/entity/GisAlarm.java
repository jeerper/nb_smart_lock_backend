package com.summit.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName(value = "gis_alarm")
public class GisAlarm {
    @TableId(value = "gis_alarm_id", type = IdType.ID_WORKER_STR)
    private String gisAlarmId;

}