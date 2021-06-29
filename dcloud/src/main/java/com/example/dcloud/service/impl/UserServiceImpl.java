package com.example.dcloud.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.dcloud.common.ServerResponse;
import com.example.dcloud.entity.School;
import com.example.dcloud.entity.User;
import com.example.dcloud.mapper.UserMapper;
import com.example.dcloud.service.SchoolService;
import com.example.dcloud.service.UserService;
import com.example.dcloud.vo.UserListVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.lang.Integer.parseInt;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Autowired
    private SchoolService schoolService;
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
        if(userMapper.checkEmail(email)==null){//在数据库里面查找是否有这个账号
            return 0;
        }
        else{
            return 1;
        }
    }
    @Override
    public int loginByMessage(String telephone){
        if(userMapper.checkTelephone(telephone)==null){//在数据库里面查找是否有这个账号
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
    public int selectRoleByTelephone(String telephone){
        return userMapper.selRoleByTelephone(telephone);
    }
    @Override
    public List<Map> selectUser(int offset){return userMapper.selUser(offset);}


    @Override
    public int addUser(String name,int sex,String telephone,String password,int roleId){
        if(userMapper.checkTelephone(telephone)==null){
            userMapper.addUserByAdmin(name,sex,telephone,password,roleId);
            return 1;
        }else
            return 0;
    }
    @Override
    public void updateUserByAdmin(String name,int sex,int roleId,String telephone){
        userMapper.updUserByAdmin(name,sex,roleId,telephone);
    }
    @Override
    public void deleteUser(String telephone){
        userMapper.delUser(telephone);
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
    public void changeUserStateService(String telephone){
        int state = userMapper.selState(telephone);
        state = state==0?1:0;
        userMapper.updUserState(state,telephone);
    }

    public ServerResponse<UserListVo> userList(String state, String name, Integer page,Integer size, Integer roleId){
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_delete",0);
        queryWrapper.ne("role_id",2);




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
        Page<User> page1 = new Page<>(page,size);  // 查询第1页，每页返回10条
        IPage<User> iPage = userMapper.selectPage(page1,queryWrapper);//查询到的所有用户信息数量

        ServerResponse<UserListVo> response = new ServerResponse<>();
        response.setTotal(iPage.getTotal());//查询到的所有用户信息数量

//
//        JSONArray jsonArray = new JSONArray();
//        JSONObject jsonObject2 = new JSONObject();
//        jsonObject2.put("totalCount",iPage.getTotal());//查询到的所有用户信息数量


        List<User> list = iPage.getRecords();
        int length = list.size();

//        jsonArray.add(jsonObject2);


        List<UserListVo> dataList = new ArrayList<>();

        for(int i = 0;i<length;i++){//每查到一条用户信息 就放到array中
//            JSONObject jsonObject1 = new JSONObject();
//            jsonObject1.put("name",list.get(i).getName());
//            jsonObject1.put("sex",list.get(i).getSex());
//            jsonObject1.put("email",list.get(i).getEmail());
//            jsonObject1.put("roleId",list.get(i).getRoleId());
//            jsonObject1.put("state",list.get(i).getState());
//            jsonObject1.put("schoolName",list.get(i).getSchoolCode());
//            jsonArray.add(jsonObject1);
//            System.out.println(length);
//            System.out.println(list.get(i).getName());
            UserListVo userListVo = new UserListVo();
            userListVo.setName(list.get(i).getName());
            userListVo.setSex(list.get(i).getSex());
            userListVo.setTelephone(list.get(i).getTelephone());
            userListVo.setRoleId(list.get(i).getRoleId());
            userListVo.setState(list.get(i).getState());
            String code = list.get(i).getSchoolCode();
            QueryWrapper<School> querySchool = new QueryWrapper();
            querySchool.eq("code",code)
                    .eq("is_delete",0);
            int count = schoolService.count(querySchool);
            if(count>0) {
                School school = schoolService.getOne(querySchool);
                Integer parentId = parseInt(school.getParentId());
                if (parentId != 0) {
                    QueryWrapper<School> queryWrapper1 = new QueryWrapper();
                    queryWrapper1.eq("id", parentId)
                            .eq("is_delete", 0);
                    int count1 = schoolService.count(queryWrapper1);
                    if (count1 > 0) {
                        School school1 = schoolService.getOne(queryWrapper1);
                        userListVo.setSchoolName(school1.getName());
                    }
                } else {
                    userListVo.setSchoolName(school.getName());
                }
            }
          //  userListVo.setSchoolName(list.get(i).getSchoolCode());
            else {
                userListVo.setSchoolName("未知");
            }
            dataList.add(userListVo);
        }
        response.setDataList(dataList);
        response.setResult(true);
//        return JSON.toJSONString(iPage);
        return response;//放回数组
    }
}
