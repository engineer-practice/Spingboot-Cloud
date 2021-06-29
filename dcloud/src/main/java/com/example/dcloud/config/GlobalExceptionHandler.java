package com.example.dcloud.config;


import com.example.dcloud.entity.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NeedLoginException.class)
    public Result handleException(NeedLoginException e) {
        return new Result(e.getResultCode());
    }
    @ExceptionHandler(Exception.class)
    public Result handleOtherException(Exception e){
        e.printStackTrace();
        return new Result(ResultCode.SYSTEM_INNER_ERROR);
    }

}
