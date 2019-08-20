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
@ApiModel(value="门禁操作记录类", description="封装门禁操作记录信息")
@JsonIgnoreProperties(value={"panoramaPicId","facePanoramaId","facePicId","faceMatchId","panoramaPic"})
@AllArgsConstructor
@TableName(value = "acc_ctrl_process")
public class AccCtrlProcess {
    @ApiModelProperty(value="门禁操作记录id",name="accCtrlProId")
    @TableId(value = "acc_ctrl_pro_id", type = IdType.ID_WORKER_STR)
    private String accCtrlProId;
    @ApiModelProperty(value="门禁id",name="accessControlId")
    @TableField(value = "access_control_id")
    private String accessControlId;
    @ApiModelProperty(value="门禁名称",name="accessControlName")
    @TableField(value = "access_control_name")
    private String accessControlName;
    @ApiModelProperty(value="门禁操作记录对应摄像头id",name="deviceId")
    @TableField(value = "device_id")
    private String deviceId;
    @ApiModelProperty(value="门禁操作记录对应摄像头ip地址",name="deviceIp")
    @TableField(value = "device_ip")
    private String deviceIp;
    @ApiModelProperty(value="门禁操作记录对应摄像头类型",name="deviceType")
    @TableField(value = "device_type")
    private String deviceType;
    @ApiModelProperty(value="门禁操作记录对应锁id",name="lockId")
    @TableField(value = "lock_id")
    private String lockId;
    @ApiModelProperty(value="门禁操作记录对应锁编号",name="lockCode")
    @TableField(value = "lock_code")
    private String lockCode;

    @TableField(exist = false)
    private AccessControlInfo accessControlInfo;

    @ApiModelProperty(value="用户id",name="userId")
    @TableField(value = "user_id")
    private String userId;
    @ApiModelProperty(value="姓名",name="userName")
    @TableField(value = "user_name")
    private String userName;
    @ApiModelProperty(value="性别，0男，1女，2未知",name="gender")
    @TableField(value = "gender")
    private Integer gender;
    @ApiModelProperty(value="生日，格式为yyyy-MM-dd",name="birthday")
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
    @ApiModelProperty(value="证件类型，0：身份证，1：护照，2：军官证，3：驾驶证，4：其他",name="cardType")
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
    @ApiModelProperty(value="人脸库类型，0：未知，1：黑名单，2：白名单，3：报警",name="faceLibType")
    @TableField(value = "face_lib_type")
    private Integer faceLibType;

    //1：开锁，2：关锁
    @ApiModelProperty(value="门禁操作类型，1：开锁，2：关锁，3：告警",name="processType")
    @TableField(value = "process_type")
    private Integer processType;
    @ApiModelProperty(value="门禁操作结果，error：失败   success：成功",name="processResult")
    @TableField(value = "process_result")
    private String processResult;
    @ApiModelProperty(value="门禁操作失败原因，error：失败   success：成功",name="processResult")
    @TableField(value = "fail_reason")
    private String failReason;

    @ApiModelProperty(hidden = true)
    @TableField(value = "panorama_pic_id")
    private String panoramaPicId;
    @ApiModelProperty(hidden = true)
    @TableField(value = "face_panorama_id")
    private String facePanoramaId;
    @ApiModelProperty(hidden = true)
    @TableField(value = "face_pic_id")
    private String facePicId;
    @ApiModelProperty(hidden = true)
    @TableField(value = "face_match_id")
    private String faceMatchId;

    @ApiModelProperty(value="全景图片Url",name="panoramaPicUrl")
    @TableField(exist = false)
    private String panoramaPicUrl;
    @ApiModelProperty(value="人脸识别全景图Url",name="facePanoramaUrl")
    @TableField(exist = false)
    private String facePanoramaUrl;
    @ApiModelProperty(value="人脸识别抠图Url",name="facePicUrl")
    @TableField(exist = false)
    private String facePicUrl;
    @ApiModelProperty(value="人脸识别和人脸库中匹配的图片Url",name="faceMatchUrl")
    @TableField(exist = false)
    private String faceMatchUrl;


    @TableField(exist = false)
    private FileInfo panoramaPic = new FileInfo();
    @TableField(exist = false)
    private FileInfo facePanorama = new FileInfo();
    @TableField(exist = false)
    private FileInfo facePic = new FileInfo();
    @TableField(exist = false)
    private FileInfo faceMatch = new FileInfo();

    @ApiModelProperty(value="操作时间",name="facePicId")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    @TableField(value = "process_time")
    private Date processTime;

    public AccCtrlProcess(){}
}

