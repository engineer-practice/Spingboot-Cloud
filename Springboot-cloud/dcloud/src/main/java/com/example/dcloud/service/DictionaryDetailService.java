package com.example.dcloud.service;

import com.example.dcloud.common.ServerResponse;
import com.example.dcloud.entity.DictionaryDetail;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

public interface DictionaryDetailService extends IService<DictionaryDetail> {
    Map<Object,List> getDetail(String code);
    ServerResponse<DictionaryDetail> getDetail1(String code);

}
