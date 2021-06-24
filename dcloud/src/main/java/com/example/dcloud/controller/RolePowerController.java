package com.example.dcloud.controller;


import com.example.dcloud.entity.Role;
import com.example.dcloud.service.RolePowerService;
import com.example.dcloud.service.RoleService;
import com.example.dcloud.util.ResultUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Api(tags = "角色权限接口")
@Controller
@RequestMapping("/rolePower")
public class RolePowerController {
    @Autowired
    RolePowerService rolePowerService;
    @Autowired
    RoleService roleService;

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET)
    @ApiOperation(value = "返回权限id",notes = "get")
    public String[] rolePower(@RequestParam(value="role_id",required = false)Integer role_id) {
         Role role = roleService.getById(role_id);
         String power_id = role.getPowerId();
         String[] power = power_id.split(",");
         return power;
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.PUT)
    @ApiOperation(value = "更新角色权限",notes = "get")
    public String updateRolePower(@RequestParam(value="role_id",required = false)Integer role_id,
                                  @RequestParam(value="power_id",required = false)String power_id) {
        Role role = new Role();
        role.setId(role_id);
        role.setPowerId(power_id);
        try{
            roleService.updateById(role);
            return ResultUtil.success();
        }catch (Exception e){
            return ResultUtil.error("更新失败！");
        }
    }
}

