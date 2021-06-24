package com.example.dcloud.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.dcloud.entity.Role;
import com.example.dcloud.service.RoleService;
import com.example.dcloud.util.ResultUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Api(tags = "角色管理接口")
@CrossOrigin
@Controller
@RequestMapping("/role")
public class RoleController {
    @Autowired
    RoleService roleService;

    private static final Logger LOG = LoggerFactory.getLogger(RoleController.class);

    @ResponseBody
    @RequestMapping(method = RequestMethod.POST)
    @ApiOperation(value = "创建角色",notes = "get")
    public String createRole(@RequestBody JSONObject jsonObject){
        Map map = JSON.toJavaObject(jsonObject, Map.class);
        Role role = new Role();
        //设置默认值
        role.setIsDelete(0);
        role.setIsStudent(0);
        role.setPowerId("0");
        //状态设置可用,1为可用，0表示不可用
        role.setState(1);
        role.setDescription(map.get("description").toString());
        role.setName(map.get("name").toString());
        try {
            roleService.save(role);
            return ResultUtil.success();
        }catch (Exception e){
            return ResultUtil.error("角色创建失败！");
        }
    }

    //获取用户列表
    @ResponseBody
    @RequestMapping(method = RequestMethod.GET)
    @ApiOperation(value = "获取角色列表",notes = "get")
    public String roleList(@RequestParam(value="page",required = false)Integer page,
                           @RequestParam(value="state",required = false)String state,
                           @RequestParam(value="name",required = false)String name) {
        return roleService.roleList(state,name,page);
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.PUT)
    @ApiOperation(value = "更新角色信息",notes = "get")
    public String updateRole(@RequestBody JSONObject jsonObject){
        Map map = JSON.toJavaObject(jsonObject, Map.class);
        Role role = new Role();
        //获取id，根据id更新
        role.setId(Integer.parseInt(map.get("id").toString()));
        role.setState(Integer.parseInt(map.get("state").toString()));
        role.setDescription(map.get("description").toString());
        role.setName(map.get("name").toString());
        try{
            roleService.updateById(role);
            return ResultUtil.success();
        }catch (Exception e){
            return ResultUtil.error("更新失败");
        }
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.DELETE)
    @ApiOperation(value = "删除角色",notes = "get")
    public String deleteRole(@RequestParam(value="id",required = false)Integer id){
        //根据id删除
        Role role = new Role();
        role.setId(id);
        role.setIsDelete(1);
        try{
            roleService.updateById(role);
            return ResultUtil.success();
        }catch (Exception e){
            return ResultUtil.error("删除失败");
        }
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.PATCH)
    @ApiOperation(value = "修改角色的状态",notes = "get")
    public String updateRoleState(
            @RequestParam(value="id",required = false)Integer id,
            @RequestParam(value="state",required = false)Integer state){
        Role role = new Role();
        role.setId(id);
        //取反
        state = state == 1?0:1;
        role.setState(state);
        try{
            //根据id更新
            roleService.updateById(role);
            return ResultUtil.success();
        }catch (Exception e){
            return ResultUtil.error("修改失败！");
        }
    }
}

