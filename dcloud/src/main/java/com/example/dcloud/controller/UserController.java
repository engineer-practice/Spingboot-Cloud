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
    public String userList(//通过一些参数查找符合条件的用户信息 返回的是用户列表
            @RequestParam(value="page",required = false)Integer page,
            @RequestParam(value="state",required = false)String state,
            @RequestParam(value="name",required = false)String name,
            @RequestParam(value="roleId",required = false)Integer roleId
    ){
        return userService.userList(state,name,page,roleId);//返回的是一个数组，每一行代表一条用户信息
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.POST)
    public String addUser(@RequestBody JSONObject jsonObject){//新增用户
        Map map = JSON.toJavaObject(jsonObject,Map.class);
        String name = map.get("name").toString();
        int sex = Integer.parseInt(map.get("sex").toString());
        String email = map.get("email").toString();
        String password = "123456";//密码是默认
        int roleId = Integer.parseInt(map.get("roleId").toString());//角色自己可以设置
        JSONObject jsonObject1 = new JSONObject();
        if(userService.addUser(name,sex,email,password,roleId)==1){//增加用户
            jsonObject1.put("respCode","1");
            return  jsonObject1.toString();
        }else{
            jsonObject1.put("respCode","账号已存在");
            return  jsonObject1.toString();
        }
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.PUT)
        //通过 管理员 更新用户信息
    public String updateUserByAdmin(@RequestBody JSONObject jsonObject){
        Map map = JSON.toJavaObject(jsonObject,Map.class);
        String name = map.get("name").toString();
        int sex = Integer.parseInt(map.get("sex").toString());
        String email = map.get("email").toString();
        //这里可以对用户的角色进行修改
        int roleId = Integer.parseInt(map.get("roleId").toString());
        //////////////////////////
        userService.updateUserByAdmin(name,sex,roleId,email);
        JSONObject jsonObject1 = new JSONObject();
        jsonObject1.put("respCode","1");
        return jsonObject1.toString();
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.DELETE)
    //删除用户信息 可以删除多条记录
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
    //改变用户状态
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
    //更新密码
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
    //忘记密码 有点问题 只需要邮箱和新密码就可以修改
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
                //自带的函数 根据id来更新数据库
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
        //获取到邮箱直接重置
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
    //显示个人信息 根据邮箱来查找用户 并显示到前端个人信息页面
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
    //修改个人信息
    public String updateInfo(@RequestBody JSONObject jsonObject) {
        Map map = JSON.toJavaObject(jsonObject,Map.class);
        //新建一个user对象， 对前端传来的数据进行判断，符合的放入user中 最后更新
        User user = new User();
        if(map.get("sex")!=null){
            if(!map.get("sex").toString().equals("")){
                int sex = Integer.parseInt(map.get("sex").toString());
                user.setSex(sex);
            }
        }
        //这里感觉有点奇怪 没有调用setEmail方法
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
                    //判断一下这个昵称有没有被其他人使用
                    QueryWrapper<User> queryWrapper2 = new QueryWrapper<>();
                    queryWrapper2.eq("nickname",map.get("nickname").toString());
                    if(userService.count(queryWrapper2)>0)
                        return ResultUtil.error("该昵称已被使用");
                    user.setNickname(map.get("nickname").toString());
                }
            }

        }

        if(map.get("telphone")!=null){
            //和昵称一样
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
            //好像没有更新用户头像的功能
        if(map.get("image")!=null)
            if(!map.get("image").toString().equals(""))
                user.setImage(map.get("image").toString());

        try{
            //自带的更新函数
            userService.update(user,queryWrapper);
            return ResultUtil.success();
        } catch (Exception e){
            return ResultUtil.error("更新失败");
        }
    }
}
