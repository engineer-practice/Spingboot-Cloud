package com.example.dcloud.service;

import com.example.dcloud.entity.SystemManage;
import com.baomidou.mybatisplus.extension.service.IService;


public interface SystemManageService extends IService<SystemManage> {
    String pageList(Integer cur_page);

}
