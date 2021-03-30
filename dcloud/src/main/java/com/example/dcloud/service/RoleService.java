package com.example.dcloud.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.dcloud.entity.Role;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author fifteen
 * @since 2020-04-12
 */
public interface RoleService extends IService<Role> {
    String roleList(String state,String name,Integer page);
}
