package com.summit.sdk.huawei.model;

public enum AccCtrlStatus {
    OPEN(1,"打开"),
    CLOSED(2,"锁定"),
    ALARM(3,"告警"),
    NOT_ONLINE (4,"不在线");

    private int code;
    private String description;

    AccCtrlStatus(int code, String description){
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static AccCtrlStatus codeOf(int accCtrlStatusCode) {
        for (AccCtrlStatus v : values()) {
            if (v.code == accCtrlStatusCode) {
                return v;
            }
        }
        return null;
    }
}
