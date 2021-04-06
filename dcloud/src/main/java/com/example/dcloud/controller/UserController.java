package com.example.dcloud.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.dcloud.annotation.NoToken;
import com.example.dcloud.entity.User;
import com.example.dcloud.service.UserService;
import com.example.dcloud.util.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@CrossOrigin
@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserService userService;

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET)
    public String userList(
            @RequestParam(value="page",required = false)Integer page,
            @RequestParam(value="state",required = false)String state,
            @RequestParam(value="name",required = false)String name,
            @RequestParam(value="roleId",required = false)Integer roleId
    ){
        return userService.userList(state,name,page,roleId);
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.POST)
    public String addUser(@RequestBody JSONObject jsonObject){
        Map map = JSON.toJavaObject(jsonObject,Map.class);
        String name = map.get("name").toString();
        int sex = Integer.parseInt(map.get("sex").toString());
        String email = map.get("email").toString();
        String password = "123456";
        int roleId = Integer.parseInt(map.get("roleId").toString());
        JSONObject jsonObject1 = new JSONObject();
        if(userService.addUser(name,sex,email,password,roleId)==1){
            jsonObject1.put("respCode","1");
            return  jsonObject1.toString();
        }else{
            jsonObject1.put("respCode","账号已存在");
            return  jsonObject1.toString();
        }
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.PUT)
    public String updateUserByAdmin(@RequestBody JSONObject jsonObject){
        Map map = JSON.toJavaObject(jsonObject,Map.class);
        String name = map.get("name").toString();
        int sex = Integer.parseInt(map.get("sex").toString());
        String email = map.get("email").toString();
        int roleId = Integer.parseInt(map.get("roleId").toString());
        userService.updateUserByAdmin(name,sex,roleId,email);
        JSONObject jsonObject1 = new JSONObject();
        jsonObject1.put("respCode","1");
        return jsonObject1.toString();
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.DELETE)
    public String deleteUser(@RequestParam(value = "del_list") List list){
        for(int i=0;i<list.size();i++){
            String email = list.get(i).toString();
            userService.deleteUser(email);
        }
        JSONObject jsonObject1 = new JSONObject();
        jsonObject1.put("respCode","1");
        return jsonObject1.toString();
    }


    @ResponseBody
    @RequestMapping(method = RequestMethod.PATCH)
    public String changeUserState(@RequestBody JSONObject jsonObject){
        Map map = JSON.toJavaObject(jsonObject,Map.class);
        String email = map.get("email").toString();
        userService.changeUserStateService(email);
        JSONObject jsonObject1 = new JSONObject();
        jsonObject1.put("respCode","1");
        return jsonObject1.toString();
    }

    @ResponseBody
    @RequestMapping(value = "/updatePassword",method = RequestMethod.POST)
    public String updatePassword(@RequestBody JSONObject jsonObject) {
        Map map = JSON.toJavaObject(jsonObject, Map.class);
        String email = map.get("email").toString();
        String newPassword = map.get("newpassword1").toString();
        String repeatNewPassword = map.get("newpassword2").toString();
        String oldPassword = map.get("oldpassword").toString();
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("email",email);
        User user = userService.getOne(queryWrapper);
        String password = user.getPassword();
        int id = user.getId();
        if(oldPassword.equals("")){
            if(newPassword.equals(repeatNewPassword)){
                User user1 = new User();
                user1.setId(id);
                user1.setPassword(newPassword);
                userService.updateById(user1);
                return ResultUtil.success();
            }
            else{
                return ResultUtil.error("两次输入的密码不一致");
            }
        }
        else {
            if(oldPassword.equals(password)){
                if(newPassword.equals(repeatNewPassword)){
                    User user1 = new User();
                    user1.setId(id);
                    user1.setPassword(newPassword);
                    userService.updateById(user1);
                    return ResultUtil.success();
                }
                else{
                    return ResultUtil.error("两次输入的密码不一致");
                }
            }
            else{
                return ResultUtil.error("原密码错误");
            }
        }
    }

    @ResponseBody
    @NoToken
    @RequestMapping(value = "/forgetPassword",method = RequestMethod.POST)
    public String forgetPassword(@RequestBody JSONObject jsonObject) {
        Map map = JSON.toJavaObject(jsonObject, Map.class);
        String email = map.get("email").toString();
        String newPassword = map.get("newpassword").toString();
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("email",email);
        User user = userService.getOne(queryWrapper);
        if(user!=null){
            if(user.getIsDelete()==0){
                int id = user.getId();
                User user1 = new User();
                user1.setId(id);
                user1.setPassword(newPassword);
                userService.updateById(user1);
                return ResultUtil.success();
            }
            else{
                return ResultUtil.error("账号已被删除");
            }
        }
        else{
            return ResultUtil.error("账号不存在");
        }
    }

    @ResponseBody
    @RequestMapping(value = "/resetPassword",method = RequestMethod.PUT)
    public String resetPassword(@RequestParam(value="email",required = false)String email) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("email",email);
        User user = new User();
        user.setPassword("123456");
        try{
            userService.update(user,queryWrapper);
            return ResultUtil.success();
        } catch (Exception e){
            return ResultUtil.error("重置失败");
        }
    }

    @ResponseBody
    @RequestMapping(value = "/info",method = RequestMethod.GET)
    public String getInfo(@RequestParam(value="email",required = false)String email) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("email",email);
        try{
            User user = userService.getOne(queryWrapper);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("nickname",user.getNickname());
            jsonObject.put("sno",user.getSno());
            jsonObject.put("birth",user.getBirth());
            jsonObject.put("sex",user.getSex());
            jsonObject.put("school",user.getSchoolCode());
            jsonObject.put("role",user.getRoleId());
            jsonObject.put("name",user.getName());
            jsonObject.put("telphone",user.getTelphone());
            jsonObject.put("exp",user.getExp());
            jsonObject.put("image",user.getImage());
            return jsonObject.toString();
        } catch (Exception e){
            return ResultUtil.error("获取失败");
        }
    }

    @ResponseBody
    @RequestMapping(value = "/info",method = RequestMethod.PUT)
    public String updateInfo(@RequestBody JSONObject jsonObject) {
        Map map = JSON.toJavaObject(jsonObject,Map.class);
        User user = new User();
        if(map.get("sex")!=null){
            if(!map.get("sex").toString().equals("")){
                int sex = Integer.parseInt(map.get("sex").toString());
                user.setSex(sex);
            }
        }
        String email = map.get("email").toString();
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("email",email);
        User user1 = userService.getOne(queryWrapper);
//        if(!map.get("school").toString().equals(""))
//        {
//            int school = Integer.parseInt(map.get("school").toString());
//            user.setSchoolId(school);
//        }

        if(map.get("school")!=null)
            if(!map.get("school").toString().equals(""))
                user.setSchoolCode(map.get("school").toString());
        if(map.get("sno")!=null)
            if(!map.get("sno").toString().equals(""))
                user.setSno(map.get("sno").toString());
        if(map.get("name")!=null)
            if(!map.get("name").toString().equals(""))
                user.setName(map.get("name").toString());
        if(map.get("nickname")!=null){
            if(!map.get("nickname").toString().equals("")){
                if(!user1.getNickname().equals(map.get("nickname").toString())){
                    QueryWrapper<User> queryWrapper2 = new QueryWrapper<>();
                    queryWrapper2.eq("nickname",map.get("nickname").toString());
                    if(userService.count(queryWrapper2)>0)
                        return ResultUtil.error("该昵称已被使用");
                    user.setNickname(map.get("nickname").toString());
                }
            }

        }

        if(map.get("telphone")!=null){
            if(!map.get("telphone").toString().equals("")){
                if(!user1.getTelphone().equals(map.get("telphone").toString())){
                    QueryWrapper<User> queryWrapper1 = new QueryWrapper<>();
                    queryWrapper1.eq("telphone",map.get("telphone").toString());
                    if(userService.count(queryWrapper1)>0)
                        return ResultUtil.error("该手机号已被使用");

                    String phoneCheck = "^[1](([3|5|8][\\d])|([4][4,5,6,7,8,9])|([6][2,5,6,7])|([7][^9])|([9][1,8,9]))[\\d]{8}$";
                    Pattern phoneRegex = Pattern.compile(phoneCheck);
                    Matcher phoneMatcher = phoneRegex.matcher(map.get("telphone").toString());
                    boolean phoneIsMatched = phoneMatcher.matches();
                    if(phoneIsMatched==true)
                        user.setTelphone(map.get("telphone").toString());
                    else
                        return ResultUtil.error("请输入真实手机号");
                }
            }

        }
        if(map.get("birth")!=null)
            if(!map.get("birth").toString().equals(""))
                user.setBirth(map.get("birth").toString());
        if(map.get("image")!=null)
            if(!map.get("image").toString().equals(""))
                user.setImage(map.get("image").toString());

        try{
            userService.update(user,queryWrapper);
            return ResultUtil.success();
        } catch (Exception e){
            return ResultUtil.error("更新失败");
        }
    }
}
