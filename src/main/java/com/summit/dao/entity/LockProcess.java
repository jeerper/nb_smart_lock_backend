package com.summit.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import java.util.Date;

@Data
public class LockProcess {
    @TableId(value = "process_id", type = IdType.ID_WORKER_STR)
    private String processId;
    @TableField(value = "device_ip")
    private String deviceIp;
    @TableField(value = "lock_code")
    private String lockCode;
    @TableField(value = "user_id")
    private String userId;
    @TableField(value = "user_name")
    private String userName;
    @TableField(value = "process_type")
    private Integer processType;
    @TableField(value = "process_result")
    private String processResult;
    @TableField(value = "fail_reason")
    private String failReason;
//    @TableField(value = "face_panorama_id")
//    private String facePanoramaId;
//    @TableField(value = "face_pic_id")
//    private String facePicId;
//    @TableField(value = "face_match_id")
//    private String faceMatchId;

    private FileInfo facePanorama;
    private FileInfo facePic;
    private FileInfo faceMatch;

    @TableField(value = "process_time")
    private Date processTime;

}