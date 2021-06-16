package com.example.dcloud.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.dcloud.entity.Dictionary;
import com.example.dcloud.entity.SystemManage;
import com.example.dcloud.mapper.SystemManageMapper;
import com.example.dcloud.service.SystemManageService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


@Service
public class SystemManageServiceImpl extends ServiceImpl<SystemManageMapper, SystemManage> implements SystemManageService {
    @Resource SystemManageMapper systemManageMapper;
    public String pageList(Integer cur_page) {
        QueryWrapper<SystemManage> queryWrapper = new QueryWrapper<>();
        queryWrapper
                .eq("is_delete", 0)
                .orderByDesc("id");
        Page<SystemManage> page = new Page<>(cur_page, 10);  // 查询第1页，每页返回10条
        IPage<SystemManage> iPage = systemManageMapper.selectPage(page, queryWrapper);
//        iPage.setTotal(iPage.getRecords().size());
        return JSON.toJSONString(iPage);
    }
}
