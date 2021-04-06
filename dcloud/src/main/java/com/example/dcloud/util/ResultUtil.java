package com.example.dcloud.util;

import com.alibaba.fastjson.JSONObject;

public class ResultUtil {
    public static String success(){
        JSONObject jsonObject1 = new JSONObject();
        jsonObject1.put("respCode","1");
        return  jsonObject1.toString();
    }

    public static String error(String msg) {
        JSONObject jsonObject1 = new JSONObject();
        jsonObject1.put("respCode",msg);
        return  jsonObject1.toString();
    }
}