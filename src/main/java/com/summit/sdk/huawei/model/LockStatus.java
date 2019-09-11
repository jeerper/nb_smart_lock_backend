package com.summit.sdk.huawei.model;

public enum LockStatus {

    UNLOCK(1,"开锁"),
    LOCK_CLOSED(2,"关锁"),
    LOCK_ALARM(3,"告警"),
    NOT_ONLINE (4,"不在线");

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
