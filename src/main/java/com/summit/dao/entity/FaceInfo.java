package com.summit.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * Created by Administrator on 2019/8/21.
 */
@Data
@AllArgsConstructor//生成全参的构造函数
@ApiModel(value = "人脸信息类",description = "封装人脸信息")
@JsonIgnoreProperties(value = {"facepanorama","facepic","facematch"})
@TableName(value = "face_info")
public class FaceInfo {

    @ApiModelProperty(value = "人脸id",name = "faceid")
    @TableId(value = "face_id",type = IdType.ID_WORKER_STR)
    private String faceid;

    @ApiModelProperty(value = "人脸名",name = "faceName")
    @TableField(value = "face_name")
    private String faceName;

    @ApiModelProperty(value = "姓名",name = "userName")
    @TableField(value = "user_name")
    private String userName;

    @ApiModelProperty(value = "用户id",name = "userID")
    @TableField(value = "user_id")
    private String userID;

    @ApiModelProperty(value = "性别",name = "gender")
    @TableField(value = "gender")
    private Integer gender;

    @ApiModelProperty(value = "生日",name = "birthday")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern="yyyy-MM-dd", timezone="GMT+8")
    @TableField(value = "birthday")
    private Date birthday;

    @ApiModelProperty(value = "省份",name = "province")
    @TableField(value = "province")
    private  String province;

    @ApiModelProperty(value = "城市",name = "city")
    @TableField(value = "city")
    private String city;

    @ApiModelProperty(value = "证件类型",name = "cardType")
    @TableField(value = "card_type")
    private Integer cardType;

    @ApiModelProperty(value = "证件号",name = "cardID")
    @TableField(value = "card_id")
    private String cardId;

    @ApiModelProperty(value = "匹配率",name = "faceMatchrate")
    @TableField(value = "face_match_rate")
    private float faceMatchrate;

    @ApiModelProperty(value = "人脸库名称",name ="facelibname" )
    @TableField(value = "face_lib_name")
    private String facelibname;

    @ApiModelProperty(value = "人脸库类型",name = "facelibtype")
    @TableField(value = "face_lib_type")
    private Integer facelibtype;

   /* @ApiModelProperty(value = "人脸全景图",name = "facepanorama",hidden = true)
    private FileInfo facepanorama=new FileInfo() ;

    @ApiModelProperty(value = "人脸识别扣图",name = "facepic",hidden = true)
    private FileInfo facepic = new FileInfo();

    @ApiModelProperty(value = "人脸识别和人脸库中匹配的图片",name = "facematch",hidden = true)
    private FileInfo facematch=new FileInfo();
*/
    @ApiModelProperty(value = "摄像头ip地址",name ="deviceip")
    @TableField(value = "device_ip")
    private String deviceip;

    @ApiModelProperty(value = "抓拍时间",name = "picsnapshottime")
    @TableField(value = "pic_snapshot_time")
    private String picsnapshottime;

    @ApiModelProperty(value = "人脸库id",name = "facelibid")
    @TableField(value = "face_lib_id")
    private String facelibid;

    @ApiModelProperty(value = "人脸库图片",name = "faceImage")
    @TableField(value = "face_image")
    private String faceImage;

    @ApiModelProperty(value = "人脸库类型",name = "faceType")
    @TableField(value = "face_type")
    private Integer faceType;

    public FaceInfo() {
    }
}
