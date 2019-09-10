package com.summit.sdk.huawei.model;

import lombok.Data;

/**
 * Created by Administrator on 2019/9/5.
 */
@Data
public class FaceLib {
    /**
     * {
     "FaceListsArry": [{
     "ID": 3,
     "FaceListName": "人脸库1",
     "AlgVersion": "NFV_FR V1.5.0b027",
     "OnControl": 1,
     "Threshold": 90,
     "FaceListType": 2,
     "FeaStatus": 4
     }]
     }
     */
    private String ID;
    private String FaceListName;
    private String AlgVersion;
    private String OnControl;
    private String Threshold;
    private String FaceListType;
    private String FeaStatus;



}
