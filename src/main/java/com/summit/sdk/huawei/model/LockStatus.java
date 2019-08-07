package com.summit.sdk.huawei.model;

public enum LockStatus {

    UNLOCK(1,"打开"),
    LOCK_CLOSED(2,"锁定"),
    LOCK_ALARM(3,"告警");

    private int code;
    private String description;

    LockStatus(int code, String description){
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static LockStatus codeOf(int lockStatusCode) {
        for (LockStatus v : values()) {
            if (v.code == lockStatusCode) {
                return v;
            }
        }
        return null;
    }
}
