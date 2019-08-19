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
@TableName(value = "face_info")
public class FaceInfoEntity {
    @TableId(value = "face_id", type = IdType.ID_WORKER_STR)
    private String faceId;
    @TableField(value = "user_name")
    private String userName;
    @TableField(value = "user_id")
    private String userId;
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
//    @TableField(value = "face_panorama_id")
//    private String facePanoramaId;

    private FileInfo facePanorama;

//    @TableField(value = "face_pic_id")
//    private String facePicId;

    private FileInfo facePic;

//    @TableField(value = "face_match_id")
//    private String faceMatchId;

    private FileInfo faceMatch;

    @TableField(value = "device_ip")
    private String deviceIp;
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @TableField(value = "pic_snapshot_time")
    private Date picSnapshotTime;

    public FaceInfoEntity(){}
}