package com.example.dcloud.config;

import java.text.MessageFormat;


public class NeedLoginException extends RuntimeException{

    //错误代码
    ResultCode resultCode;

    public NeedLoginException(ResultCode resultCode){
        super(resultCode.getMessage());
        this.resultCode = resultCode;
    }

    public NeedLoginException(ResultCode resultCode, Object... args){
        super(resultCode.getMessage());
        String message = MessageFormat.format(resultCode.getMessage(), args);
        resultCode.setMessage(message);
        this.resultCode = resultCode;
    }

    public ResultCode getResultCode(){
        return resultCode;
    }
}
