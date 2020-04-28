package com.summit.sdk.huawei.model;

public enum AlarmProcessType {

    Illegal_LOCK_ALARM(1,"非法开锁告警"),
    LowPower_Alarm(2,"低电量告警"),
    Drop_line_Alarm(3,"掉线告警"),
    LowVoltage_Alarm(0,"低电压告警"),
    Breakdown_Alarm(4,"故障告警"),
    Lock_Closing_Timeout (5,"关锁超时报警"),
    No_Alarm (6,"无报警(不入库)");
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
