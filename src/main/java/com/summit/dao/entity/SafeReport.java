package com.summit.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@TableName(value = "safe_report")
public class SafeReport {
    @TableId(value = "safe_rep_id", type = IdType.ID_WORKER_STR)
    private String safeRepId;
    @TableField(value = "lock_code")
    private String lockCode;
    @TableField(value = "type")
    private String type;
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @TableField(value = "report_time")
    private Date reportTime;
    @TableField(value = "content")
    private String content;

    private List<SafeReportData> bojx;

    public SafeReport(){}
}