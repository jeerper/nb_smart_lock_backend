package com.summit.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LockRealTimeInfo {

    private String lockId;
    private String lockCode;
    //1开锁，2锁定，3告警
    private Integer lockStatus;

    private String devId;
    private String deviceIp;
    //0正常，1异常
    private Integer deviceStatus;

    //最近锁操作记录对应的人脸信息
    private String faceId;
    private String userName;
    private String userId;
    private String name;
    private Integer gender;
    private String birthday;
    private String province;
    private String city;
    private Integer cardType;
    private String cardId;
    private Float faceMatchRate;
    private String faceLibName;
    private Integer faceLibType;
    private String facePanoramaUrl;
    private String facePicUrl;

    private String faceMatchUrl;
    private String picSnapshotTime;

    //当前锁告警数量
    private Integer alarmCount;

    public LockRealTimeInfo(){}


}
