package com.example.dcloud.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.dcloud.entity.Menu;
import com.example.dcloud.service.MenuService;
import com.example.dcloud.util.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import org.springframework.stereotype.Controller;

import java.util.*;

import static java.lang.Integer.parseInt;
import static java.lang.Long.parseLong;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author fifteen
 * @since 2020-04-01
 */
@Controller
@RequestMapping("/menus")
public class MenuController {
    @Autowired
    private  MenuService menuService;
    /**
     * 返回菜单列表，查询，详情，上级菜单
     * @return
     */
    @ResponseBody
    @RequestMapping(method = RequestMethod.GET)
    public String getList(@RequestParam(value="id",required = false)Integer id,
                          @RequestParam(value="name",required = false)String name,
                          @RequestParam(value="page",required = false)Integer page,
                          @RequestParam(value="parent",required = false)Integer parent,
                          @RequestParam(value="is_visible",required = false)Integer is_visible){
        if(id!=null){//单条菜单详情
            return JSON.toJSONString(menuService.getById(id));
        }else if(name!=null){//查询
            return menuService.queryList(name,page,is_visible);
        }else if(page!=null){//列表
            return menuService.pageList(page);
        }else if(parent != null) {
            //上级菜单选择 显示所有菜单名字
            Map<String, Object> columnMap = new HashMap<>();
            columnMap.put("is_deleted",0);
            Collection<Map<String,Object>> collection = menuService.listMaps();
            List<String> parent1 = new ArrayList<>();
            Iterator<Map<String,Object>> it = collection.iterator();
            while (it.hasNext()) {
                Map<String,Object> s = it.next();
                if(s.get("isDeleted").equals(0)){
                    parent1.add(s.get("name").toString());
                }
            }
            return JSON.toJSONString(parent1);
        }else{//侧边菜单栏 含children
            return menuService.getAll();
        }
    }

    /**
     * 新增菜单
     * @param jsonObject
     * @return
     */
    @ResponseBody
    @RequestMapping(method = RequestMethod.POST)
    public String saveOne(@RequestBody JSONObject jsonObject){
        Map map = JSON.toJavaObject(jsonObject, Map.class);
        Menu menu = new Menu();
        if(map.containsKey("id")){//编辑
            menu.setParentId(parseLong(map.get("id").toString()));
        }
        //名称不能重复
        QueryWrapper<Menu> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("name",map.get("name").toString())
                    .eq("is_deleted",0);
        int count = menuService.count(queryWrapper);
        if(count > 0){
            return ResultUtil.error("菜单名称不允许重复！");
        }
        QueryWrapper<Menu> queryWrapper1 = new QueryWrapper();
        queryWrapper1.eq("name",map.get("parent_name").toString())
                     .eq("is_deleted",0);
        Menu menu1 = menuService.getOne(queryWrapper1);
        if(menu1 == null){
            menu.setParentId(parseLong("0"));
        }else{
            menu.setParentId(menu1.getId());
        }
        menu.setName(map.get("name").toString());
        menu.setIcon(map.get("icon").toString());
        menu.setIsPage(parseInt(map.get("is_page").toString()));
        menu.setUrl(map.get("url").toString());
        menu.setIsVisible(parseInt(map.get("is_visible").toString()));
        menu.setIsMenu(parseInt(map.get("is_menu").toString()));
        menu.setMenuOrder(parseInt(map.get("menu_order").toString()));
        menu.setMenuLevel(parseInt(map.get("menu_level").toString()));
        menu.setType(1);
        menu.setIsDeleted(0);
        menuService.save(menu);
        return ResultUtil.success();
    }

    /**
     * 编辑菜单
     * @param jsonObject
     * @return
     */
    @ResponseBody
    @RequestMapping(method = RequestMethod.PATCH)
    public String uodateOne(@RequestBody JSONObject jsonObject){
        Map map = JSON.toJavaObject(jsonObject, Map.class);
        Menu menu = new Menu();
        if(map.get("parent_name").toString().equals("无")){
            int a = 0;
            long b = (int)a;
            menu.setParentId(b);
        }else{
            QueryWrapper<Menu> queryWrapper1 = new QueryWrapper();
            queryWrapper1.eq("name",map.get("parent_name").toString())
                         .eq("is_deleted",0);
            Menu menu1 = menuService.getOne(queryWrapper1);
            menu.setParentId(menu1.getId());
        }
        menu.setId(parseLong(map.get("id").toString()));
        menu.setName(map.get("name").toString());
        menu.setIcon(map.get("icon").toString());
        menu.setIsPage(parseInt(map.get("is_page").toString()));
        menu.setUrl(map.get("url").toString());
        menu.setIsVisible(parseInt(map.get("is_visible").toString()));
        menu.setIsMenu(parseInt(map.get("is_menu").toString()));
        menu.setMenuOrder(parseInt(map.get("menu_order").toString()));
        menu.setType(1);
        menu.setIsDeleted(0);
        menuService.updateById(menu);
        return ResultUtil.success();
    }

    /**
     * 删除菜单
     * @param list
     * @return
     */
    @ResponseBody
    @RequestMapping(method = RequestMethod.DELETE)
    public String delMenu(@RequestParam(value = "ids") List list) {
        for(int i = 0; i < list.size(); i++){
            Menu menu = new Menu();
            menu.setId(parseLong(list.get(i).toString()));
            menu.setIsDeleted(1);
            menuService.updateById(menu);
        }
        return ResultUtil.success();
    }

}

