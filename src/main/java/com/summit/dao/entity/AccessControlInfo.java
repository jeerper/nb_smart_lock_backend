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
@JsonIgnoreProperties(value={"roles"},ignoreUnknown = true)
@TableName(value = "access_control")
@ApiModel(value="门禁信息类", description="封装门禁信息")
public class AccessControlInfo {
    @ApiModelProperty(value="门禁id,录入时不传，传了则忽略，仍用系统自动生成id。更新时需要传入",name="accessControlId")
    @TableId(value = "access_control_id", type = IdType.ID_WORKER_STR)
    private String accessControlId;
    @ApiModelProperty(value="门禁名称",name="accessControlName",required = true)
    @TableField(value = "access_control_name")
    private String accessControlName;
    @ApiModelProperty(hidden = true)
    @TableField(value = "lock_id")
    private String lockId;
    @ApiModelProperty(hidden = true)
    @TableField(value = "lock_code")
    private String lockCode;
    @ApiModelProperty(value="锁信息",name="lockInfo")
    @TableField(exist = false)
    private LockInfo lockInfo;
    @ApiModelProperty(hidden = true)
    @TableField(value = "entry_camera_id")
    private String entryCameraId;
    @ApiModelProperty(hidden = true)
    @TableField(value = "entry_camera_ip")
    private String entryCameraIp;
    @ApiModelProperty(value="入口摄像头信息",name="entryCamera")
    @TableField(exist = false)
    private CameraDevice entryCamera;
    @ApiModelProperty(hidden = true)
    @TableField(value = "exit_camera_id")
    private String exitCameraId;
    @ApiModelProperty(hidden = true)
    @TableField(value = "exit_camera_ip")
    private String exitCameraIp;
    @ApiModelProperty(value="出口摄像头信息",name="exitCamera")
    @TableField(exist = false)
    private CameraDevice exitCamera;
    //1开启，2锁定，3告警
    @ApiModelProperty(value="门禁状态,1开启，2锁定，3告警，录入时默认为2锁定",name="status",notes = "1开启，2锁定，3告警",required = true)
    @TableField(value = "status")
    private Integer status;
    @ApiModelProperty(value="创建人名称，取当前用户名称",name="createby",hidden = true)
    @TableField(value = "createby")
    private String createby;
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    @ApiModelProperty(hidden = true)
    @TableField(value = "createtime")
    private Date createtime;
//    @ApiModelProperty(value="门禁创建时间",name="createtimeStr",notes = "格式为yyyy-MM-dd HH:mm:ss")
//    @TableField(exist = false)
//    private String createtimeStr;
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    @ApiModelProperty(hidden = true)
    @TableField(value = "updatetime")
    private Date updatetime;
//    @ApiModelProperty(value="门禁更新时间",name="updatetimeStr",notes = "格式为yyyy-MM-dd HH:mm:ss")
//    @TableField(exist = false)
//    private String updatetimeStr;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @ApiModelProperty(hidden = true)
    @TableField(exist = false)
    private List<LockRole> roles;

    @ApiModelProperty(value="经度",name="longitude")
    @TableField(value = "longitude")
    private String longitude;
    @ApiModelProperty(value="纬度",name="latitude")
    @TableField(value = "latitude")
    private String latitude;


    @ApiModelProperty(value="工作状态(0：实时在线，1：忙时在线，2:闲时休眠)",name="workStatus")
    @TableField(value = "work_status")
    private Integer workStatus;

    @ApiModelProperty(value="告警状态(0：低电压告警，2低电量告警，1：非法开锁告警，3：掉线告警，4：故障告警，5：关锁超时报警)",name="alarmStatus")
    @TableField(value = "alarm_status")
    private Integer alarmStatus;

    @ApiModelProperty(value = "门禁所属部门集合", name = "depts")
    @TableField(exist = false)
    private String[] depts;

    @ApiModelProperty(value = "查询用--门禁所属部门集合", name = "deptIds",hidden = true)
    @TableField(exist = false)
    private String deptIds;

    @ApiModelProperty(value = "查询用--门禁所属部门名称，以,分割", name = "deptNames", hidden = true)
    @TableField(exist = false)
    private String deptNames;



    public AccessControlInfo(){}

    public AccessControlInfo(String accessControlName, String createby, String lockCode, String entryCameraIp, String exitCameraIp, Integer status){
        this.accessControlName = accessControlName;
        this.createby = createby;
        this.lockCode = lockCode;
        this.entryCameraIp = entryCameraIp;
        this.exitCameraIp = exitCameraIp;
        this.status = status;
    }

    public AccessControlInfo(String accessControlName,  String createby,String lockCode) {
        this.accessControlName = accessControlName;
        this.lockCode = lockCode;
        this.createby = createby;
    }
}
