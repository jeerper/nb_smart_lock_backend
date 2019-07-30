package com.summit.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.Date;
@Data
public class Alarm {
    @TableId(value = "alarm_id", type = IdType.ID_WORKER_STR)
    private String alarmId;
    @TableField(value = "alarm_name")
    private String alarmName;

    @TableField(value = "process_id")
    private String processId;

    //告警对应的锁操作记录
    private LockProcess lockProRecord;

    @TableField(value = "alarm_time")
    private Date alarmTime;
    @TableField(value = "alarm_status")
    private Integer alarmStatus;

}