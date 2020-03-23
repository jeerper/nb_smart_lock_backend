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
@TableName(value = "dept_face_auth")
@ApiModel(value="部门人脸关联类", description="部门人脸关联类信息")
public class FaceDept {

    @ApiModelProperty(value="主键id",name="Id")
    @TableId(value = "Id", type = IdType.ID_WORKER_STR)
    private String Id;

    @ApiModelProperty(value="部门id",name="deptId")
    @TableField(value = "dept_id")
    private String deptId;


    @ApiModelProperty(value="人脸id",name="faceId")
    @TableField(value = "face_id")
    private String faceId;

    public FaceDept() {
    }


}
