package com.summit.sdk.huawei.model;

public enum AlarmProcessType {

    Illegal_LOCK_ALARM(1,"非法开锁告警"),
    LowPower_Alarm(2,"低电量告警"),
    Drop_line_Alarm(3,"掉线告警");
    private int code;
    private String description;

    AlarmProcessType(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
    public static AlarmProcessType codeOf(int alarmProcessCode) {
        for (AlarmProcessType v : values()) {
            if (v.code == alarmProcessCode) {
                return v;
            }
        }
        return null;
    }
}
