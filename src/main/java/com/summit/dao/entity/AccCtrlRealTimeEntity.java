package com.summit.dao.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
@TableName(value = "acc_crtl_realtime")
public class AccCtrlRealTimeEntity {

    @ApiModelProperty(value="门禁实时信息id",name="accCrtlRealTimeId")
    @TableId(value = "acc_crtl_realtime_id", type = IdType.ID_WORKER_STR)
    private String accCrtlRealTimeId;
    @ApiModelProperty(value="门禁id",name="accessControlId")
    @TableField(value = "access_control_id")
    private String accessControlId;
    @ApiModelProperty(value="门禁名称",name="accessControlName")
    @TableField(value = "access_control_name")
    private String accessControlName;
    @ApiModelProperty(value="锁id",name="lockId")
    @TableField(value = "lock_id")
    private String lockId;
    @ApiModelProperty(value="锁编号",name="lockCode")
    @TableField(value = "lock_code")
    private String lockCode;
    @ApiModelProperty(value="开锁状态，1：开锁，2：关锁，3:不在线",name="accCtrlStatus")
    @TableField(value = "acc_ctrl_status")
    private Integer accCtrlStatus;
    @ApiModelProperty(value="锁状态",name="lockStatus",notes = "1开锁，2锁定，3告警")
    @TableField(value = "lock_status")
    private Integer lockStatus;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @ApiModelProperty(value="摄像头id",name="devId")
    @TableField(value = "dev_id")
    private String devId;
    @ApiModelProperty(value="摄像头ip地址",name="deviceIp")
    @TableField(value = "device_ip")
    private String deviceIp;
    @ApiModelProperty(value="摄像头类型",name="deviceType")
    @TableField(value = "device_type")
    private String deviceType;
    //0正常，1异常
//    @ApiModelProperty(value="摄像头状态",name="deviceStatus",notes = "0正常，1异常")
//    private Integer deviceStatus;

    //最近锁操作记录对应的人脸信息
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @ApiModelProperty(value="对应人脸信息id",name="faceId")
    @TableField(value = "face_id",updateStrategy= FieldStrategy.IGNORED)
    private String faceId;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @ApiModelProperty(value="用户id",name="userId")
    @TableField(value = "user_id")
    private String userId;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @ApiModelProperty(value="人脸名",name="userName")
    @TableField(value = "user_name")
    private String userName;
    @ApiModelProperty(value="开锁人姓名",name="name")
    @TableField(value = "name",updateStrategy= FieldStrategy.IGNORED)
    private String name;
    @ApiModelProperty(value="性别。0男，1女，2未知",name="gender")
    @TableField(value = "gender",updateStrategy= FieldStrategy.IGNORED)
    private Integer gender;
    @JsonFormat(pattern="yyyy-MM-dd", timezone="GMT+8")
    @ApiModelProperty(value="生日",name="birthday")
    @TableField(value = "birthday",updateStrategy= FieldStrategy.IGNORED)
    private Date birthday;

    @ApiModelProperty(value="省份",name="province")
    @TableField(value = "province",updateStrategy= FieldStrategy.IGNORED)
    private String province;
    @ApiModelProperty(value="城市",name="city")
    @TableField(value = "city",updateStrategy= FieldStrategy.IGNORED)
    private String city;
    @ApiModelProperty(value="证件类型，0：身份证，1：护照，2：军官证，3：驾驶证，4：其他",name="cardType")
    @TableField(value = "card_type",updateStrategy= FieldStrategy.IGNORED)
    private Integer cardType;
    @ApiModelProperty(value="证件id",name="cardId")
    @TableField(value = "card_id",updateStrategy= FieldStrategy.IGNORED)
    private String cardId;
    @ApiModelProperty(value="匹配率",name="faceMatchRate")
    @TableField(value = "face_match_rate")
    private Float faceMatchRate;
    @ApiModelProperty(value="人脸库名称",name="faceLibName")
    @TableField(value = "face_lib_name",updateStrategy= FieldStrategy.IGNORED)
    private String faceLibName;
    @ApiModelProperty(value="人脸库类型，0：未知，1：黑名单，2：白名单，3：报警",name="faceLibType")
    @TableField(value = "face_lib_type",updateStrategy= FieldStrategy.IGNORED)
    private Integer faceLibType;

    @ApiModelProperty(value="全景图url",name="facePanoramaUrl")
    @TableField(value = "face_panorama_url")
    private String facePanoramaUrl;
    @ApiModelProperty(value="人脸识别抠图url",name="facePicUrl")
    @TableField(value = "face_pic_url")
    private String facePicUrl;
    @ApiModelProperty(value="人脸库匹配图url",name="faceMatchUrl")
    @TableField(value = "face_match_url",updateStrategy= FieldStrategy.IGNORED)
    private String faceMatchUrl;

    @ApiModelProperty(value="快照时间",name="picSnapshotTime")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    @TableField(value = "pic_snapshot_time")
    private Date picSnapshotTime;

    //当前锁告警数量
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @ApiModelProperty(value="门禁当前告警数量",name="alarmCount")
    @TableField(exist = false)
    private Integer alarmCount;

    @ApiModelProperty(value="经度",name="longitude")
    @TableField(value = "longitude",updateStrategy= FieldStrategy.IGNORED)
    private String longitude;
    @ApiModelProperty(value="纬度",name="latitude")
    @TableField(value = "latitude",updateStrategy= FieldStrategy.IGNORED)
    private String latitude;
    @ApiModelProperty(value="告警id",name="alarmId")
    @TableField(value = "alarm_id")
    private String alarmId;
    @ApiModelProperty(value="当前操作记录id",name="accCtrlProId")
    @TableField(value = "acc_ctrl_pro_id")
    private String accCtrlProId;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    @ApiModelProperty(value="最后更新时间",name="updatetime")
    @TableField(value = "updatetime")
    private Date updatetime;

    @ApiModelProperty(value="工作状态(0：实时在线，1：忙时在线，2:闲时休眠)",name="workStatus")
    @TableField(value = "work_status")
    private Integer workStatus;

    @ApiModelProperty(value="告警状态(0：低电压告警，1：非法开锁告警，2低电量告警，3：掉线告警，4：故障告警，5：关锁超时报警)",name="alarmStatus")
    @TableField(value = "alarm_status")
    private Integer alarmStatus;

   /* @ApiModelProperty(value="告警状态(0：低电压告警，1：非法开锁告警，2低电量告警，3：掉线告警，4：故障告警，5：关锁超时报警)",name="alarmStatusName")
    @TableField(exist = false)
    private String alarmStatusName;*/

    @ApiModelProperty(value="进出方式(0:进,1:出)",name="enterOrExit")
    @TableField(value = "enterOrExit")
    private Integer enterOrExit;

    @ApiModelProperty(value = "部门名称",name = "deptName")
    @TableField(exist = false)
    private String deptNames;

    @ApiModelProperty(value = "电池电量",name = "batteryLeve")
    @TableField(exist = false)
    private Integer batteryLeve;


    public AccCtrlRealTimeEntity(){}


}
