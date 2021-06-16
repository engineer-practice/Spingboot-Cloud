package com.example.dcloud.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.dcloud.annotation.NoToken;
import com.example.dcloud.common.ServerResponse;
import com.example.dcloud.entity.User;
import com.example.dcloud.service.UserService;
import com.example.dcloud.vo.loginByMessageVo;
import com.example.dcloud.vo.loginByPasswordVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
@Api(tags = "登录接口")
@CrossOrigin
@RestController
public class LoginController {

    @Autowired
    UserService userService;
    private static final Logger LOG = LoggerFactory.getLogger(LoginController.class);
    @NoToken
    @ApiOperation(value = "账号密码登录",notes = "get")
    @PostMapping(value = "/loginByPassword")
    public ServerResponse<loginByPasswordVo> loginByPassword(@RequestParam(value="account")String account,
                                                             @RequestParam(value="password")String password)
    {
        String emailCheck = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
        Pattern emailRegex = Pattern.compile(emailCheck);
        Matcher emailMatcher = emailRegex.matcher(account);
        boolean emailIsMatched = emailMatcher.matches();
        String phoneCheck = "^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$";
        Pattern phoneRegex = Pattern.compile(phoneCheck);
        Matcher phoneMatcher = phoneRegex.matcher(account);
        boolean phoneIsMatched = phoneMatcher.matches();
        System.out.println("是否是邮箱："+emailIsMatched);
        System.out.println("是否是手机号："+phoneIsMatched);
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_delete",0);
        System.out.println(userService.count(queryWrapper));
        if(emailIsMatched==true){
            queryWrapper.eq("email",account);
        }
        if(phoneIsMatched==true){
            queryWrapper.eq("telephone",account);
        }

        ServerResponse<loginByPasswordVo> response = new ServerResponse<>();
        loginByPasswordVo loginVo = new loginByPasswordVo();
        List<loginByPasswordVo> dataList = new ArrayList<>();

        if(userService.count(queryWrapper)==0){
            //输出日志
            LOG.info("======loginByPassword======account：{}, password：{}===登录失败（账号或密码错误）",account,password);
            response.setMsg("账号或密码错误");
            response.setResult(false);
            return response;
        }else{
            queryWrapper.eq("password",password);
            if(userService.count(queryWrapper)==0){
                //输出日志
                LOG.info("======loginByPassword======account：{}, password：{}===登录失败（账号或密码错误）",account,password);
                response.setMsg("账号或密码错误");
                response.setResult(false);
                return response;
            }else{
                //输出日志
                LOG.info("======loginByPassword======account：{}, password：{}===登录成功",account,password);
                User user = userService.getOne(queryWrapper);
                String token = JWT.create().withAudience(user.getId() + "")
                        .sign(Algorithm.HMAC256(user.getPassword()));
                loginVo.setRole(user.getRoleId());
                if(emailIsMatched==true) {
                    loginVo.setAccount(user.getEmail());
                }
                else{
                    loginVo.setAccount(user.getTelephone());
                }
                loginVo.setToken(token);
                dataList.add(loginVo);
                response.setDataList(dataList);
                response.setMsg("登录成功");
                response.setResult(true);
                return response;
            }
        }
    }
    @NoToken
    @ApiOperation(value = "邮箱验证码登录",notes = "get")
    @PostMapping(value = "/loginByCode")
    public ServerResponse<loginByMessageVo> loginByCode(@RequestParam(value="email")String email)
    {
        ServerResponse<loginByMessageVo> response = new ServerResponse<>();
        if(userService.loginByCode(email)==1){
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("email",email);
            User user = userService.getOne(queryWrapper);
            if(user.getIsDelete()==1){
                response.setMsg("账号已被删除！");
                response.setResult(false);
                return response;
            }
            String token = JWT.create().withAudience(user.getId() + "")
                    .sign(Algorithm.HMAC256(user.getPassword()));
            LOG.info("======loginByCode验证码登录======email：{}===登录成功",email);
            List<loginByMessageVo> dataList = new ArrayList<>();
            loginByMessageVo loginVo = new loginByMessageVo();
            int role_id = userService.selectRole(email);
            loginVo.setRole(role_id);
            loginVo.setToken(token);
            dataList.add(loginVo);
            response.setDataList(dataList);
            response.setMsg("登录成功！");
            response.setResult(true);
            return response;
        }
        else{
            //输出日志
            LOG.info("======loginByCode验证码登录======email：{}===登录失败（账号不存在）",email);
            response.setResult(false);
            response.setMsg("账号不存在!");
            return response;
        }
    }
    @NoToken
    @ApiOperation(value = "短信验证码登录",notes = "get")
    @PostMapping(value = "/loginByMessage")
    public ServerResponse<loginByMessageVo> loginByMessage(@RequestParam(value="telephone")String telephone) {
        ServerResponse<loginByMessageVo> response = new ServerResponse<>();
        if (userService.loginByMessage(telephone) == 1) {
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("telephone", telephone);
            User user = userService.getOne(queryWrapper);
            if (user.getIsDelete() == 1) {
                response.setMsg("账号已被删除！");
                response.setResult(false);
               // return response;
            }
            String token = JWT.create().withAudience(user.getId() + "")
                    .sign(Algorithm.HMAC256(user.getPassword()));

            //输出日志
            LOG.info("======loginByMessage短信验证码登录======telephone：{}===登录成功", telephone);

            int role_id = userService.selectRoleByTelephone(telephone);
            loginByMessageVo loginVo = new loginByMessageVo();
            loginVo.setRole(role_id);
            loginVo.setToken(token);
            List<loginByMessageVo> datalist = new ArrayList<>();
            datalist.add(loginVo);
            response.setDataList(datalist);
            response.setMsg("登录成功");
            response.setResult(true);
        } else {
            //输出日志
            LOG.info("======loginByMessage短信验证码登录======telephone：{}===登录失败（账号不存在）", telephone);
            response.setResult(false);
            response.setMsg("账号不存在");
          //  return response;
        }
        return response;
    }
}
