package com.example.dcloud.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.dcloud.annotation.NoToken;
import com.example.dcloud.entity.User;
import com.example.dcloud.entity.UserAuths;
import com.example.dcloud.service.UserService;
import com.example.dcloud.util.ResultUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Api(tags = "注册接口")
@CrossOrigin
@RestController
public class RegisterController {

    @Autowired
    UserService userService;
    private static final Logger LOG = LoggerFactory.getLogger(RegisterController.class);
    @NoToken
    @ApiOperation(value = "短信注册",notes = "get")
    @PostMapping("/registerByMessage")
    public String registerByMessage(@RequestParam(value="telephone")String telephone)
    {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("telephone",telephone);
        int count = userService.count(queryWrapper);
        if(count>0){
            return ResultUtil.error("该手机号已经注册过了！");
        }
        else{
            User user1 = new User();
            user1.setEmail("0");
            user1.setExp(0);
            user1.setIsDelete(0);
            user1.setNickname("0");
            user1.setSno("0");
            user1.setName("0");
            user1.setImage("0");
            user1.setBirth("0");
            user1.setTelephone(telephone);
            user1.setSex(0);
            user1.setEducation(0);
            user1.setPassword("123456");
            user1.setPowerId("0");
            user1.setSchoolCode("0");
            user1.setState(0);
            user1.setRoleId(0);//表示教师
            userService.save(user1);
            return ResultUtil.success();
        }
    }
    @NoToken
    @ApiOperation(value = "邮箱注册",notes = "get")
    @PostMapping(value = "/registerByEmail")
    public String registerByEmail(@RequestParam(value="email")String email,
                                  @RequestParam(value="password")String password)
    {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("email",email);
        int count = userService.count(queryWrapper);
        System.out.println(count);
        if(count>0){
            return ResultUtil.error("该邮箱已经注册过了！");
        }
        else{
            User user1 = new User();
            user1.setEmail(email);
            user1.setExp(0);
            user1.setIsDelete(0);
            user1.setNickname("0");
            user1.setSno("0");
            user1.setName("0");
            user1.setImage("0");
            user1.setBirth("0");
            user1.setTelephone("0");
            user1.setSex(0);
            user1.setEducation(0);
            user1.setPassword(password);
            user1.setPowerId("0");
            user1.setSchoolCode("0");
            user1.setState(0);
            user1.setRoleId(0);
            userService.save(user1);
            return ResultUtil.success();
        }
    }

}
