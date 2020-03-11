package com.summit.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@JsonIgnoreProperties(value={"devices","roles"},ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@TableName(value = "lock_info")
@ApiModel(value="锁信息类", description="封装锁信息")
public class LockInfo {
    @ApiModelProperty(value="锁id,录入时不传，传了则忽略，仍用系统自动生成id。更新时锁id和锁编号至少传入一个，两个都传则用id更新",name="lockId")
    @TableId(value = "lock_id", type = IdType.ID_WORKER_STR)
    private String lockId;
    @ApiModelProperty(value="锁编号",name="lockCode",required = true)
    @TableField(value = "lock_code")
    private String lockCode;

    //1开锁，2锁定，3告警
    @ApiModelProperty(value="锁状态,1开锁，2锁定，3告警，录入时默认为2锁定状态",name="status",notes = "1开锁，2锁定，3告警")
    @TableField(value = "status")
    private Integer status;


    @ApiModelProperty(value="锁创建人,取当前用户名称",name="createby",notes = "这里填name",hidden = true)
    @TableField(value = "createby")
    private String createby;

    @ApiModelProperty(value="当前密码",name="currentPassword",hidden = true)
    @TableField(value = "current_password")
    private String currentPassword;

    @ApiModelProperty(value="新密码",name="newPassword",hidden = true)
    @TableField(value = "new_password")
    private String newPassword;



    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    @ApiModelProperty(hidden = true)
    @TableField(value = "createtime")
    private Date createtime;

    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    @ApiModelProperty(hidden = true)
    @TableField(value = "updatetime")
    private Date updatetime;

    @ApiModelProperty(value="固定实时设置开始时间",name="fixedRealTimeStart")
    @TableField(value = "fixed_realTime_start")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date fixedRealTimeStart;


    @ApiModelProperty(value="固定实时设置结束时间",name="fixedRealTimeEnd")
    @TableField(value = "fixed_realTime_end")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date fixedRealTimeEnd;


    @ApiModelProperty(value="忙时是否在线设置(0:在线,1:不在线)",name="busyOnlineOrNot")
    @TableField(value = "busy_online_orNot")
    private int busyOnlineOrNot;

    @ApiModelProperty(value="低电压告警设置",name="lowVoltageAlarm")
    @TableField(value = "low_voltage_alarm")
    private int lowVoltageAlarm;

    @ApiModelProperty(value="低电量告警设置",name="lowPowerAlarm")
    @TableField(value = "low_power_alarm")
    private int lowPowerAlarm;


    @ApiModelProperty(value="非法开锁告警设置（0:合法，1：非法）",name="illegallUnlockingAlarm")
    @TableField(value = "illegall_unlocking_alarm")
    private int illegallUnlockingAlarm;

    @ApiModelProperty(value="闲时休眠设置",name="dormancySet")
    @TableField(value = "dormancy_set")
    private String dormancySet;

    @ApiModelProperty(value="掉线告警设置",name="dropLineAlarm")
    @TableField(value = "drop_line_alarm")
    private String dropLineAlarm;

    @ApiModelProperty(value="关锁超时设置",name="lockTimeoutSet")
    @TableField(value = "lock_timeout_set")
    private int lockTimeoutSet;

    @ApiModelProperty(value="锁关联的摄像头列表,为null则json中不返回此字段",name="devices",hidden = true)
    @TableField(exist = false)
    private List<CameraDevice> devices;

    @ApiModelProperty(value="锁关联的角色列表,此字段不返回前台",name="devices",hidden = true)
    @TableField(exist = false)
    private List<LockRole> roles;


    @ApiModelProperty(value="门禁名称",name="accessControlName",hidden = true)
    @TableField(exist = false)
    private String accessControlName;





    public LockInfo(){}

}