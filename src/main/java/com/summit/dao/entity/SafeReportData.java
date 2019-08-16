package com.summit.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@AllArgsConstructor
@TableName(value = "safe_report_data")
public class SafeReportData {
    @TableId(value = "safe_rep_id", type = IdType.ID_WORKER_STR)
    private String id;
    @TableField(value = "safe_rep_id")
    private String safeRepId;
    @TableField(value = "version")
    private String version;
    @TableField(value = "vol")
    private double vol;
    @TableField(value = "jzdw")
    private String jzdw;
    @TableField(value = "latitude")
    private double latitude;
    @TableField(value = "longitude")
    private double longitude;
    @TableField(value = "altitude")
    private double altitude;
    @TableField(value = "speed")
    private double speed;
    @TableField(value = "direction")
    private Integer direction;

    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @TableField(value = "gps_time")
    private Date gpsTime;
    @TableField(value = "status_desc")
    private String statusDesc;

    public SafeReportData(){}
}