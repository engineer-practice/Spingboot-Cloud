package com.example.dcloud.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.dcloud.entity.User;

import java.util.List;
import java.util.Map;
/**
 * <p>
 *  服务类
 * </p>
 *
 * @author fifteen
 * @since 2020-04-01
 */
public interface UserService extends IService<User> {
    int registerService(String email,String password);
//    int loginByPwd(String email,String password);
    int loginByCode(String email);
    int selectRole(String email);
    List<Map> selectUser(int offset);
    int addUser(String name,int sex,String email,String password,int roleId);
    void updateUserByAdmin(String name,int sex,int roleId,String email);
    void deleteUser(String email);
    int selectUserNum();
    List<Map> searchUser(int state,String name,int offset);
    int searchUserNum(int state,String name,int offset);
    void changeUserStateService(String email);
    String userList(String state,String name,Integer page,Integer roleId);
}
