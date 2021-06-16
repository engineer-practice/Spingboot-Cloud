package com.example.dcloud.controller;


import com.example.dcloud.entity.Role;
import com.example.dcloud.service.RolePowerService;
import com.example.dcloud.service.RoleService;
import com.example.dcloud.util.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/rolePower")
public class RolePowerController {
    @Autowired
    RolePowerService rolePowerService;
    @Autowired
    RoleService roleService;

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET)
    public String[] rolePower(@RequestParam(value="role_id",required = false)Integer role_id) {
         Role role = roleService.getById(role_id);
         String power_id = role.getPowerId();
         String[] power = power_id.split(",");
         return power;
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.PUT)
    public String updateRolePower(@RequestParam(value="role_id",required = false)Integer role_id,
                                  @RequestParam(value="power_id",required = false)String power_id) {
        Role role = new Role();
        role.setId(role_id);
        role.setPowerId(power_id);
        System.out.println(power_id);
        System.out.println(role);
        try{
            roleService.updateById(role);
            return ResultUtil.success();
        }catch (Exception e){
            return ResultUtil.error("修改失败");
        }
    }



}

