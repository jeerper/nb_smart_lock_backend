package com.summit.entity;

import lombok.Data;

/**
 * Created by Administrator on 2019/8/26.
 */
@Data
public class FaceInfoManagerEntity {
    private String faceid;
    private String userName;
    private Integer gender;
    private String province;
    private String city;
    private String birthday;
    private Integer cardType;
    private String cardId;
    private String faceImage;
    private Integer faceType;
}
