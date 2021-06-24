package com.example.dcloud.service;

import com.example.dcloud.entity.CourseManage;
import com.baomidou.mybatisplus.extension.service.IService;

public interface CourseManageService extends IService<CourseManage> {
    //搜索课程
    String pageListforQuery(String name,Integer page);
    //返回某页
    String pageList(Integer page);
    //返回所有
    String getCourseName();

}
