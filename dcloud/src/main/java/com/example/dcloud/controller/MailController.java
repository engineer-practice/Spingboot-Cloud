package com.example.dcloud.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.dcloud.annotation.NoToken;
import com.example.dcloud.service.MailService;
import com.example.dcloud.util.ResultUtil;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Random;
@Api(tags = "发送邮箱验证码接口")
@CrossOrigin
@RestController
public class MailController {

    @Autowired
    MailService mailService;

    private static final Logger LOG = LoggerFactory.getLogger(MailController.class);

    @NoToken
    @PostMapping(value = "/sendCode")
    public String sendCode(@RequestParam(value="email")String email)
    {
        LOG.info("ddd:已经连接上");
        String checkCode = String.valueOf(new Random().nextInt(899999)+100000);
        String message = "您的验证码为："+checkCode;
        try {
            mailService.sendMail(email,"验证码",message);
            return ResultUtil.error(checkCode);
        }catch (Exception e){
            e.printStackTrace();
            return ResultUtil.error("请输入真实邮箱");
        }
    }
}
