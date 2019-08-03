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
@TableName(value = "lock_info")
public class LockInfo {
    @TableId(value = "lock_id", type = IdType.ID_WORKER_STR)
    private String lockId;
    @TableField(value = "lock_code")
    private String lockCode;

    //1开锁，2锁定，3告警
    @TableField(value = "status")
    private Integer status;
    @TableField(value = "createby")
    private String createby;
    @TableField(value = "updatetime")
    private Date updatetime;

    @TableField(exist = false)
    private List<CameraDevice> devices;

    @TableField(exist = false)
    private List<LockRole> roles;

    public LockInfo(){}

}