package com.example.dcloud.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.dcloud.entity.Menu;
import com.example.dcloud.service.MenuService;
import com.example.dcloud.util.ResultUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.stereotype.Controller;

import java.util.*;

import static java.lang.Integer.parseInt;
import static java.lang.Long.parseLong;

@Api(tags = "菜单管理接口")
@Controller
@RequestMapping("/menus")
public class MenuController {
    @Autowired
    private MenuService menuService;
    @ResponseBody
    @RequestMapping(method = RequestMethod.GET)
    @ApiOperation(value = "获取菜单列表",notes = "get")
    public String getList(@RequestParam(value="id",required = false)Integer id,
                          @RequestParam(value="name",required = false)String name,
                          @RequestParam(value="page",required = false)Integer page,
                          @RequestParam(value="parent",required = false)Integer parent,
                          @RequestParam(value="is_visible",required = false)Integer is_visible){
        if(id!=null){
            //返回菜单详情，根据id查
            return JSON.toJSONString(menuService.getById(id));
        }else if(name!=null){
            //查找菜单（搜索）
            return menuService.queryList(name,page,is_visible);
        }else if(page!=null){
            //获取菜单列表
            return menuService.pageList(page);
        }else if(parent != null) {
            //获取其子菜单
            Map<String, Object> columnMap = new HashMap<>();
            //查找没被删除的
            columnMap.put("is_deleted",0);
            Collection<Map<String,Object>> collection = menuService.listMaps();
            List<String> parent1 = new ArrayList<>();
            Iterator<Map<String,Object>> it = collection.iterator();
            //遍历
            while (it.hasNext()) {
                Map<String,Object> s = it.next();
                if(s.get("isDeleted").equals(0)){
                    parent1.add(s.get("name").toString());
                }
            }
            return JSON.toJSONString(parent1);
        }else{
            //获取所有菜单
            return menuService.getAll();
        }
    }
    @ResponseBody
    @RequestMapping(method = RequestMethod.PATCH)
    @ApiOperation(value = "编辑菜单",notes = "get")
    public String updateOne(@RequestBody JSONObject jsonObject){
        Map map = JSON.toJavaObject(jsonObject, Map.class);
        Menu menu = new Menu();
        //如果没有父级菜单，parentid设为0
        if(map.get("parent_name").toString().equals("无")){
            int a = 0;
            long b = (int)a;
            menu.setParentId(b);
        }else{
            //根据父级菜单名字查找parentid
            QueryWrapper<Menu> queryWrapper1 = new QueryWrapper();
            queryWrapper1.eq("name",map.get("parent_name").toString())
                    .eq("is_deleted",0);
            Menu menu1 = menuService.getOne(queryWrapper1);
            menu.setParentId(menu1.getId());
        }
        //更新
        menu.setId(parseLong(map.get("id").toString()));
        menu.setName(map.get("name").toString());
        menu.setIcon(map.get("icon").toString());
        menu.setIsPage(parseInt(map.get("is_page").toString()));
        menu.setUrl(map.get("url").toString());
        menu.setIsVisible(parseInt(map.get("is_visible").toString()));
        menu.setIsMenu(parseInt(map.get("is_menu").toString()));
        menu.setMenuOrder(parseInt(map.get("menu_order").toString()));
        //为按钮
        menu.setType(1);
        menu.setIsDeleted(0);
        //根据id更新菜单
        menuService.updateById(menu);
        return ResultUtil.success();
    }
    @ResponseBody
    @RequestMapping(method = RequestMethod.POST)
    @ApiOperation(value = "新增一个菜单",notes = "get")
    public String saveOne(@RequestBody JSONObject jsonObject){
        Map map = JSON.toJavaObject(jsonObject, Map.class);
        Menu menu = new Menu();
        if(map.containsKey("id")){
            //如果有父级菜单，则指向父级菜单
            menu.setParentId(parseLong(map.get("id").toString()));
        }
        //查询是否有重复的菜单名称
        QueryWrapper<Menu> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("name",map.get("name").toString())
                    .eq("is_deleted",0);
        int count = menuService.count(queryWrapper);
        if(count > 0){
            return ResultUtil.error("菜单重复！");
        }
        //查找父级菜单，如果有父级菜单设置parentid，没有则设置0
        QueryWrapper<Menu> queryWrapper1 = new QueryWrapper();
        queryWrapper1.eq("name",map.get("parent_name").toString())
                     .eq("is_deleted",0);
        Menu menu1 = menuService.getOne(queryWrapper1);
        if(menu1 == null){
            menu.setParentId(parseLong("0"));
        }else{
            menu.setParentId(menu1.getId());
        }
        //相关的信息
        menu.setName(map.get("name").toString());
        menu.setIcon(map.get("icon").toString());
        menu.setIsPage(parseInt(map.get("is_page").toString()));
        menu.setUrl(map.get("url").toString());
        menu.setIsVisible(parseInt(map.get("is_visible").toString()));
        menu.setIsMenu(parseInt(map.get("is_menu").toString()));
        menu.setMenuOrder(parseInt(map.get("menu_order").toString()));
        menu.setMenuLevel(parseInt(map.get("menu_level").toString()));
        //表示按钮
        menu.setType(1);
        //启用
        menu.setIsDeleted(0);
        menuService.save(menu);
        return ResultUtil.success();
    }
    @ResponseBody
    @RequestMapping(method = RequestMethod.DELETE)
    @ApiOperation(value = "删除菜单",notes = "get")
    public String delMenu(@RequestParam(value = "ids") List list) {
        for(int i = 0; i < list.size(); i++){
            Menu menu = new Menu();
            menu.setId(parseLong(list.get(i).toString()));
            //软删除
            menu.setIsDeleted(1);
            menuService.updateById(menu);
        }
        return ResultUtil.success();
    }

}

