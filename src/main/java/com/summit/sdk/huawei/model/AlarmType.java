package com.summit.sdk.huawei.model;

public enum AlarmType {

   
    FACE_DETECTION(119, "人脸检测");

    private int alarmCode;
    private String alarmDescription;

    AlarmType(int alarmCode, String alarmDescription) {
        this.alarmCode = alarmCode;
        this.alarmDescription = alarmDescription;
    }

    public static AlarmType codeOf(int alarmCode) {
        for (AlarmType v : values()) {
            if (v.alarmCode == alarmCode) {
                return v;
            }
        }
        return null;
    }

    public int getAlarmCode() {
        return alarmCode;
    }

    public String getAlarmDescription() {
        return alarmDescription;
    }
}
