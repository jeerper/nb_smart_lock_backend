package com.summit.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName(value = "vedio_monitor_info")
public class VedioMonitorInfo {
    @TableId(value = "id", type = IdType.ID_WORKER_STR)
    private String id;
    @TableField(value = "device_ip")
    private String deviceIp;

}