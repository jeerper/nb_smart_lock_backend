package com.summit.sdk.huawei.model;

/**
 * 摄像机上报类型
 * @author liuyuan
 */
public enum CameraUploadType {
    //开关锁状态
    Unlock("Unlock","开锁"),
    Lock("Lock","关锁"),
    Not_Online("Not_Online","不在线"),

    //告警状态
    Illegal_Alarm("Illegal_Alarm","非法开锁告警"),
    LowPower_Alarm("LowPower_Alarm","低电量告警"),
    Drop_line_Alarm("Drop_line_Alarm","掉线告警"),
    LowVoltage_Alarm("LowVoltage_Alarm","低电压告警"),
    Breakdown_Alarm("Breakdown_Alarm","故障告警"),

    //工作状态
    Realtime_Online("Realtime_Online","实时在线"),
    BusyHour_Online("BusyHour_Online","忙时在线"),
    LeisureTime_Online("LeisureTime_Online","闲时在线");

    private String code;
    private String description;

    CameraUploadType(String code,String description) {
        this.code = code;
        this.code = description;
    }

    public static CameraUploadType codeOf(String code) {
        for (CameraUploadType v : values()) {
            if (v.code.equals(code)) {
                return v;
            }
        }
        return null;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
