package com.example.dcloud.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.dcloud.entity.Role;

public interface RoleService extends IService<Role> {
    String roleList(String state,String name,Integer page);
}
