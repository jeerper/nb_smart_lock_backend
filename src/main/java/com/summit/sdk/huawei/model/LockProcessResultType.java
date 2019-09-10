package com.summit.sdk.huawei.model;

public enum LockProcessResultType {

    CommandSuccess(1,"下发指令成功"),
    Exception(2,"异常"),
    NotOnline(4,"不在线"),
    NotResponse(5,"没有回复"),
    Success(6,"成功"),
    Failure(7,"失败"),
    MessageError(8,"消息错误"),
    NotSupportMessage(9,"不支持的消息");
    private int code;

    private String description;

    LockProcessResultType(int code , String description){
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static LockProcessResultType codeOf(int lockProcessResultTypeCode) {
        for (LockProcessResultType v : values()) {
            if (v.code==lockProcessResultTypeCode) {
                return v;
            }
        }
        return null;
    }
}
