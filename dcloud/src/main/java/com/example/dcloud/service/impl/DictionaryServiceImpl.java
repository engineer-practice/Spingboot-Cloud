package com.example.dcloud.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.dcloud.common.ServerResponse;
import com.example.dcloud.entity.Dictionary;
import com.example.dcloud.mapper.DictionaryMapper;
import com.example.dcloud.service.DictionaryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.tomcat.jni.Directory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service
public class DictionaryServiceImpl extends ServiceImpl<DictionaryMapper, Dictionary> implements DictionaryService {

    @Resource
    private DictionaryMapper dictionaryMapper;
    @Override
    public int selectDictNum(){return dictionaryMapper.selDictNum();}
    @Override
    //查询数据字典
    public List<Map> selectDictionary(int offset){return dictionaryMapper.selectDictionary(offset);}

    @Override
    //分页查询
    public String pageList(Integer cur_page) {
        QueryWrapper<Dictionary> queryWrapper =  new QueryWrapper<>();
        queryWrapper
                .eq("is_delete",0)
                .orderByDesc("id");
        Page<Dictionary> page = new Page<>(cur_page,10);
        IPage<Dictionary> iPage = dictionaryMapper.selectPage(page,queryWrapper);
        return JSON.toJSONString(iPage);
    }

    @Override
    //搜索数据字典
    public String pageListforQuery(String code,String name,Integer page) {
        QueryWrapper<Dictionary> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_delete",0);
        queryWrapper.like("code",code).like("name",name);
        Page<Dictionary> page1 = new Page<>(page,10);
        IPage<Dictionary> iPage = dictionaryMapper.selectPage(page1,queryWrapper);
        return JSON.toJSONString(iPage);
    }


}
