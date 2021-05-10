package com.example.dcloud.service;

import com.example.dcloud.entity.Course;
import com.baomidou.mybatisplus.extension.service.IService;


public interface CourseService extends IService<Course> {
    String jsonArraySort(String jsonArrStr,String sortKey);
    String searchArray(String jsonArr,String search);
    String searchLesson(String jsonArr,String search);
    String getRank(String jsonArr,String email);
}
