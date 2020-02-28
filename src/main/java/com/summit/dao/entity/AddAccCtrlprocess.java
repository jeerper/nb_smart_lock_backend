package com.summit.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Created by Administrator on 2019/9/16.
 */
@Data
@AllArgsConstructor
@ApiModel(value = "统计分析类",description = "封装统计分析的信息包括门禁的id,门禁的名称，门禁的开关锁状态，电池电量")
@TableName(value = "addup_acc_ctrl_process")
public class AddAccCtrlprocess {

    @ApiModelProperty(value = "记录id",name = "id")
    @TableId(value = "id", type = IdType.ID_WORKER_STR)
    private String id;

    @ApiModelProperty(value = "门禁id",name = "accessControlId")
    @TableField(value = "access_control_id")
    private String accessControlId;

    @ApiModelProperty(value = "门禁状态",name = "accessControlStatusCount")
    @TableField(value = "access_control_status_count")
    private Integer accessControlStatusCount;

    @ApiModelProperty(value = "电池电量",name = "batteryLeve")
    @TableField(value = "battery_leve")
    private Integer batteryLeve;

    @ApiModelProperty(value = "锁编码",name = "lockCode")
    @TableField(exist = false)
    private String lockCode;

    @ApiModelProperty(value = "门禁表的门禁名称",name = "accessControlName")
    @TableField(exist = false)
    private String accessControlName;

    @ApiModelProperty(value = "报警次数",name = "alarmCount")
    @TableField(value = "alarm_count")
    private Integer alarmCount;

    public AddAccCtrlprocess() {
    }
}
