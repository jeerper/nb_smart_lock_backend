package com.summit.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@TableName(value = "camera_device")
public class CameraDevice {
    @TableId(value = "dev_id", type = IdType.ID_WORKER_STR)
    private String devId;
    @TableField(value = "device_ip")
    private String deviceIp;
    @TableField(value = "lock_code")
    private String lockCode;

    //0正常，1异常
    @TableField(value = "status")
    private Integer status;

    public CameraDevice(){}
}