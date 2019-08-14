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
import java.util.List;

@Data
@AllArgsConstructor
@TableName(value = "access_control")
@ApiModel(value="门禁信息类", description="封装门禁信息")
public class AccessControlInfo {
    @ApiModelProperty(value="门禁id",name="accessControlId")
    @TableId(value = "access_control_id", type = IdType.ID_WORKER_STR)
    private String accessControlId;
    @ApiModelProperty(value="门禁名称",name="accessControlName")
    @TableField(value = "access_control_name")
    private String accessControlName;

    @TableField(value = "lock_id")
    private String lockId;
    @TableField(value = "lock_code")
    private String lockCode;
    @ApiModelProperty(value="锁信息",name="lockInfo")
    @TableField(exist = false)
    private LockInfo lockInfo;
    @TableField(value = "entry_camera_id")
    private String entryCameraId;
    @TableField(value = "entry_camera_ip")
    private String entryCameraIp;
    @ApiModelProperty(value="入口摄像头信息",name="entryCamera")
    @TableField(exist = false)
    private CameraDevice entryCamera;
    @TableField(value = "exit_camera_id")
    private String exitCameraId;
    @TableField(value = "exit_camera_ip")
    private String exitCameraIp;
    @ApiModelProperty(value="出口摄像头信息",name="exitCamera")
    @TableField(exist = false)
    private CameraDevice exitCamera;

    //1开启，2锁定，3告警
    @ApiModelProperty(value="门禁状态",name="status",notes = "1开启，2锁定，3告警")
    @TableField(value = "status")
    private Integer status;
    @ApiModelProperty(value="创建人名称",name="createby")
    @TableField(value = "createby")
    private String createby;
    @ApiModelProperty(value="创建时间",name="createtime")
    @TableField(value = "createtime")
    private Date createtime;
    @ApiModelProperty(value="更新时间",name="updatetime")
    @TableField(value = "updatetime")
    private Date updatetime;

    @TableField(exist = false)
    private List<LockRole> roles;

    public AccessControlInfo(){}
}
