package com.example.dcloud.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.dcloud.annotation.NoToken;
import com.example.dcloud.common.ServerResponse;
import com.example.dcloud.dto.UpdateInfoDto;
import com.example.dcloud.dto.UpdatePasswordDto;
import com.example.dcloud.dto.UserBasicDto;
import com.example.dcloud.dto.UserDto;
import com.example.dcloud.entity.User;
import com.example.dcloud.service.UserService;
import com.example.dcloud.util.ResultUtil;
import com.example.dcloud.vo.UserListVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
@Api(tags = "用户接口")
@CrossOrigin
@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserService userService;
    @ResponseBody
    @RequestMapping(method = RequestMethod.GET)
    public ServerResponse<UserListVo> userList(//通过一些参数查找符合条件的用户信息 返回的是用户列表
                                               @RequestParam(value="page",required = false)Integer page,
                                               @RequestParam(value="size",required = false)Integer size,
                                               @RequestParam(value="state",required = false)String state,
                                               @RequestParam(value="name",required = false)String name,
                                               @RequestParam(value="roleId",required = false)Integer roleId
    ){
        return userService.userList(state,name,page,size,roleId);//返回的是一个数组，每一行代表一条用户信息
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.POST)
    public String addUser(@RequestBody UserBasicDto userBasicDto){//新增用户
        String password = "123456";//密码是默认
       // int roleId = Integer.parseInt(map.get("roleId").toString());//角色自己可以设置
        JSONObject jsonObject1 = new JSONObject();
        if(userService.addUser(userBasicDto.getName(),userBasicDto.getSex(),userBasicDto.getTelephone(),password,userBasicDto.getRoleId())==1){//增加用户
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
    public String updateUserByAdmin(@RequestBody UserBasicDto userBasicDto){
        userService.updateUserByAdmin(userBasicDto.getName(),userBasicDto.getSex(),userBasicDto.getRoleId(),userBasicDto.getTelephone());
        JSONObject jsonObject1 = new JSONObject();
        jsonObject1.put("respCode","1");
        return jsonObject1.toString();
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.DELETE)
    //删除用户信息 可以删除多条记录
    public String deleteUser(@RequestParam(value = "del_list") List list){
        for(int i=0;i<list.size();i++){
            String telephone = list.get(i).toString();
            userService.deleteUser(telephone);
        }
        JSONObject jsonObject1 = new JSONObject();
        jsonObject1.put("respCode","1");
        return jsonObject1.toString();
    }


    @ResponseBody
    @RequestMapping(method = RequestMethod.PATCH)
    //改变用户状态
    public String changeUserState( @RequestParam(value="telephone")String telephone){
        userService.changeUserStateService(telephone);
        JSONObject jsonObject1 = new JSONObject();
        jsonObject1.put("respCode","1");
        return jsonObject1.toString();
    }

    @ResponseBody
    @RequestMapping(value = "/updatePassword",method = RequestMethod.POST)
    //更新密码
    public String updatePassword(@RequestBody UpdatePasswordDto updatePasswordDto) {
        //Map map = JSON.toJavaObject(jsonObject, Map.class);
        String telephone = updatePasswordDto.getTelephone();
        String newPassword = updatePasswordDto.getNewpassword1();
        String repeatNewPassword = updatePasswordDto.getNewpasswor2();
        String oldPassword = updatePasswordDto.getOldpassword();
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("telephone",telephone);
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
    //忘记密码
    public String forgetPassword(@RequestParam(value="telephone")String telephone,
                                 @RequestParam(value="newpassword")String newPassword) {

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("telephone",telephone);
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
    public String resetPassword(@RequestParam(value="telephone",required = false)String telephone) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        //获取到电话直接重置
        queryWrapper.eq("telephone",telephone);
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
    public String getInfo(@RequestParam(value="account")String account) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("telephone",account);
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
            jsonObject.put("telephone",user.getTelephone());
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
    public String updateInfo(@RequestBody UpdateInfoDto updateInfoDto) {
       // Map map = JSON.toJavaObject(jsonObject,Map.class);
        //新建一个user对象， 对前端传来的数据进行判断，符合的放入user中 最后更新
        User user = new User();
        if(updateInfoDto.getSex()!=null){
            if(!updateInfoDto.getSex().equals("")){
                user.setSex(updateInfoDto.getSex());
            }
        }
        String telephone = updateInfoDto.getTelephone();
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("telephone",telephone);
        User user1 = userService.getOne(queryWrapper);
        if(updateInfoDto.getSchoolCode()!=null)
            if(!updateInfoDto.getSchoolCode().equals(""))
                user.setSchoolCode(updateInfoDto.getSchoolCode());
        if(updateInfoDto.getSno()!=null)
            if(!updateInfoDto.getSno().equals(""))
                user.setSno(updateInfoDto.getSno());
        if(updateInfoDto.getName()!=null)
            if(!updateInfoDto.getName().equals(""))
                user.setName(updateInfoDto.getName());
        if(updateInfoDto.getNickname()!=null){
            if(!updateInfoDto.getNickname().equals("")){
                if(!user1.getNickname().equals(updateInfoDto.getNickname())){
                    //判断一下这个昵称有没有被其他人使用
                    QueryWrapper<User> queryWrapper2 = new QueryWrapper<>();
                    queryWrapper2.eq("nickname",updateInfoDto.getNickname());
                    if(userService.count(queryWrapper2)>0)
                        return ResultUtil.error("该昵称已被使用");
                    user.setNickname(updateInfoDto.getNickname());
                }
            }

        }

        if(updateInfoDto.getTelephone()!=null){
            //和昵称一样
            if(!updateInfoDto.getTelephone().equals("")){
                if(!user1.getTelephone().equals(updateInfoDto.getTelephone())){
                    QueryWrapper<User> queryWrapper1 = new QueryWrapper<>();
                    queryWrapper1.eq("telephone",updateInfoDto.getTelephone());
                    if(userService.count(queryWrapper1)>0)
                        return ResultUtil.error("该手机号已被使用");

                    String phoneCheck = "^[1](([3|5|8][\\d])|([4][4,5,6,7,8,9])|([6][2,5,6,7])|([7][^9])|([9][1,8,9]))[\\d]{8}$";
                    Pattern phoneRegex = Pattern.compile(phoneCheck);
                    Matcher phoneMatcher = phoneRegex.matcher(updateInfoDto.getTelephone());
                    boolean phoneIsMatched = phoneMatcher.matches();
                    if(phoneIsMatched==true)
                        user.setTelephone(updateInfoDto.getTelephone());
                    else
                        return ResultUtil.error("请输入真实手机号");
                }
            }
        }
        if(updateInfoDto.getBirth()!=null)
            if(!updateInfoDto.getBirth().equals(""))
                user.setBirth(updateInfoDto.getBirth());
        if(updateInfoDto.getImage()!=null)
            if(!updateInfoDto.getImage().equals(""))
                user.setImage(updateInfoDto.getImage());

        try{
            //自带的更新函数
            userService.update(user,queryWrapper);
            return ResultUtil.success();
        } catch (Exception e){
            return ResultUtil.error("更新失败");
        }
    }

}
