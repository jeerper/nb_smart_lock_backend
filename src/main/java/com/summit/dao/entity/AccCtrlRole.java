package com.summit.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
@TableName(value = "role_accesscontrol_auth")
@ApiModel(value="角色门禁权限类", description="封角色的门禁权限信息")
public class AccCtrlRole {

    @ApiModelProperty(value="权限id",name="id")
    @TableId(value = "id", type = IdType.ID_WORKER_STR)
    private String id;


//    @TableField(value = "role_id")
    @ApiModelProperty(value="角色id",name="roleId")
    @TableField(exist = false)
    private String roleId;

//    @TableField(exist = false)
    @ApiModelProperty(value="角色code",name="roleCode")
    @TableField(value = "role_id")
    private String roleCode;

    @ApiModelProperty(value="门禁id",name="accessControlId")
    @TableField(value = "access_control_id")
    private String accessControlId;

    public AccCtrlRole(){}
}
