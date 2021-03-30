package com.example.dcloud.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.dcloud.entity.Role;
import com.example.dcloud.mapper.RoleMapper;
import com.example.dcloud.service.RoleService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author fifteen
 * @since 2020-04-12
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {
    @Resource
    RoleMapper roleMapper;
    @Override
    public String roleList(String state,String name,Integer page){
        QueryWrapper<Role> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_delete",0);
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
        Page<Role> page1 = new Page<>(page,10);  // 查询第1页，每页返回10条
        IPage<Role> iPage = roleMapper.selectPage(page1,queryWrapper);

        return JSON.toJSONString(iPage);
    }
}
