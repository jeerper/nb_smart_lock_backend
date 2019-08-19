package com.summit.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@AllArgsConstructor
@TableName(value = "lock_process")
public class LockProcess {
    @TableId(value = "process_id", type = IdType.ID_WORKER_STR)
    private String processId;
    @TableField(value = "device_ip")
    private String deviceIp;
    @TableField(value = "lock_id")
    private String lockId;
    @TableField(value = "lock_code")
    private String lockCode;

    @TableField(exist = false)
    private LockInfo lockInfo;

    @TableField(value = "user_id")
    private String userId;
    @TableField(value = "user_name")
    private String userName;
    @TableField(value = "gender")
    private Integer gender;
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @JsonFormat(pattern="yyyy-MM-dd")
    @TableField(value = "birthday")
    private Date birthday;
    @TableField(value = "province")
    private String province;
    @TableField(value = "city")
    private String city;
    @TableField(value = "card_type")
    private Integer cardType;
    @TableField(value = "card_id")
    private String cardId;
    @TableField(value = "face_match_rate")
    private Float faceMatchRate;
    @TableField(value = "face_lib_name")
    private String faceLibName;
    @TableField(value = "face_lib_type")
    private Integer faceLibType;

    //1：开锁，2：关锁
    @TableField(value = "process_type")
    private Integer processType;
    @TableField(value = "process_result")
    private String processResult;
    @TableField(value = "fail_reason")
    private String failReason;
    @TableField(value = "face_panorama_id")
    private String facePanoramaId;
    @TableField(value = "face_pic_id")
    private String facePicId;
    @TableField(value = "face_match_id")
    private String faceMatchId;
    @TableField(exist = false)
    private FileInfo facePanorama;
    @TableField(exist = false)
    private FileInfo facePic;
    @TableField(exist = false)
    private FileInfo faceMatch;

    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @TableField(value = "process_time")
    private Date processTime;

    public LockProcess(){}

}