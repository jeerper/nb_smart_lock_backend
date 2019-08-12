package com.summit.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@TableName(value = "access_control")
public class AccessControlInfo {

    @TableId(value = "access_control_id", type = IdType.ID_WORKER_STR)
    private String accessControlId;
    @TableField(value = "access_control_name")
    private String accessControlName;
    @TableField(value = "lock_id")
    private String lockId;
    @TableField(value = "lock_code")
    private String lockCode;
    @TableField(value = "entry_camera_id")
    private String entryCameraId;
    @TableField(value = "entry_camera_ip")
    private String entryCameraIp;
    @TableField(value = "exit_camera_id")
    private String exitCameraId;
    @TableField(value = "exit_camera_ip")
    private String exitCameraIp;

    //1开锁，2锁定，3告警
    @TableField(value = "status")
    private Integer status;
    @TableField(value = "createby")
    private String createby;
    @TableField(value = "createtime")
    private Date createtime;
    @TableField(value = "updatetime")
    private Date updatetime;

    @TableField(exist = false)
    private List<LockRole> roles;

    public AccessControlInfo(){}
}
