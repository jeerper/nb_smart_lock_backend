package com.summit.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.Date;

@Data
public class AwakenlockRecords {

    @TableId(value = "awak_id", type = IdType.ID_WORKER_STR)
    private String awakId;
    @TableField(value = "lock_code")
    private String lockCode;
    @TableField(value = "lock_code")
    private String userId;
    @TableField(value = "user_name")
    private String userName;
    @TableField(value = "awakenlock_result")
    private String awakenlockResult;
    @TableField(value = "fail_reason")
    private String failReason;
    @TableField(value = "awak_time")
    private Date awakTime;

}