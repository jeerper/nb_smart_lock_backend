package com.summit.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import java.util.Date;
import java.util.List;

@Data
public class LockInfo {
    @TableId(value = "lock_id", type = IdType.ID_WORKER_STR)
    private String lockId;
    @TableField(value = "lock_code")
    private String lockCode;
    @TableField(value = "status")
    private Integer status;
    @TableField(value = "updatetime")
    private Date updatetime;

    private List<CameraDevice> devices;

}