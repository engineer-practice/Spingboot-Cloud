package com.example.dcloud.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.dcloud.entity.CourseManage;
import com.example.dcloud.mapper.CourseManageMapper;
import com.example.dcloud.service.CourseManageService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class CourseManageServiceImpl extends ServiceImpl<CourseManageMapper, CourseManage> implements CourseManageService {

    @Resource
    private CourseManageMapper courseManageMapper;
    @Override
    public String pageListforQuery(String name, Integer page) {
        //根据课程名搜索课程
        QueryWrapper<CourseManage> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_deleted",0);
        queryWrapper.like("name",name);
        Page<CourseManage> page1 = new Page<>(page,10);
        IPage<CourseManage> iPage = courseManageMapper.selectPage(page1,queryWrapper);
        return JSON.toJSONString(iPage);
    }

    @Override
    public String pageList(Integer cur_page) {
        QueryWrapper<CourseManage> queryWrapper =  new QueryWrapper<>();
        queryWrapper
                .eq("is_deleted",0)
                .orderByAsc("id");
        Page<CourseManage> page = new Page<>(cur_page,10);  // 查询第1页，每页返回10条
        IPage<CourseManage> iPage = courseManageMapper.selectPage(page,queryWrapper);
        return JSON.toJSONString(iPage);
    }

    @Override
    public String getCourseName() {
        QueryWrapper<CourseManage> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(CourseManage::getIsDeleted,0);
        List<CourseManage> list = list(queryWrapper);
        return JSON.toJSONString(list);
    }
}
