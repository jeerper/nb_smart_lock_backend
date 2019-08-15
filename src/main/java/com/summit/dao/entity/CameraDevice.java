package com.summit.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@TableName(value = "camera_device")
@ApiModel(value="摄像头信息类", description="封装摄像头信息")
public class CameraDevice {
    @ApiModelProperty(value="摄像头id",name="devId")
    @TableId(value = "dev_id", type = IdType.ID_WORKER_STR)
    private String devId;
    @ApiModelProperty(value="摄像头ip地址",name="deviceIp",required = true)
    @TableField(value = "device_ip")
    private String deviceIp;
    @ApiModelProperty(hidden = true)
    @TableField(value = "lock_id")
    private String lockId;
    @ApiModelProperty(hidden = true)
    @TableField(value = "lock_code")
    private String lockCode;

    //0正常，1异常
    @ApiModelProperty(value="摄像头状态,0正常，1异常，不传默认为0",name="deviceIp",notes = "0正常，1异常，不传默认为0")
    @TableField(value = "status")
    private Integer status = 0;

    public CameraDevice(){}
}