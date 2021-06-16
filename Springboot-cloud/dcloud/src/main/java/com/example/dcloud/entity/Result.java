package com.example.dcloud.entity;

import com.alibaba.fastjson.JSONObject;
import com.example.dcloud.config.ResultCode;

public class Result<T> {
    //操作代码
    int respCode;

    //提示信息
    String message;

    //结果数据
    T data;

    public Result(ResultCode resultCode){
        this.respCode = resultCode.getRespCode();
        this.message = resultCode.getMessage();
    }

    public Result(ResultCode resultCode, T data){
        this.respCode = resultCode.getRespCode();
        this.message = resultCode.getMessage();
        this.data = data;
    }

    public Result(String message){
        this.message = message;
    }

    public static Result SUCCESS(){
        return new Result(ResultCode.SUCCESS);
    }

    public static <T> Result SUCCESS(T data){
        return new Result(ResultCode.SUCCESS, data);
    }

    public static Result FAIL(){
        return new Result(ResultCode.FAIL);
    }

    public static Result FAIL(String message){
        return new Result(message);
    }

    public int getCode() {
        return respCode;
    }

    public void setCode(int respCode) {
        this.respCode = respCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
