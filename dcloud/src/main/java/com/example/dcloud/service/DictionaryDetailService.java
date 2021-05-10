package com.example.dcloud.service;

import com.example.dcloud.entity.DictionaryDetail;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author fifteen
 * @since 2020-04-01
 */
public interface DictionaryDetailService extends IService<DictionaryDetail> {
    Map<Object,List> getDetail(String code);
}
