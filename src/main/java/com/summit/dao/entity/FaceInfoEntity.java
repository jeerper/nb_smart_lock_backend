package com.summit.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@AllArgsConstructor
@ApiModel(value="人脸信息类", description="封装人脸信息")
@JsonIgnoreProperties(value={"facePanorama", "facePic", "faceMatch"})
@TableName(value = "face_info")
public class FaceInfoEntity {
    @ApiModelProperty(value="人脸id ",name="faceId")
    @TableId(value = "face_id", type = IdType.ID_WORKER_STR)
    private String faceId;
    @ApiModelProperty(value="姓名 ",name="userName")
    @TableField(value = "user_name")
    private String userName;
    @ApiModelProperty(value="用户id",name="userId")
    @TableField(value = "user_id")
    private String userId;
    @ApiModelProperty(value="性别",name="gender")
    @TableField(value = "gender")
    private Integer gender;
    @ApiModelProperty(value="生日",name="birthday")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @JsonFormat(pattern="yyyy-MM-dd")
    @TableField(value = "birthday")
    private Date birthday;
    @ApiModelProperty(value="省份",name="province")
    @TableField(value = "province")
    private String province;
    @ApiModelProperty(value="城市",name="city")
    @TableField(value = "city")
    private String city;
    @ApiModelProperty(value="证件类型",name="cardType")
    @TableField(value = "card_type")
    private Integer cardType;
    @ApiModelProperty(value="证件号",name="cardId")
    @TableField(value = "card_id")
    private String cardId;
    @ApiModelProperty(value="匹配率",name="faceMatchRate")
    @TableField(value = "face_match_rate")
    private Float faceMatchRate;
    @ApiModelProperty(value="人脸库名称",name="faceLibName")
    @TableField(value = "face_lib_name")
    private String faceLibName;
    @ApiModelProperty(value="人脸库类型",name="faceLibType")
    @TableField(value = "face_lib_type")
    private Integer faceLibType;
//    @TableField(value = "face_panorama_id")
//    private String facePanoramaId;

    @ApiModelProperty(value="人脸全景图",name="facePanorama",hidden = true)
    private FileInfo facePanorama = new FileInfo();

//    @TableField(value = "face_pic_id")
//    private String facePicId;

    @ApiModelProperty(value="人脸识别抠图",name="facePic",hidden = true)
    private FileInfo facePic = new FileInfo();

//    @TableField(value = "face_match_id")
//    private String faceMatchId;

    @ApiModelProperty(value="人脸识别和人脸库中匹配的图片",name="faceMatch",hidden = true)
    private FileInfo faceMatch = new FileInfo();

    @ApiModelProperty(value="摄像头ip地址",name="deviceIp")
    @TableField(value = "device_ip")
    private String deviceIp;

    @ApiModelProperty(value="快照时间",name="picSnapshotTime")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    @TableField(value = "pic_snapshot_time")
    private Date picSnapshotTime;

    public FaceInfoEntity(){}
}