package com.example.dcloud.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.dcloud.entity.School;
import com.example.dcloud.service.SchoolService;
import com.example.dcloud.util.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.stereotype.Controller;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

import static java.awt.SystemColor.menu;
import static java.lang.Integer.parseInt;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author fifteen
 * @since 2020-05-11
 */
@CrossOrigin
@Controller
@RequestMapping("/schools")
public class SchoolController {
    @Autowired
    private SchoolService schoolService;

    //新增
    @ResponseBody
    @RequestMapping(method = RequestMethod.POST)
    public String add(@RequestBody JSONObject jsonObject) {
        Map map = JSON.toJavaObject(jsonObject, Map.class);
        //code不能重复
        QueryWrapper<School> queryCode = new QueryWrapper();
        queryCode.eq("code",map.get("code").toString())
                .eq("is_delete",0);
        int count1 = schoolService.count(queryCode);
        if(count1 > 0){
            return ResultUtil.error("编码不能重复！");
        }
        //name不能重复
        QueryWrapper<School> queryName = new QueryWrapper();
        queryName.eq("name",map.get("name").toString())
                 .eq("is_delete",0);
        int count2 = schoolService.count(queryName);
        if(count2 > 0){
            return ResultUtil.error("学校或学院名称不能重复！");
        }
        School school = new School();
        school.setCode(map.get("code").toString());
        school.setName(map.get("name").toString());
        school.setParentId(map.get("parent_id").toString());
        school.setIsDelete(0);
        schoolService.save(school);
        return ResultUtil.success();
    }
    //编辑
    @ResponseBody
    @RequestMapping(method = RequestMethod.PATCH)
    public String edit(@RequestBody JSONObject jsonObject) {
        Map map = JSON.toJavaObject(jsonObject, Map.class);
        School school = new School();
        //名字不能跟数据库中的重复
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
        schoolService.updateById(school);
        return ResultUtil.success();
    }
    //删除 传ids
    @ResponseBody
    @RequestMapping(method = RequestMethod.DELETE)
    public String delByIds(@RequestParam(value = "ids") List list) {
        boolean flag = false;
        for(int i = 0; i < list.size(); i++){
            QueryWrapper<School> queryWrapper = new QueryWrapper();
            queryWrapper.eq("parent_id",list.get(i))
                        .eq("is_delete",0);
            int count = schoolService.count(queryWrapper);
            if(count > 0){//说明该id作为父亲
                flag = true;
            }else{
                School school = new School();
                school.setId(parseInt(list.get(i).toString()));
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
    public String getCode(@RequestParam(value="code",required = false)String code){
        if(code == null || code.equals("0")){
            return "未设置";
        }
        QueryWrapper<School> queryWrapper = new QueryWrapper();
        queryWrapper.eq("code",code)
                    .eq("is_delete",0);
        School school1 = schoolService.getOne(queryWrapper);
        return school1.getName();
    }
    //查询（学校，学院）
    @ResponseBody
    @RequestMapping(method = RequestMethod.GET)
    public String getlist(
            @RequestParam(value="page",required = false)Integer page,
            @RequestParam(value="name",required = false)String name,
            @RequestParam(value="id",required = false)Integer id,
            @RequestParam(value="info",required = false)Integer info,
            @RequestParam(value="school",required = false)Integer school,
            @RequestParam(value="schoolCode",required = false)String schoolCode,
            @RequestParam(value="academy",required = false)Integer parentId
    ){
        if(id != null){//获取右侧展开列表
            info = 0;
            return schoolService.getChildList(page,id,info);
        }else if(name != null){//查询
            return schoolService.queryList(page,name);
        }else if(page != null){//右侧所有列表
            return schoolService.queryListforAll(page);
        }else if(info != null) {
            return schoolService.getAll(info);
        }else if(school != null) {//获取学校列表
            return schoolService.getSchools();
        }else if(schoolCode !=null){//获取学院列表
            return schoolService.getAcademiesByCode(schoolCode);
        }else if(parentId != null){
            return schoolService.getAcademies(parentId);
        }else{//获取树 含children的列表
                info = 0;
                return schoolService.getAll(info);
        }

    }



}

