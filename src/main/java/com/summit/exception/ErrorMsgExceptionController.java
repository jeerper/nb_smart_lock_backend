package com.summit.exception;

import com.summit.common.entity.ResponseCodeEnum;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class ErrorMsgExceptionController {
    @ResponseBody
    @ExceptionHandler(value = ErrorMsgException.class)
    public Map<String,Object> myExceptionHandler(ErrorMsgException exception){
        Map<String,Object> map  = new HashMap<String,Object>();
        map.put("code", ResponseCodeEnum.CODE_9999);
        map.put("msg",exception.getErrorMsg());
        return map;
    }
}
