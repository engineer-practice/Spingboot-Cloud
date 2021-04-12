package com.example.dcloud.service;

import com.example.dcloud.entity.Dictionary;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;


public interface DictionaryService extends IService<Dictionary> {
    int selectDictNum();
    List<Map> selectDictionary(int offset);
    String pageList(Integer cur_page);
    String pageListforQuery(String code,String name,Integer page);
}
