package com.example.dcloud.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.dcloud.service.DictionaryDetailService;
import com.example.dcloud.service.DictionaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.stereotype.Controller;

import java.util.*;

import static java.lang.Integer.parseInt;
import static java.lang.Long.parseLong;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author fifteen
 * @since 2020-04-01
 */
@Controller
@RequestMapping("/dictionaryDetail")
public class DictionaryDetailController {

    @Autowired
    private DictionaryDetailService dictionaryDetailService;
    @Autowired
    private DictionaryService dictionaryService;

    /**
     * 数据字典详情
     * @param jsonObject(code)
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/getDetail",method = RequestMethod.POST)
    public String getList(@RequestBody JSONObject jsonObject) {
        Map map = JSON.toJavaObject(jsonObject, Map.class);
        String code = map.get("code").toString();
        return JSON.toJSONString(dictionaryDetailService.getDetail(code));
    }


}

