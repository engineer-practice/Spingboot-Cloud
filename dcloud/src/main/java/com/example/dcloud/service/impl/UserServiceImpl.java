package com.example.dcloud.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.dcloud.entity.User;
import com.example.dcloud.mapper.UserMapper;
import com.example.dcloud.service.UserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Resource
    UserMapper userMapper;
    @Override
    public int registerService(String email,String password){
        if(userMapper.checkEmail(email)==null){
            userMapper.addUser(email,password);
            return 1;
        }
        else{
            return 0;
        }
    }
//    @Override
//    public int loginByPwd(String email,String password){
//        String emailCheck = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
//        Pattern emailRegex = Pattern.compile(emailCheck);
//        Matcher emailMatcher = emailRegex.matcher(email);
//        boolean emailIsMatched = emailMatcher.matches();
//
//        String phoneCheck = "^[1](([3|5|8][\\\\d])|([4][4,5,6,7,8,9])|([6][2,5,6,7])|([7][^9])|([9][1,8,9]))[\\\\d]{8}$";
//        Pattern phoneRegex = Pattern.compile(phoneCheck);
//        Matcher phoneMatcher = phoneRegex.matcher(email);
//        boolean phoneIsMatched = phoneMatcher.matches();
//
//        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
//        queryWrapper.eq("is_delete",0);
//        if(emailIsMatched==true){
//            queryWrapper.eq("email",email);
//        }else if(phoneIsMatched==true){
//            queryWrapper.eq("telphone",email);
//        }else{
//            queryWrapper.eq("nickname",email);
//        }
//
//        if(userMapper.selectCount(queryWrapper)==0){
//            return 0;
//        }else{
//            queryWrapper.eq("password",password);
//            if(userMapper.selectCount(queryWrapper)==0){
//                return 1;
//            }else{
//                return 2;
//            }
//        }
//    }
    @Override
    public int loginByCode(String email){
        if(userMapper.checkEmail(email)==null){
            return 0;
        }
        else{
            return 1;
        }
    }
    @Override
    public int selectRole(String email){
        return userMapper.selRole(email);
    }
    @Override
    public List<Map> selectUser(int offset){return userMapper.selUser(offset);}


    @Override
    public int addUser(String name,int sex,String email,String password,int roleId){
        if(userMapper.checkEmail(email)==null){
            userMapper.addUserByAdmin(name,sex,email,password,roleId);
            return 1;
        }else
            return 0;
    }
    @Override
    public void updateUserByAdmin(String name,int sex,int roleId,String email){
        userMapper.updUserByAdmin(name,sex,roleId,email);
    }
    @Override
    public void deleteUser(String email){
        userMapper.delUser(email);
    }
    @Override
    public int selectUserNum(){return userMapper.selUserNum();}
    @Override
    public List<Map> searchUser(int state,String name,int offset){
        if(state!=-1&&name.equals("%%")==true){
            return userMapper.selUserByState(state,offset);
        }else if(name.equals("%%")==false&&state==-1){
            return userMapper.selUserByName(name,offset);
        }else{
            return userMapper.selUserByNameAndState(name,state,offset);
        }
    }
    @Override
    public int searchUserNum(int state,String name,int offset){
        if(state!=-1&&name.equals("%%")==true){
            return userMapper.selUserByStateNum(state,offset);
        }else if(name.equals("%%")==false&&state==-1){
            return userMapper.selUserByNameNum(name,offset);
        }else{
            return userMapper.selUserByNameAndStateNum(name,state,offset);
        }
    }
    @Override
    public void changeUserStateService(String email){
        int state = userMapper.selState(email);
        state = state==0?1:0;
        userMapper.updUserState(state,email);
    }

    public String userList(String state,String name,Integer page,Integer roleId){
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_delete",0);
        queryWrapper.ne("role_id",2);
        if(roleId==1)
            queryWrapper.eq("role_id",0);
        if(state!=null && name !=null) {
            queryWrapper
                    .like("state", state)
                    .like("name", name);
        }
        if(state == null && name!=null){
            queryWrapper.like("name", name);
        }
        if(state != null && name == null){
            int s = Integer.parseInt(state);
            queryWrapper.like("state", s);
        }
        Page<User> page1 = new Page<>(page,10);  // 查询第1页，每页返回10条
        IPage<User> iPage = userMapper.selectPage(page1,queryWrapper);

        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject2 = new JSONObject();
        jsonObject2.put("totalCount",iPage.getTotal());
        List<User> list = iPage.getRecords();
        int length = list.size();
        jsonArray.add(jsonObject2);
        for(int i = 0;i<length;i++){
            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("name",list.get(i).getName());
            jsonObject1.put("sex",list.get(i).getSex());
            jsonObject1.put("email",list.get(i).getEmail());
            jsonObject1.put("roleId",list.get(i).getRoleId());
            jsonObject1.put("state",list.get(i).getState());
            jsonObject1.put("schoolName",list.get(i).getSchoolCode());
            jsonArray.add(jsonObject1);
        }
//        return JSON.toJSONString(iPage);
        return jsonArray.toString();
    }
}
