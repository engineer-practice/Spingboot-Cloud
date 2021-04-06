package com.example.dcloud.config;


import com.example.dcloud.entity.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理自定义异常
     */
    @ExceptionHandler(NeedLoginException.class)
    public Result handleException(NeedLoginException e) {
        // 打印异常信息
        return new Result(e.getResultCode());
    }

    /**
     * 处理所有不可知的异常
     */
    @ExceptionHandler(Exception.class)
    public Result handleOtherException(Exception e){
        //打印异常堆栈信息
        e.printStackTrace();
        // 打印异常信息
        return new Result(ResultCode.SYSTEM_INNER_ERROR);
    }

}
