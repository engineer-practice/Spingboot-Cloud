package com.example.dcloud.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.dcloud.entity.Dictionary;
import com.example.dcloud.entity.Menu;
import com.example.dcloud.mapper.MenuMapper;
import com.example.dcloud.service.MenuService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sun.javafx.collections.MappingChange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;

import static java.lang.Integer.parseInt;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author fifteen
 * @since 2020-04-01
 */
@Service
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements MenuService {
    @Autowired
    private MenuMapper menuMapper;

    @Override
    public String pageList(int cur_page) {
        QueryWrapper<Menu> queryWrapper =  new QueryWrapper<>();
        queryWrapper
                .eq("is_deleted",0)
                .orderByAsc("id");
        Page<Menu> page = new Page<>(cur_page,10);  // 查询第1页，每页返回10条
        IPage<Menu> iPage = menuMapper.selectPage(page,queryWrapper);
        return JSON.toJSONString(iPage);
    }

    @Override
    public String queryList(String name,Integer page,Integer is_visible) {
        QueryWrapper<Menu> queryWrapper =  new QueryWrapper<>();
        if(is_visible == null){
            queryWrapper.eq("is_deleted",0)
                    .like("name",name);
        }else{
            queryWrapper.eq("is_deleted",0)
                    .eq("is_visible",is_visible)
                    .like("name",name);
        }

//        queryWrapper.lambda().eq(Menu::getIsDeleted, 0).and(
//                queryWrapper1 -> queryWrapper1.eq(Menu::getIsVisible,is_visible)
//                        .like(Menu::getName,name));
        Page<Menu> page1 = new Page<>(page,10);  // 查询第1页，每页返回10条
        IPage<Menu> iPage = menuMapper.selectPage(page1,queryWrapper);
        return JSON.toJSONString(iPage);
    }
    @Override
    public String getAll() {
        List result = getAllMenu();
        return JSON.toJSONString(result);
    }

    public List<Map<String,Object>> getAllMenu(){
        List tempList = new ArrayList();
        List<Map<String,Object>> retultList = new ArrayList();
        QueryWrapper<Menu> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_deleted",0);
        List<Menu> list = menuMapper.selectList(queryWrapper);

        for (Menu menu : list) {
            Map tempMap = new HashMap();
            tempMap.put("id", menu.getId());
            tempMap.put("icon", menu.getIcon());
            tempMap.put("is_menu", menu.getIsMenu());
            tempMap.put("is_page", menu.getIsPage());
            tempMap.put("is_visible", menu.getIsVisible());
            tempMap.put("menu_level", menu.getMenuLevel());
            tempMap.put("menu_order", menu.getMenuOrder());
            tempMap.put("url", menu.getUrl());
            tempMap.put("name", menu.getName());
            tempMap.put("parent_id",menu.getParentId());
            tempList.add(tempMap);
        }

        for (int i = 0; i < tempList.size(); i++) {
            Map temp = (Map) tempList.get(i);
            if (!("0".equals(temp.get("parent_id").toString()))) {
                for (int j = 0; j < tempList.size(); j++) {
                    Map temp2 = (Map) tempList.get(j);
                    if (temp2.get("id").toString().equals(temp.get("parent_id").toString())) {
                        if (temp2.get("children") == null) {
                            List children = new ArrayList();
                            children.add(temp);
                            temp2.put("children", children);
                        } else {
                            List children = (List) temp2.get("children");
                            children.add(temp);
                            temp2.put("children", children);
                        }
                        break;
                    }
                }
            }
        }
        for (int i = 0; i < tempList.size(); i++) {
            Map temp = (Map) tempList.get(i);
            if ("0".equals(temp.get("parent_id").toString())) {
                if(temp.get("name").toString().equals("首页")){
                    temp.put("roles","common");
                }else if(temp.get("name").toString().equals("系统管理")){
                    temp.put("roles","common");
                }else if(temp.get("name").toString().equals("学校管理")) {
                    temp.put("roles","common");
                }else if(temp.get("name").toString().equals("班课管理")) {
                    temp.put("roles","common");
                }else if(temp.get("name").toString().equals("用户管理")) {
                    temp.put("roles","admin");
                }else{
                        temp.put("roles","superAdmin");
                }
                retultList.add(temp);
            }
            temp.remove("parent_id");
        }
        sortList(retultList);
        return retultList;
    }
    public List<Map<String,Object>> sortList(List<Map<String,Object>> list){
        //排序
        Collections.sort(list, new Comparator<Map<String, Object>>() {
            @Override
            public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                Integer name1 = Integer.valueOf(o1.get("menu_order").toString()) ;//name1是从你list里面拿出来的一个
                Integer name2 = Integer.valueOf(o2.get("menu_order").toString()) ; //name2是从你list里面拿出来的第二个name
                return name1.compareTo(name2);
            }
        });
        return list;
    }

}
