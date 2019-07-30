package com.summit.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class UserLockAuth {
    @TableId(value = "id", type = IdType.ID_WORKER_STR)
    private String id;
    @TableField(value = "user_id")
    private String userId;
    @TableField(value = "lock_id")
    private String lockId;

}