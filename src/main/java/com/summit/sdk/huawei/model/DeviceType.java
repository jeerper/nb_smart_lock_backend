package com.summit.sdk.huawei.model;

public enum  DeviceType {
    ENTRY("entry","入口类型"),
    EXIT("exit","出口类型");

    private String code;
    private String description;

    DeviceType(String code , String description){
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static DeviceType codeOf(String deviceTypeCode) {
        for (DeviceType v : values()) {
            if (v.code.equals(deviceTypeCode)) {
                return v;
            }
        }
        return null;
    }
}
