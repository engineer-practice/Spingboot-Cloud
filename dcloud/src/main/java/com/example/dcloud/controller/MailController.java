package com.example.dcloud.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.dcloud.annotation.NoToken;
import com.example.dcloud.service.MailService;
import com.example.dcloud.util.ResultUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Random;

@CrossOrigin
@RestController
public class MailController {

    @Autowired
    MailService mailService;

    private static final Logger LOG = LoggerFactory.getLogger(MailController.class);

    @NoToken
    @PostMapping(value = "/sendCode")
    public String sendCode(@RequestBody JSONObject jsonObject)
    {
        Map map = JSON.toJavaObject(jsonObject,Map.class);
        String email = (String) map.get("email");
        String checkCode = String.valueOf(new Random().nextInt(899999)+100000);
        String message = "您的验证码为："+checkCode;
        try {
            //输出日志
            LOG.info("======sendCode======email：{}, checkCode:{}",email,checkCode);
            mailService.sendMail(email,"到云验证码",message);
            return ResultUtil.error(checkCode);
        }catch (Exception e){
            //输出日志
            LOG.info("======sendCode======邮箱错误，请输入真实邮箱");
            e.printStackTrace();
            return ResultUtil.error("请输入真实邮箱");
        }
    }
}
