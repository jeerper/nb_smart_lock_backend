package com.summit.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Created by Administrator on 2019/8/29.
 */
@Data
@AllArgsConstructor
@ApiModel(value = "简单的人脸信息列表",description ="包含人脸信息的id、id、人脸信息名称" )
public class SimpleFaceInfo {
    @ApiModelProperty(value = "人脸信息的id",name="faceid")
    private String faceid;
    @ApiModelProperty(value = "人脸信息名称",name = "userName")
    private String userName;
    @ApiModelProperty(value = "人脸图片",name = "faceImage")
    private String faceImage;
}
