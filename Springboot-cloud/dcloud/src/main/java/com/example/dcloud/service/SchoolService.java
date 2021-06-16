package com.example.dcloud.service;

import com.example.dcloud.common.ServerResponse;
import com.example.dcloud.entity.School;
import com.baomidou.mybatisplus.extension.service.IService;


public interface SchoolService extends IService<School> {
    String queryList(Integer page,String name);
    String queryListforAll(Integer page);
    String getChildList(Integer page,Integer id,Integer info);
    String getAll(Integer info);
    String getAcademies(Integer parentId);
    ServerResponse<School> getAcademiesByCode(String schoolCode);
    ServerResponse<School> getSchools();
}
