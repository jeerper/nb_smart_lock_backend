package com.summit.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Created by Administrator on 2019/8/29.
 */
@Data
@AllArgsConstructor
@TableName(value = "face_info_access_control")
@ApiModel(value = "人脸信息门禁权限类",description = "封装人脸门禁的权限信息")
public class FaceInfoAccCtrl {

    @ApiModelProperty(value = "权限id",name = "id")
    @TableId(value = "id",type= IdType.ID_WORKER_STR)
    private String id;

    @ApiModelProperty(value = "门禁id",name = "accessControlId")
    @TableField(value = "access_control_id")
    private String accessControlId;

    @ApiModelProperty(value = "人脸信息id",name = "faceid")
    @TableField(value = "face_id")
    private String faceid;

    @ApiModelProperty(value = "人脸录入状态(0：已录入，1：未录入)",name = "faceid")
    @TableField(value = "auth_status")
    private String authStatus;


    public FaceInfoAccCtrl() {
    }
}
