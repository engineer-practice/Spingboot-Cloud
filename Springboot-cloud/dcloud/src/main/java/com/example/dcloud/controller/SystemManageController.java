package com.example.dcloud.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.dcloud.entity.Dictionary;
import com.example.dcloud.entity.DictionaryDetail;
import com.example.dcloud.entity.SystemManage;
import com.example.dcloud.service.SystemManageService;
import com.example.dcloud.util.ResultUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.stereotype.Controller;
import sun.awt.LightweightFrame;

import java.util.*;

import static java.lang.Integer.parseInt;
import static java.lang.Long.parseLong;

@Api(tags = "系统参数管理接口")
@Controller
@RequestMapping("/systems")
public class SystemManageController {
    @Autowired
    private SystemManageService systemManageService;

    //获取系统参数列表
    @ApiOperation(value = "获取系统列表",notes = "get")
    @ResponseBody
    @RequestMapping(method = RequestMethod.GET)
    public String getAll(@RequestParam(value="page")Integer page){
        return systemManageService.pageList(page);
    }

    @ApiOperation(value = "新增系统参数",notes = "get")
    @ResponseBody
    @RequestMapping(method = RequestMethod.POST)
    public String addOne(@RequestParam(value="para_name")String para_name,
                         @RequestParam(value="key_name")String key_name,
                         @RequestParam(value="key_value")Integer key_value) {

        QueryWrapper<SystemManage> queryWrapper = new QueryWrapper<>();
        queryWrapper
                .eq("para_name", para_name)
                .eq("is_delete",0);
        int count = systemManageService.count(queryWrapper);
        if(count > 0){
            return ResultUtil.error("参数名称重复！");
        }
        QueryWrapper<SystemManage> queryName = new QueryWrapper<>();
        queryName
                .eq("key_name", key_name)
                .eq("is_delete",0);
        int countName = systemManageService.count(queryName);
        if(countName > 0){
            return ResultUtil.error("关键字重复！");
        }
        if(key_value == null){
            return ResultUtil.error("值为空！");
        }

        SystemManage systemManage = new SystemManage();
        systemManage.setParaName(para_name);
        systemManage.setKeyName(key_name);
        systemManage.setKeyValue(key_value);
        systemManage.setIsDelete(0);
        systemManageService.saveOrUpdate(systemManage);
        return ResultUtil.success();
    }

    @ApiOperation(value = "判断是否重复",notes = "get")
    @ResponseBody
    @RequestMapping(value = "/isRepeat",method = RequestMethod.POST)
    public int isRepeat(
                         @RequestParam(value="key_name")String key_name
                       ) {

        QueryWrapper<SystemManage> queryWrapper = new QueryWrapper<>();
        queryWrapper
                .eq("key_name", key_name)
                .eq("is_delete",0);
        int count = systemManageService.count(queryWrapper);
        if(count > 0){
            return 0;
        }
        else
            return 1;

    }

    @ApiOperation(value = "编辑系统参数",notes = "get")
    @ResponseBody
    @RequestMapping(method = RequestMethod.PATCH)
    public String update(
                        @RequestParam(value="para_name",required = false)String para_name,
                         @RequestParam(value="key_name",required = false)String key_name,
                         @RequestParam(value="key_value",required = false)Integer key_value) {
        System.out.println("编辑");

       QueryWrapper<SystemManage> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("para_name", para_name)
                .eq("is_delete", 0);
//        int count = systemManageService.count(queryWrapper);
//        if (count > 0) {
//            return ResultUtil.error("该参数名称已存在!");
//        }
//        QueryWrapper<SystemManage> queryName = new QueryWrapper<>();
//        queryName
//                .eq("key_name", key_name)
//                .eq("is_delete", 0);
//        int countName = systemManageService.count(queryName);
//        if (countName > 0) {
//            return ResultUtil.error("该关键字已存在！");
//        }
        SystemManage systemManage = new SystemManage();
//        if (para_name!=null)
            systemManage.setParaName(para_name);
//        if(key_name!=null)
           systemManage.setKeyName(key_name);
        if(key_value!=null)
            systemManage.setKeyValue(key_value);
       // systemManage.setIsDelete(0);
        systemManageService.update(systemManage,queryWrapper);
        return ResultUtil.success();
    }

    @ApiOperation(value = "删除单条系统参数",notes = "get")
    @ResponseBody
    @RequestMapping(method = RequestMethod.DELETE)
    public String delete(@RequestParam(value="key_name")String key_name) {
        SystemManage systemManage = new SystemManage();
        QueryWrapper<SystemManage> queryName = new QueryWrapper<>();
        queryName
                .eq("key_name", key_name)
                .eq("is_delete", 0);
        systemManage.setIsDelete(1);
        int count = systemManageService.count(queryName);
        if(count == 0)
            return ResultUtil.error("删除失败");
        systemManageService.update(systemManage,queryName);
        return ResultUtil.success();
    }

}

