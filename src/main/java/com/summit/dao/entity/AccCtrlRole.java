package com.summit.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
@TableName(value = "role_accesscontrol_auth")
public class AccCtrlRole {

    @TableId(value = "id", type = IdType.ID_WORKER_STR)
    private String id;

//    @TableField(value = "role_id")
    @TableField(exist = false)
    private String roleId;

//    @TableField(exist = false)
    @TableField(value = "role_id")
    private String roleCode;

    @TableField(value = "access_control_id")
    private String accessControlId;

    public AccCtrlRole(){}
}
