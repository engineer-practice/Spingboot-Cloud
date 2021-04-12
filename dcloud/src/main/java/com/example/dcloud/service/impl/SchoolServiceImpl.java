package com.example.dcloud.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author fifteen
 * @since 2020-05-11
 */
@Service
public class SchoolServiceImpl extends ServiceImpl<SchoolMapper, School> implements SchoolService {

    public static List<String>  listIds = new ArrayList<>();//孩子id列表
    @Autowired
    private SchoolMapper schoolMapper;

    @Override
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
    public String queryListforAll(Integer page) {
        QueryWrapper<School> queryWrapper =  new QueryWrapper<>();
        queryWrapper.eq("is_delete", 0);
        Page<School> page1 = new Page<>(page,10);  // 查询第page页，每页返回10条
        IPage<School> iPage = schoolMapper.selectPage(page1,queryWrapper);
        return JSON.toJSONString(iPage);
    }

    @Override
    public String getChildList(Integer page, Integer id,Integer info) {
        List<Map<Object,String>> list = getAllSchool(info);
        //遍历所有children的id，进行查询
        List<String> ids = getChildrenIds(id.toString());
        QueryWrapper<School> queryWrapper =  new QueryWrapper<>();
        queryWrapper.in("id",ids);
//        queryWrapper.lambda().eq(School::getIsDelete, 0).and(
//                queryWrapper1 -> queryWrapper1.eq(School::getName,school_name)
//                        .or().eq(School::getName,academy_name));
        Page<School> page1 = new Page<>(page,10);  // 查询第page页，每页返回10条
        IPage<School> iPage = schoolMapper.selectPage(page1,queryWrapper);
        return JSON.toJSONString(iPage);
//        return JSON.toJSONString(ids);
    }

    @Override
    public String getAll(Integer info) {
        List result = getAllSchool(info);
        return JSON.toJSONString(result);
    }

    @Override
    public String getAcademies(Integer parentId) {
        QueryWrapper<School> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_delete",0);
        queryWrapper.eq("parent_id",parentId);
        return JSON.toJSONString(schoolMapper.selectList(queryWrapper));
    }

    @Override
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

    @Override
    public String getSchools() {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("is_delete",0);
        queryWrapper.eq("parent_id",0);
        return JSON.toJSONString(schoolMapper.selectList(queryWrapper));
    }

    public List<String> getChildrenIds(String id){
        List<String> parentIds = new ArrayList<>();
        parentIds.add(id);
        List tempList = new ArrayList();
        List<Map<Object,String>> retultList = new ArrayList();
        QueryWrapper<School> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_delete",0);
        List<School> list = schoolMapper.selectList(queryWrapper);

        for (School school : list) {
            Map tempMap = new HashMap();
            tempMap.put("id", school.getId());
            tempMap.put("code", school.getCode());
            tempMap.put("label", school.getName());
            tempMap.put("parent_id",school.getParentId());
            tempList.add(tempMap);
        }

        for (int i = 0; i < tempList.size(); i++) {
            boolean flag = false;
            Map temp = (Map) tempList.get(i);
            for(int k = 0; k < parentIds.size(); k++){
                if(parentIds.get(k).toString().equals(temp.get("id").toString())){
                    flag = true;
                }
            }
            if(flag){//找父亲列表中的孩子就好
                for (int j = 0; j < tempList.size(); j++) {
                    Map temp2 = (Map) tempList.get(j);
                    if (temp.get("id").toString().equals(temp2.get("parent_id").toString())) {
                        //temp是temp2的孩子，将temp的id加入parentids列表
                        parentIds.add(temp2.get("id").toString());
                    }
                }
            }

        }
        return parentIds;
    }
    public List<Map<Object,String>> getAllSchool(Integer info){
        List tempList = new ArrayList();
        List<Map<Object,String>> retultList = new ArrayList();
        QueryWrapper<School> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_delete",0);
        List<School> list = schoolMapper.selectList(queryWrapper);

        for (School school : list) {
            Map tempMap = new HashMap();
            tempMap.put("id", school.getId());
            if(info == 1){
                tempMap.put("value", school.getCode());
            }else{
                tempMap.put("code", school.getCode());
            }
            tempMap.put("label", school.getName());
            tempMap.put("parent_id",school.getParentId());
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
                retultList.add(temp);
            }
            temp.remove("parent_id");
            if(info == 1){
                temp.remove("id");
            }
        }
        return retultList;
    }
}
