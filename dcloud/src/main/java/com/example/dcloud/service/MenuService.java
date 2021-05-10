package com.example.dcloud.service;

import com.example.dcloud.entity.Menu;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service
public interface MenuService extends IService<Menu> {
    String pageList(int cur_page);
    String queryList(String name,Integer page,Integer is_visible);
    String getAll();
}
