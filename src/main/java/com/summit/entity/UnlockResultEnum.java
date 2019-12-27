package com.summit.entity;

/**
 * 锁操作结果枚举类
 */
public enum UnlockResultEnum {

    OperationSuccess(0,"操作成功"),
    OperationError(1,"操作失败"),
    ValidateError(2,"验证失败,拒绝"),
    TouchRope(3,"请压锁绳"),
    OperationAndUpdatePasswordSuccess(16,"操作成功并更新密码成功"),
    UpdatePasswordSuccess(17,"更新密码成功"),
    UpdatePasswordFailure(18,"更新密码失败");

    private int code;
    private String description;

     UnlockResultEnum(int code,String description){
         this.code=code;
         this.description=description;
    }


    public static UnlockResultEnum codeOf(int resultCode) {
        for (UnlockResultEnum v : values()) {
            if (v.code == resultCode) {
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
