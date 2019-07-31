package com.summit.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@TableName(value = "user_lock_auth")
public class UserLockAuth {
    @TableId(value = "id", type = IdType.ID_WORKER_STR)
    private String id;
    @TableField(value = "user_id")
    private String userId;
    @TableField(value = "lock_id")
    private String lockId;

    public UserLockAuth(){}
}