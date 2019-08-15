package com.summit.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;
@Data
@AllArgsConstructor
@TableName(value = "alarm")
@ApiModel(value="锁告警信息实体类", description="用来记录锁告警信息")
public class Alarm {
    @ApiModelProperty(value="告警id ",name="alarmId")
    @TableId(value = "alarm_id", type = IdType.ID_WORKER_STR)
    private String alarmId;
    @ApiModelProperty(value="告警名称 ",name="alarmName")
    @TableField(value = "alarm_name")
    private String alarmName;
    @ApiModelProperty(value="告警对应门禁操作id ",name="accCtrlProId")
    @TableField(value = "acc_ctrl_pro_id")
    private String accCtrlProId;
    //告警对应的门禁操作记录
    @TableField(exist = false)
    private AccCtrlProcess accCtrlProcess;
    @ApiModelProperty(value="告警对应门禁id ",name="accessControlId")
    @TableField(exist = false)
    private String accessControlId;
    @ApiModelProperty(value="告警对应门禁名称 ",name="accessControlName")
    @TableField(exist = false)
    private String accessControlName;
    @ApiModelProperty(value="告警发生时间 ",name="alarmTime")
    @TableField(value = "alarm_time")
    private Date alarmTime;
    @ApiModelProperty(value="告警状态，0已处理，1未处理",name="alarmStatus")
    @TableField(value = "alarm_status")
    private Integer alarmStatus;

    public Alarm(){}
}