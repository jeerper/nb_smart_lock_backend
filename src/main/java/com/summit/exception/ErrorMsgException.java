package com.summit.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ErrorMsgException extends RuntimeException{

    private String errorMsg;

    public ErrorMsgException(){}
    public ErrorMsgException(String errorMsg){
        this.errorMsg = errorMsg;
    }

}
