package com.summit.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
@Data
@AllArgsConstructor
@TableName(value = "alarm")
@ApiModel(value="锁告警信息实体类", description="用来记录锁告警信息")
@JsonIgnoreProperties(ignoreUnknown = true)
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
    @ApiModelProperty(value="告警对应门禁信息 ",name="accCtrlProcess",hidden = true)
    @TableField(exist = false)
    private AccCtrlProcess accCtrlProcess;
    @ApiModelProperty(value="告警对应门禁id ",name="accessControlId")
    @TableField(exist = false)
    private String accessControlId;
    @ApiModelProperty(value="告警对应门禁名称 ",name="accessControlName")
    @TableField(exist = false)
    private String accessControlName;
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    @ApiModelProperty(value="告警发生时间 ",name="alarmTime")
    @TableField(value = "alarm_time")
    private Date alarmTime;
    @ApiModelProperty(value="告警状态(0：低电压告警，2低电量告警，1：非法开锁告警，3：掉线告警，4：故障告警，5：关锁超时报警",name="alarmStatus")
    @TableField(value = "alarm_status")
    private Integer alarmStatus;
    @ApiModelProperty(value="处理人",name="processPerson")
    @TableField(value = "process_person")
    private String processPerson;
    @ApiModelProperty(value="告警描述信息",name="alarmStatus")
    @TableField(value = "description")
    private String description;
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    @ApiModelProperty(value="告警最后更新时间，0已处理，1未处理",name="updatetime")
    @TableField(value = "updatetime")
    private Date updatetime;
    @ApiModelProperty(value="告警处理内容",name="processRemark")
    @TableField(value = "process_remark")
    private String processRemark;
    @ApiModelProperty(value="当前门禁状态",name="processRemark")
    @TableField(exist = false)
    private Integer accCtrlStatus;
    @ApiModelProperty(value="开锁结果",name="processResult")
    @TableField(exist = false)
    private Integer processResult;

    @ApiModelProperty(value="进出方式(0:进,1:出)",name="enterOrExit")
    @TableField(value = "enterOrExit")
    private Integer enterOrExit;

    @ApiModelProperty(value="报警处理状态(0已处理，1未处理)",name="alarmDealStatus")
    @TableField(value = "alarm_deal_status")
    private Integer alarmDealStatus;

    @ApiModelProperty(value = "部门名称",name = "deptName")
    @TableField(exist = false)
    private String deptNames;

    @ApiModelProperty(value = "人脸姓名",name = "faceName")
    @TableField(value = "face_name")
    private String faceName;

    @ApiModelProperty(value = "登录账号",name = "userName")
    @TableField(value = "user_name")
    private String userName;

    @ApiModelProperty(value="用户昵称",name="nickname")
    @TableField(exist = false)
    private String nickname;

    public Alarm(){}
}