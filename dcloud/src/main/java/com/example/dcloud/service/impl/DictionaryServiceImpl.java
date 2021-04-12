package com.example.dcloud.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.dcloud.entity.Dictionary;
import com.example.dcloud.mapper.DictionaryMapper;
import com.example.dcloud.service.DictionaryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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
    //查询
    public List<Map> selectDictionary(int offset){return dictionaryMapper.selectDictionary(offset);}

    @Override
    //也是查询 返回多页
    public String pageList(Integer cur_page) {
        QueryWrapper<Dictionary> queryWrapper =  new QueryWrapper<>();
        queryWrapper
                .eq("is_delete",0)
                .orderByDesc("id");
        Page<Dictionary> page = new Page<>(cur_page,10);  // 查询第1页，每页返回10条
        IPage<Dictionary> iPage = dictionaryMapper.selectPage(page,queryWrapper);
//        iPage.setTotal(iPage.getRecords().size());
        return JSON.toJSONString(iPage);
    }

    @Override
    //给定调节查询 返回列表
    public String pageListforQuery(String code,String name,Integer page) {
        QueryWrapper<Dictionary> queryWrapper = new QueryWrapper<>();
//        queryWrapper
//                .eq("name", map.get("name").toString()).ne("is_delete", 1)
//                .or(i -> i.eq("code", map.get("code").toString()).ne("is_delete", 1));
        queryWrapper.eq("is_delete",0);
        queryWrapper
                .like("code",code)
                .like("name",name);
        Page<Dictionary> page1 = new Page<>(page,10);  // 查询第1页，每页返回10条
        IPage<Dictionary> iPage = dictionaryMapper.selectPage(page1,queryWrapper);
        return JSON.toJSONString(iPage);
    }


}
