package com.summit.sdk.huawei.model;

/**
 * 摄像机上报类型
 * @author liuyuan
 */
public enum CameraUploadType {
    //开锁
    Unlock("Unlock"),
    //报警
    Alarm("Alarm");

    private String code;

    CameraUploadType(String code) {
        this.code = code;
    }

    public static CameraUploadType codeOf(String code) {
        for (CameraUploadType v : values()) {
            if (v.code.equals(code)) {
                return v;
            }
        }
        return null;
    }

    public String getCode() {
        return code;
    }
}
