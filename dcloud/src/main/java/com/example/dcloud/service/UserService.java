package com.example.dcloud.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.dcloud.common.ServerResponse;
import com.example.dcloud.entity.User;
import com.example.dcloud.vo.UserListVo;

import java.util.List;
import java.util.Map;

public interface UserService extends IService<User> {
    int registerService(String email,String password);
//    int loginByPwd(String email,String password);
    int loginByCode(String email);
    int selectRole(String email);
    int selectRoleByTelephone(String telephone);
    List<Map> selectUser(int offset);
    int addUser(String name,int sex,String telephone,String password,int roleId);
    void updateUserByAdmin(String name,int sex,int roleId,String telephone);
    void deleteUser(String telephone);
    int selectUserNum();
    List<Map> searchUser(int state,String name,int offset);
    int searchUserNum(int state,String name,int offset);
    void changeUserStateService(String telephone);
    ServerResponse<UserListVo> userList(String state, String name, Integer page, Integer size ,Integer roleId);
    int loginByMessage(String telephone);
}
