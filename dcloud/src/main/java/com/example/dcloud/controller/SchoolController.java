package com.example.dcloud.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.dcloud.common.ServerResponse;
import com.example.dcloud.entity.School;
import com.example.dcloud.service.SchoolService;
import com.example.dcloud.util.ResultUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.stereotype.Controller;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

import static java.awt.SystemColor.menu;
import static java.lang.Integer.parseInt;
@Api(tags = "学校管理接口")
@CrossOrigin
@Controller
@RequestMapping("/schools")
public class SchoolController {
    @Autowired
    private SchoolService schoolService;

   // 新增
    @ResponseBody
    @RequestMapping(method = RequestMethod.POST)
    @ApiOperation(value = "新增学校或学院",notes = "get")
    public String add(@RequestBody JSONObject jsonObject) {
        Map map = JSON.toJavaObject(jsonObject, Map.class);
        //判断学校编码是否重复
        QueryWrapper<School> queryWrapper = new QueryWrapper();
        queryWrapper.eq("code",map.get("code").toString())
                .eq("is_delete",0);
        int count = schoolService.count(queryWrapper);
        if(count > 0){
            return ResultUtil.error("编码不能重复！");
        }
        //判断学校名称是否重复
        QueryWrapper<School> queryName = new QueryWrapper();
        queryName.eq("name",map.get("name").toString())
                 .eq("is_delete",0);
        int count1 = schoolService.count(queryName);
        if(count1 > 0){
            return ResultUtil.error("学校或学院名称不能重复！");
        }
        School school = new School();
        school.setCode(map.get("code").toString());
        school.setName(map.get("name").toString());
        //保存父级机构
        school.setParentId(map.get("parent_id").toString());
        school.setIsDelete(0);
        schoolService.save(school);
        return ResultUtil.success();
    }
    //编辑
    @ResponseBody
    @RequestMapping(method = RequestMethod.PATCH)
    @ApiOperation(value = "编辑学校或学院",notes = "get")
    public String edit(@RequestBody JSONObject jsonObject) {
        Map map = JSON.toJavaObject(jsonObject, Map.class);
        School school = new School();
        //判断是否重复
        QueryWrapper<School> queryWrapper = new QueryWrapper();
        queryWrapper.eq("code",map.get("name").toString())
                    .eq("is_delete",0)
                    .ne("id",parseInt(map.get("id").toString()));
        int count = schoolService.count(queryWrapper);
        if(count > 0){
            return ResultUtil.error("该名称已存在！");
        }
        school.setId(parseInt(map.get("id").toString()));
        school.setName(map.get("name").toString());
        //根据id更新值
        schoolService.updateById(school);
        return ResultUtil.success();
    }
    //删除 传ids
    @ResponseBody
    @RequestMapping(method = RequestMethod.DELETE)
    @ApiOperation(value = "删除学校或学院",notes = "get")
    public String delByIds(@RequestParam(value = "ids") List list) {
        boolean flag = false;
        for(int i = 0; i < list.size(); i++){
            QueryWrapper<School> queryWrapper = new QueryWrapper();
            //判断是否有父级机构
            queryWrapper.eq("parent_id",list.get(i))
                        .eq("is_delete",0);
            int count = schoolService.count(queryWrapper);
            if(count > 0){
                flag = true;
            }else{
                School school = new School();
                school.setId(parseInt(list.get(i).toString()));
                //软删除
                school.setIsDelete(1);
                schoolService.updateById(school);
            }
        }
        if(flag){
            return ResultUtil.error("选中的节点中存在父节点，请先删除他们的子节点！");
        }else{
            return ResultUtil.success();
        }
    }

    @ResponseBody
    @RequestMapping(value = "/getCode",method = RequestMethod.GET)
    @ApiOperation(value = "获取学校名称",notes = "get")
    public String getCode(@RequestParam(value="code",required = false)String code){
        if(code == null || code.equals("0")){
            return "未设置";
        }
        QueryWrapper<School> queryWrapper = new QueryWrapper();
        queryWrapper.eq("code",code)
                    .eq("is_delete",0);
        School school = schoolService.getOne(queryWrapper);
        return school.getName();
    }
    @ResponseBody
    @ApiOperation(value = "获取学校列表",notes = "get")
    @RequestMapping(value = "/getSchools",method = RequestMethod.GET)
    public ServerResponse<School> getSchools(){
        return schoolService.getSchools1();
    }
    @ResponseBody
    @ApiOperation(value = "获取学校的代码获取该学校的学院列表",notes = "get")
    @RequestMapping(value = "/getAcademies",method = RequestMethod.GET)
    public ServerResponse<School> Academies(@RequestParam(value="schoolCode")String schoolCode){
        return schoolService.getAcademiesByCode1(schoolCode);
    }
    @ResponseBody
    @RequestMapping(method = RequestMethod.GET)
    @ApiOperation(value = "获取学校、学院列表",notes = "get")
    public String getlist(
            @RequestParam(value="page",required = false)Integer page,
            @RequestParam(value="name",required = false)String name,
            @RequestParam(value="id",required = false)Integer id,
            @RequestParam(value="info",required = false)Integer info,
            @RequestParam(value="school",required = false)Integer school,
            @RequestParam(value="schoolCode",required = false)String schoolCode,
            @RequestParam(value="academy",required = false)Integer parentId
    ){
        //获取学院列表
        if(id != null){
            info = 0;
            return schoolService.getChildList(page,id,info);
            //根据学校名称查询
        }else if(name != null){
            return schoolService.queryList(page,name);
            //分页获取列表
        }else if(page != null){
            return schoolService.queryListforAll(page);
        }else if(info != null) {
            return schoolService.getAll(info);
            //获取学校列表
        }else if(school != null) {
           return schoolService.getSchools();
           // return "0";
            //获取学院列表
        }else if(schoolCode !=null){
          // return "0";
             return schoolService.getAcademiesByCode(schoolCode);
        }else if(parentId != null){
            return schoolService.getAcademies(parentId);
        }else{
                info = 0;
                return schoolService.getAll(info);
        }
    }
}

