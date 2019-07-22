package com.summit.sdk.huawei.model;

public enum FaceLibType {
    FACE_LIB_DEFAULT(0,"未知"),
    FACE_LIB_BLACK(1,"黑名单"),
    FACE_LIB_WHITE(2,"白名单");



    private int faceLibTypeCode;
    private String faceLibTypeDescription;

    FaceLibType(int faceLibTypeCode, String faceLibTypeDescription) {
        this.faceLibTypeCode = faceLibTypeCode;
        this.faceLibTypeDescription = faceLibTypeDescription;
    }


    public static FaceLibType codeOf(int faceLibTypeCode) {
        for (FaceLibType v : values()) {
            if (v.faceLibTypeCode == faceLibTypeCode) {
                return v;
            }
        }
        return null;
    }


    public int getFaceLibTypeCode() {
        return faceLibTypeCode;
    }

    public String getFaceLibTypeDescription() {
        return faceLibTypeDescription;
    }
}
