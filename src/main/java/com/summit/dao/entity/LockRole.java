package com.summit.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@TableName(value = "role_lock_auth")
public class LockRole {

    @TableId(value = "id", type = IdType.ID_WORKER_STR)
    private String id;

    @TableField(value = "role_id")
    private String roleId;

    @TableField(exist = false)
    private String roleCode;

    @TableField(value = "lock_id")
    private String lockId;

    @TableField(value = "lock_code")
    private String lockCode;

    public LockRole(){}

}
