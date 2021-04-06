package com.example.dcloud.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.dcloud.annotation.NoToken;
import com.example.dcloud.entity.User;
import com.example.dcloud.service.RoleService;
import com.example.dcloud.service.UserService;
import com.example.dcloud.util.ResultUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@CrossOrigin
@RestController
public class LoginController {

    @Autowired
    UserService userService;
    @Autowired
    RoleService roleService;

    private static final Logger LOG = LoggerFactory.getLogger(LoginController.class);

    @NoToken
    @PostMapping(value = "/loginByPassword")
    public String loginByPassword(@RequestBody JSONObject jsonObject)
    {
        Map map = JSON.toJavaObject(jsonObject,Map.class);
        String email = (String) map.get("email");
        String password = (String) map.get("password");
        String emailCheck = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
        Pattern emailRegex = Pattern.compile(emailCheck);
        Matcher emailMatcher = emailRegex.matcher(email);
        boolean emailIsMatched = emailMatcher.matches();
        System.out.println(emailIsMatched);

//        String phoneCheck = "^[1](([3|5|8][\\d])|([4][4,5,6,7,8,9])|([6][2,5,6,7])|([7][^9])|([9][1,8,9]))[\\d]{8}$";
//        Pattern phoneRegex = Pattern.compile(phoneCheck);
//        Matcher phoneMatcher = phoneRegex.matcher(email);
//        boolean phoneIsMatched = phoneMatcher.matches();
//        System.out.println(phoneIsMatched);

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_delete",0);
        if(emailIsMatched==true){
            queryWrapper.eq("email",email);
        }
//        else if(phoneIsMatched==true){
//            queryWrapper.eq("telphone",email);
//        }
//        else{
//            queryWrapper.eq("nickname",email);
//        }

        JSONObject jsonObject1 = new JSONObject();
        if(userService.count(queryWrapper)==0){
            //输出日志
            LOG.info("======loginByPassword======email：{}, password：{}===登录失败（账号或密码错误）",email,password);

            jsonObject1.put("respCode","账号或密码错误");
            jsonObject1.put("role","-1");
            return jsonObject1.toString();
        }else{
            queryWrapper.eq("password",password);
            if(userService.count(queryWrapper)==0){
                //输出日志
                LOG.info("======loginByPassword======email：{}, password：{}===登录失败（账号或密码错误）",email,password);

                jsonObject1.put("respCode","账号或密码错误");
                jsonObject1.put("role","-1");
                return jsonObject1.toString();
            }else{
                //输出日志
                LOG.info("======loginByPassword======email：{}, password：{}===登录成功",email,password);

                User user = userService.getOne(queryWrapper);
                String token = JWT.create().withAudience(user.getId() + "")
                        .sign(Algorithm.HMAC256(user.getPassword()));
                jsonObject1.put("respCode","1");
                int role_id = user.getRoleId();
                jsonObject1.put("role",role_id);
                jsonObject1.put("token",token);
                jsonObject1.put("email",user.getEmail());
                return jsonObject1.toString();
            }
        }
    }

    @NoToken
    @PostMapping(value = "/loginByCode")
    public String loginByCode(@RequestBody JSONObject jsonObject)
    {
        Map map = JSON.toJavaObject(jsonObject,Map.class);
        String email = (String) map.get("email");
        JSONObject jsonObject1 = new JSONObject();
        if(userService.loginByCode(email)==1){
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("email",email);
            User user = userService.getOne(queryWrapper);
            if(user.getIsDelete()==1){
                return ResultUtil.error("账号已被删除！");
            }
            String token = JWT.create().withAudience(user.getId() + "")
                    .sign(Algorithm.HMAC256(user.getPassword()));

            //输出日志
            LOG.info("======loginByCode验证码登录======email：{}===登录成功",email);

            jsonObject1.put("respCode","1");
            int role_id = userService.selectRole(email);
            jsonObject1.put("role",role_id);
            jsonObject1.put("token",token);
            return jsonObject1.toString();
        }
        else{
            //输出日志
            LOG.info("======loginByCode验证码登录======email：{}===登录失败（账号不存在）",email);

            jsonObject1.put("respCode","账号不存在");
            jsonObject1.put("role","-1");
            return jsonObject1.toString();
        }
    }
}
