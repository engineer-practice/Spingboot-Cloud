package com.example.dcloud.controller;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.dcloud.annotation.NoToken;
import com.example.dcloud.service.MessageService;
import com.example.dcloud.util.ResultUtil;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Random;
@Api(tags = "发送短信验证码接口")
@CrossOrigin
@RestController
public class MessageController {

    @Autowired
    MessageService messageService;
    private static final Logger LOG = LoggerFactory.getLogger(MessageController.class);
    @PostMapping(value = "/sendMessage")
    public String sendMessage(@RequestParam(value="telephone")String telephone)
    {
        String checkCode = String.valueOf(new Random().nextInt(899999)+100000);
        try {
            LOG.info("======sendMessage======telephone：{}, checkCode:{}",telephone,checkCode);
            messageService.sendMessage(telephone,checkCode);
            return ResultUtil.error(checkCode);
        }catch (Exception e){
            //输出日志
            LOG.info("======sendMessage======手机号错误，请输入真实邮箱");
            e.printStackTrace();
            return ResultUtil.error("请输入真实手机号");
        }
    }
}
