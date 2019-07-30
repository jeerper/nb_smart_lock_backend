package com.summit.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class SafeReport {
    @TableId(value = "safe_rep_id", type = IdType.ID_WORKER_STR)
    private String safeRepId;
    @TableField(value = "lock_code")
    private String lockCode;
    @TableField(value = "type")
    private String type;
    @TableField(value = "report_time")
    private Date reportTime;
    @TableField(value = "content")
    private String content;

    private List<SafeReportData> bojx;

}