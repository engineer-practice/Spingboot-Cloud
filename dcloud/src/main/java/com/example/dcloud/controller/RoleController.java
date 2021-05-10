package com.example.dcloud.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.dcloud.entity.Role;
import com.example.dcloud.service.RoleService;
import com.example.dcloud.util.ResultUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author fifteen
 * @since 2020-04-12
 */
@CrossOrigin
@Controller
@RequestMapping("/role")
public class RoleController {
    @Autowired
    RoleService roleService;

    private static final Logger LOG = LoggerFactory.getLogger(RoleController.class);

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET)
    public String roleList(@RequestParam(value="page",required = false)Integer page,
                           @RequestParam(value="state",required = false)String state,
                           @RequestParam(value="name",required = false)String name) {
       return roleService.roleList(state,name,page);
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.POST)
    public String createRole(@RequestBody JSONObject jsonObject){
        Map map = JSON.toJavaObject(jsonObject, Map.class);
        Role role = new Role();
        role.setIsDelete(0);
        role.setIsStudent(0);
        role.setPowerId("0");
        role.setState(1);
        role.setDescription(map.get("description").toString());
        role.setName(map.get("name").toString());
        try {
            //输出日志
            LOG.info("======createRole======创建角色成功，name:{}===description:{}",map.get("name").toString(),map.get("description").toString());
            roleService.save(role);
            return ResultUtil.success();
        }catch (Exception e){
            //输出日志
            LOG.info("======createRole======创建角色失败");
            return ResultUtil.error("创建失败");
        }
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.PUT)
    public String updateRole(@RequestBody JSONObject jsonObject){
        Map map = JSON.toJavaObject(jsonObject, Map.class);
        Role role = new Role();
        role.setId(Integer.parseInt(map.get("id").toString()));
        role.setState(Integer.parseInt(map.get("state").toString()));
        role.setDescription(map.get("description").toString());
        role.setName(map.get("name").toString());
        try{
            //输出日志
            LOG.info("======updateRole======更新角色成功");
            roleService.updateById(role);
            return ResultUtil.success();
        }catch (Exception e){
            //输出日志
            LOG.info("======updateRole======更新角色失败");
            return ResultUtil.error("更新失败");
        }
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.DELETE)
    public String deleteRole(
            @RequestParam(value="id",required = false)Integer id){
        Role role = new Role();
        role.setId(id);
        role.setIsDelete(1);
        try{
            //输出日志
            LOG.info("======deleteRole======删除角色成功，删除id为：{}",id);
            roleService.updateById(role);
            return ResultUtil.success();
        }catch (Exception e){
            //输出日志
            LOG.info("======deleteRole======删除角色失败");
            return ResultUtil.error("删除失败");
        }
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.PATCH)
    public String updateRoleState(
            @RequestParam(value="id",required = false)Integer id,
            @RequestParam(value="state",required = false)Integer state){
        Role role = new Role();
        role.setId(id);
        state = state == 1?0:1;
        role.setState(state);
        try{
            roleService.updateById(role);
            return ResultUtil.success();
        }catch (Exception e){
            return ResultUtil.error("修改失败");
        }
    }
}

