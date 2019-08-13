package com.summit.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@ApiModel(value="锁实时信息类", description="封装所需要的实时信息")

@JsonInclude(JsonInclude.Include.NON_NULL)
public class LockRealTimeInfo {

    @ApiModelProperty(value="门禁id",name="accessControlId")
    private String accessControlId;
    @ApiModelProperty(value="门禁名称",name="accessControlName")
    private String accessControlName;
    @ApiModelProperty(value="锁id",name="lockId")
    private String lockId;
    @ApiModelProperty(value="锁编号",name="lockCode")
    private String lockCode;
    //1打开，2锁定，3告警
    @ApiModelProperty(value="门禁状态",name="accCtrlStatus",notes = "1打开，2锁定，3告警")
    private Integer accCtrlStatus;
    //1开锁，2锁定，3告警
//    @ApiModelProperty(value="锁状态",name="lockStatus",notes = "1开锁，2锁定，3告警")
//    private Integer lockStatus;
//    @ApiModelProperty(value="摄像头id",name="devId")
//    private String devId;
    @ApiModelProperty(value="摄像头ip地址",name="deviceIp")
    private String deviceIp;
    //0正常，1异常
//    @ApiModelProperty(value="摄像头状态",name="deviceStatus",notes = "0正常，1异常")
//    private Integer deviceStatus;

    //最近锁操作记录对应的人脸信息
    @ApiModelProperty(value="对应人脸信息id",name="faceId")
    private String faceId;
    @ApiModelProperty(value="用户名",name="userName")
    private String userName;
    @ApiModelProperty(value="用户id",name="userId")
    private String userId;
    @ApiModelProperty(value="姓名",name="name")
    private String name;
    @ApiModelProperty(value="性别",name="gender")
    private Integer gender;
    @ApiModelProperty(value="生日",name="birthday")
    private String birthday;
    @ApiModelProperty(value="省份",name="province")
    private String province;
    @ApiModelProperty(value="城市",name="city")
    private String city;
    @ApiModelProperty(value="证件类型",name="cardType")
    private Integer cardType;
    @ApiModelProperty(value="证件id",name="cardId")
    private String cardId;
    @ApiModelProperty(value="匹配率",name="faceMatchRate")
    private Float faceMatchRate;
    @ApiModelProperty(value="人脸库名称",name="faceLibName")
    private String faceLibName;
    @ApiModelProperty(value="人脸库类型",name="faceLibType")
    private Integer faceLibType;
    @ApiModelProperty(value="全景图url",name="facePanoramaUrl")
    private String facePanoramaUrl;
    @ApiModelProperty(value="人脸识别抠图url",name="facePicUrl")
    private String facePicUrl;
    @ApiModelProperty(value="锁编号",name="faceMatchUrl")
    private String faceMatchUrl;
    @ApiModelProperty(value="快照时间",name="picSnapshotTime")
    private String picSnapshotTime;

    //当前锁告警数量
    @ApiModelProperty(value="锁当前告警数量",name="alarmCount")
    private Integer alarmCount;

    public LockRealTimeInfo(){}


}
