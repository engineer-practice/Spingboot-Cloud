package com.example.dcloud.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.example.dcloud.common.ServerResponse;
import com.example.dcloud.entity.Course;
import com.example.dcloud.entity.Dictionary;
import com.example.dcloud.entity.DictionaryDetail;
import com.example.dcloud.entity.School;
import com.example.dcloud.mapper.DictionaryDetailMapper;
import com.example.dcloud.mapper.DictionaryMapper;
import com.example.dcloud.service.CourseStudentService;
import com.example.dcloud.service.DictionaryDetailService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.dcloud.vo.CourseVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class DictionaryDetailServiceImpl extends ServiceImpl<DictionaryDetailMapper, DictionaryDetail> implements DictionaryDetailService {

    @Autowired
    private DictionaryDetailService dictionaryDetailService;
    @Resource
    private DictionaryDetailMapper dictionaryDetailMapper;
    @Resource
    private DictionaryMapper dictionaryMapper;
    @Override
    //查询数据字典明细
    public Map<Object,List> getDetail(String code) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("code", code);
        map.put("is_delete", 0);
        List<Dictionary> list1 = dictionaryMapper.selectByMap(map);
        Map<String, Object> detailMap = new HashMap<String, Object>();
        detailMap.put("type_code", code);
        detailMap.put("is_delete",0);
        List<DictionaryDetail> list2 = dictionaryDetailMapper.selectByMap(detailMap);
        Map<Object,List> list = new HashMap<>();
        list.put("dict",list1);
        list.put("detail",list2);
        System.out.println(list);
        return list;
    }
    //查询数据字典明细
    public ServerResponse<DictionaryDetail> getDetail1(String typeCode) {
        ServerResponse<DictionaryDetail> response = new ServerResponse<>();
        QueryWrapper<DictionaryDetail> queryWrapper = new QueryWrapper();
        queryWrapper.eq("type_code",typeCode)
                .eq("is_delete",0);
        List<DictionaryDetail> list = dictionaryDetailService.list(queryWrapper);
        response.setDataList(list);
        response.setTotal((long)list.size());
        response.setResult(true);
        response.setMsg("查询成功！");
        return response;
    }
}
