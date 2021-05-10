package com.example.dcloud.service;

import com.example.dcloud.entity.CourseManage;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author fifteen
 * @since 2020-06-18
 */
public interface CourseManageService extends IService<CourseManage> {
    String pageListforQuery(String name,Integer page);
    String pageList(Integer page);
    String getCourseName();

}
