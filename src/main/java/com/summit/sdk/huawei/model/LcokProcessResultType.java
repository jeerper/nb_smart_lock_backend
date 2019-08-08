package com.summit.sdk.huawei.model;

public enum LcokProcessResultType {
    SUCCESS("success","操作成功"),
    ERROR("error","操作失败");

    private String code;
    private String description;

    LcokProcessResultType(String code , String description){
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static LcokProcessResultType codeOf(String lockProcessResultTypeCode) {
        for (LcokProcessResultType v : values()) {
            if (v.code.equals(lockProcessResultTypeCode)) {
                return v;
            }
        }
        return null;
    }
}
