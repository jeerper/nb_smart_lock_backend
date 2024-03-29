package com.summit.sdk.huawei.model;

public enum LockProcessType {

    UNLOCK(1,"开锁"),
    CLOSE_LOCK(2,"关锁"),
    Not_Online(3,"不在线");

    private int code;
    private String description;

    LockProcessType(int code, String description){
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static LockProcessType codeOf(int lockProcessTypeCode) {
        for (LockProcessType v : values()) {
            if (v.code == lockProcessTypeCode) {
                return v;
            }
        }
        return null;
    }
}
