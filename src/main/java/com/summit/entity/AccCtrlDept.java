package com.summit.entity;

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
@TableName(value = "dept_accesscontrol_auth")
@ApiModel(value="部门门禁权限类", description="封部门门禁权限信息")
public class AccCtrlDept {

    @ApiModelProperty(value="主键id",name="id")
    @TableId(value = "id", type = IdType.ID_WORKER_STR)
    private String id;

    @ApiModelProperty(value="部门id",name="deptId")
    @TableField(value = "dept_id")
    private String deptId;

    @ApiModelProperty(value="门禁id",name="accessControlId")
    @TableField(value = "access_control_id")
    private String accessControlId;


    public AccCtrlDept() {
    }
}
