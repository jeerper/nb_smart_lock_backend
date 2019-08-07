package com.summit.sdk.huawei.model;

public enum AlarmStatus {
    PROCESSED(0,"已处理"),
    UNPROCESSED(1,"未处理");

    private int code;
    private String description;

    AlarmStatus(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public static AlarmStatus codeOf(int alarmStatusCode) {
        for (AlarmStatus v : values()) {
            if (v.code == alarmStatusCode) {
                return v;
            }
        }
        return null;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
