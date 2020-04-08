package com.summit.sdk.huawei.model;

public enum LockProcessMethod {

    FACE_RECOGNITION(1,"刷脸操作"),
    INTERFACE_BY(2,"接口操作"),
    Login_Operation(3,"APP登录操作");
    private int code;
    private String description;

    LockProcessMethod(int code, String description){
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static LockProcessMethod codeOf(int lockProcessMethodeCode) {
        for (LockProcessMethod v : values()) {
            if (v.code == lockProcessMethodeCode) {
                return v;
            }
        }
        return null;
    }
}
