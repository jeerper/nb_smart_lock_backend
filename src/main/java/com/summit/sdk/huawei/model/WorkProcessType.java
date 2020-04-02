package com.summit.sdk.huawei.model;

public enum WorkProcessType {
    //工作状态
    Realtime_Online(1,"实时在线"),
    BusyHour_Online(2,"忙时在线"),
    LeisureTime_Online(3,"闲时在线");
    private int code;
    private String description;

    WorkProcessType(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public static  WorkProcessType getWorkProcessType(int code){
        for (WorkProcessType w :values()){
            if (w.code==code){
                return w;
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
