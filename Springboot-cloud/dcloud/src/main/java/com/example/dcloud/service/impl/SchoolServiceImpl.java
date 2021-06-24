package com.example.dcloud.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.dcloud.common.ServerResponse;
import com.example.dcloud.entity.School;
import com.example.dcloud.mapper.SchoolMapper;
import com.example.dcloud.service.SchoolService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class SchoolServiceImpl extends ServiceImpl<SchoolMapper, School> implements SchoolService {

    public static List<String>  listIds = new ArrayList<>();
    @Resource
    private SchoolMapper schoolMapper;
    @Override
    //获取学校列表
    public String queryList(Integer page, String name) {
        QueryWrapper<School> queryWrapper =  new QueryWrapper<>();
            queryWrapper.lambda().eq(School::getIsDelete, 0).and(
                    queryWrapper1 -> queryWrapper1.like(School::getName,name)
                            .or().like(School::getName,name));
        Page<School> page1 = new Page<>(page,10);  // 查询第page页，每页返回10条
        IPage<School> iPage = schoolMapper.selectPage(page1,queryWrapper);
        return JSON.toJSONString(iPage);
    }
    @Override
    public ServerResponse<School> getAcademiesByCode1(String schoolCode) {
        //通过父级code找到父级Id
        ServerResponse<School> response = new ServerResponse<>();
        QueryWrapper<School> parentQuery = new QueryWrapper();
        parentQuery.eq("code",schoolCode);
        School school = schoolMapper.selectOne(parentQuery);
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("is_delete",0);
        queryWrapper.eq("parent_id",school.getId());
        List<School> schools = schoolMapper.selectList(queryWrapper);
        response.setDataList(schools);
        response.setTotal((long)schools.size());
        response.setMsg("查询成功！");
        response.setResult(true);
        return response;
    }

    @Override
    public ServerResponse getSchools1() {
        ServerResponse<School> response = new ServerResponse<>();
        QueryWrapper<School> queryWrapper = new QueryWrapper();
        queryWrapper.eq("is_delete",0);
        queryWrapper.eq("parent_id",0);
        List<School> schools = schoolMapper.selectList(queryWrapper);
        response.setTotal((long)(schools.size()));
        response.setDataList(schools);
        response.setResult(true);
        response.setMsg("查询成功！");
        return response;
    }
    @Override
    //枫叶查询
    public String queryListforAll(Integer page) {
        QueryWrapper<School> queryWrapper =  new QueryWrapper<>();
        queryWrapper.eq("is_delete", 0);
        Page<School> page1 = new Page<>(page,10);
        IPage<School> iPage = schoolMapper.selectPage(page1,queryWrapper);
        return JSON.toJSONString(iPage);
    }

    @Override
    public String getChildList(Integer page, Integer id,Integer info) {
        //获取学院列表
        List<Map<Object,String>> list = getAllSchool(info);
        List<String> ids = getChildrenIds(id.toString());
        QueryWrapper<School> queryWrapper =  new QueryWrapper<>();
        queryWrapper.in("id",ids);
        Page<School> page1 = new Page<>(page,10);
        IPage<School> iPage = schoolMapper.selectPage(page1,queryWrapper);
        return JSON.toJSONString(iPage);
    }

    @Override
    public String getAll(Integer info) {
        //获取学校列表
        List result = getAllSchool(info);
        return JSON.toJSONString(result);
    }
    public String getAcademiesByCode(String schoolCode) {
        //通过父级code找到父级Id
        QueryWrapper<School> parentQuery = new QueryWrapper();
        parentQuery.eq("code",schoolCode);
        School school = schoolMapper.selectOne(parentQuery);
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("is_delete",0);
        queryWrapper.eq("parent_id",school.getId());
        return JSON.toJSONString(schoolMapper.selectList(queryWrapper));
    }
    //查学校
    @Override
    public String getSchools() {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("is_delete",0);
        queryWrapper.eq("parent_id",0);
        return JSON.toJSONString(schoolMapper.selectList(queryWrapper));
    }
    //查学院
    @Override
    public String getAcademies(Integer parentId) {
        QueryWrapper<School> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_delete",0);
        queryWrapper.eq("parent_id",parentId);
        return JSON.toJSONString(schoolMapper.selectList(queryWrapper));
    }

    //获取学院列表
    public List<String> getChildrenIds(String id){
        List<String> parentIds = new ArrayList<>();
        parentIds.add(id);
        List list1 = new ArrayList();
        List<Map<Object,String>> retultList = new ArrayList();
        QueryWrapper<School> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_delete",0);
        List<School> list = schoolMapper.selectList(queryWrapper);

        for (School school : list) {
            Map map = new HashMap();
            map.put("id", school.getId());
            map.put("code", school.getCode());
            map.put("label", school.getName());
            map.put("parent_id",school.getParentId());
            list1.add(map);
        }
        //遍历所有子节点
        for (int i = 0; i < list1.size(); i++) {
            boolean flag = false;
            Map temp = (Map) list1.get(i);
            for(int k = 0; k < parentIds.size(); k++){
                if(parentIds.get(k).toString().equals(temp.get("id").toString())){
                    flag = true;
                }
            }
            if(flag){
                for (int j = 0; j < list1.size(); j++) {
                    Map temp2 = (Map) list1.get(j);
                    if (temp.get("id").toString().equals(temp2.get("parent_id").toString())) {

                        parentIds.add(temp2.get("id").toString());
                    }
                }
            }

        }
        return parentIds;
    }
    //获取学校和学院信息
    public List<Map<Object,String>> getAllSchool(Integer info){
        List list = new ArrayList();
        List<Map<Object,String>> retultList = new ArrayList();
        QueryWrapper<School> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_delete",0);
        List<School> schoolList = schoolMapper.selectList(queryWrapper);
        for (School school : schoolList) {
            Map tempMap = new HashMap();
            tempMap.put("id", school.getId());
            if(info == 1){
                tempMap.put("value", school.getCode());
            }else{
                tempMap.put("code", school.getCode());
            }
            tempMap.put("label", school.getName());
            tempMap.put("parent_id",school.getParentId());
            list.add(tempMap);
        }
        //遍历子节点
        for (int i = 0; i < list.size(); i++) {
            Map temp = (Map) list.get(i);
            if (!("0".equals(temp.get("parent_id").toString()))) {
                for (int j = 0; j < list.size(); j++) {
                    Map temp2 = (Map) list.get(j);
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
        for (int j = 0; j < list.size(); j++) {
            Map map = (Map) list.get(j);
            if ("0".equals(map.get("parent_id").toString())) {
                retultList.add(map);
            }
            map.remove("parent_id");
            if(info == 1){
                map.remove("id");
            }
        }
        return retultList;
    }
}
