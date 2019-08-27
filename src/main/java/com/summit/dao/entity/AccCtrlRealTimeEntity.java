package com.summit.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
@TableName(value = "acc_crtl_realtime")
public class AccCtrlRealTimeEntity {

    @ApiModelProperty(value="门禁id",name="accessControlId")
    @TableId(value = "access_control_id", type = IdType.ID_WORKER_STR)
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
    @ApiModelProperty(value="门禁状态,1打开，2锁定，3告警",name="accCtrlStatus")
    @TableField(value = "acc_ctrl_status")
    private Integer accCtrlStatus;
    @ApiModelProperty(value="锁状态",name="lockStatus",notes = "1开锁，2锁定，3告警")
    @TableField(value = "lock_code")
    private Integer lock_status;
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
    @TableField(value = "face_id")
    private String faceId;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @ApiModelProperty(value="用户名",name="userName")
    @TableField(value = "user_name")
    private String userName;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @ApiModelProperty(value="用户id",name="userId")
    @TableField(value = "user_id")
    private String userId;
    @ApiModelProperty(value="姓名",name="name")
    @TableField(value = "name")
    private String name;
    @ApiModelProperty(value="性别。0男，1女，2未知",name="gender")
    @TableField(value = "gender")
    private Integer gender;
    @ApiModelProperty(value="生日",name="birthday")
    private String birthday;
    @TableField(value = "birthday")
    private Date birthdayDate;
    @ApiModelProperty(value="省份",name="province")
    @TableField(value = "province")
    private String province;
    @ApiModelProperty(value="城市",name="city")
    @TableField(value = "city")
    private String city;
    @ApiModelProperty(value="证件类型，0：身份证，1：护照，2：军官证，3：驾驶证，4：其他",name="cardType")
    @TableField(value = "card_type")
    private Integer cardType;
    @ApiModelProperty(value="证件id",name="cardId")
    @TableField(value = "card_id")
    private String cardId;
    @ApiModelProperty(value="匹配率",name="faceMatchRate")
    @TableField(value = "face_match_rate")
    private Float faceMatchRate;
    @ApiModelProperty(value="人脸库名称",name="faceLibName")
    @TableField(value = "face_lib_name")
    private String faceLibName;
    @ApiModelProperty(value="人脸库类型，0：未知，1：黑名单，2：白名单，3：报警",name="faceLibType")
    @TableField(value = "face_lib_type")
    private Integer faceLibType;

    @ApiModelProperty(value="全景图url",name="facePanoramaUrl")
    @TableField(value = "face_panorama_url")
    private String facePanoramaUrl;
    @ApiModelProperty(value="人脸识别抠图url",name="facePicUrl")
    @TableField(value = "face_pic_url")
    private String facePicUrl;
    @ApiModelProperty(value="锁编号",name="faceMatchUrl")
    @TableField(value = "face_match_url")
    private String faceMatchUrl;
    @ApiModelProperty(value="快照时间",name="picSnapshotTime")
    private String picSnapshotTime;
    @TableField(value = "pic_snapshot_time")
    private Date picSnapshotTimeDate;

    //当前锁告警数量
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @ApiModelProperty(value="锁当前告警数量",name="alarmCount")
    @TableField(exist = false)
    private Integer alarmCount;

    @ApiModelProperty(value="经度",name="longitude")
    @TableField(value = "longitude")
    private String longitude;
    @ApiModelProperty(value="纬度",name="latitude")
    @TableField(value = "latitude")
    private String latitude;
    @ApiModelProperty(value="告警id",name="alarmId")
    @TableField(value = "alarm_id")
    private String alarmId;
    @ApiModelProperty(value="当前操作记录id",name="accCtrlProId")
    @TableField(value = "acc_ctrl_pro_id")
    private String accCtrlProId;

    @ApiModelProperty(hidden = true)
    @TableField(value = "is_first")
    private Boolean isFirst;

    public AccCtrlRealTimeEntity(){}


}
