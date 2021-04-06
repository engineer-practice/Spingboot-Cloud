package com.example.dcloud.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.dcloud.annotation.NoToken;
import com.example.dcloud.entity.User;
import com.example.dcloud.service.UserService;
import com.example.dcloud.util.ResultUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin
@Controller
@NoToken
@RequestMapping("/register")
public class RegisterController {

    @Autowired
    UserService userService;

    private static final Logger LOG = LoggerFactory.getLogger(RegisterController.class);

    @ResponseBody
    @NoToken
    @RequestMapping(method = RequestMethod.POST)
    public String register(@RequestBody JSONObject jsonObject)
    {
        Map map = JSON.toJavaObject(jsonObject,Map.class);
        String email = (String) map.get("email");
        String password = (String) map.get("password");
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("email",email);
        int count = userService.count(queryWrapper);
        if(count>0){
            //输出日志
            LOG.info("======register======email：{}, password：{}===注册失败（该邮箱已经注册过了）",email,password);
            return ResultUtil.error("该邮箱已经注册过了！");
        }
        else{
            //输出日志
            LOG.info("======register======email：{}, password：{}===注册成功",email,password);
            
            User user1 = new User();
            user1.setEmail(email);
            user1.setExp(0);
            user1.setIsDelete(0);
            user1.setNickname("0");
            user1.setSno("0");
            user1.setName("0");
            user1.setImage("0");
            user1.setBirth("0");
            user1.setTelphone("0");
            user1.setSex(0);
            user1.setEducation(0);
            user1.setPassword(password);
            user1.setPowerId("0");
            user1.setSchoolCode("0");
            user1.setState(0);
            if(map.get("role_id")==null){
                user1.setRoleId(0);
            }else{
                user1.setRoleId(3);
            }
            userService.save(user1);
            return ResultUtil.success();
        }
    }
}
