package com.summit.entity;

import lombok.Data;
@Data
public class SimFaceInfoAccCtl {
    /**
     * 授权进度
     */
    private Float faceAccCtrlProgress;
    /**
     * 人脸姓名
     */
    private  String userName;
    /**
     * 是否授权成功
     */
    private String isSuccessed;

    public SimFaceInfoAccCtl(Float faceAccCtrlProgress) {
        this.faceAccCtrlProgress = faceAccCtrlProgress;
    }

    public SimFaceInfoAccCtl(Float faceAccCtrlProgress, String userName, String isSuccessed) {
        this.faceAccCtrlProgress = faceAccCtrlProgress;
        this.userName = userName;
        this.isSuccessed = isSuccessed;
    }

    public SimFaceInfoAccCtl() {
    }
}
