package com.example.dcloud.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.dcloud.common.ServerResponse;
import com.example.dcloud.entity.DictionaryDetail;
import com.example.dcloud.service.DictionaryDetailService;
import com.example.dcloud.service.DictionaryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.stereotype.Controller;

import java.util.*;

import static java.lang.Integer.parseInt;
import static java.lang.Long.parseLong;

@Api(tags = "数据字典明细接口")
@Controller
@RequestMapping("/dictionaryDetail")
public class DictionaryDetailController {

    @Autowired
    private DictionaryDetailService dictionaryDetailService;
    @Autowired
    private DictionaryService dictionaryService;

    @ResponseBody
    @RequestMapping(value = "/getDetail",method = RequestMethod.POST)
    public String getList(@RequestBody JSONObject jsonObject) {
        Map map = JSON.toJavaObject(jsonObject, Map.class);
        String code = map.get("code").toString();
        return JSON.toJSONString(dictionaryDetailService.getDetail(code));
    }
    @ApiOperation(value = "根据关键字返回具体数据字典明细",notes = "get")
    @ResponseBody
    @RequestMapping(value = "/getDetail1",method = RequestMethod.POST)
    public ServerResponse<DictionaryDetail> getList1(@RequestParam(value="typeCode")String typeCode) {
        return dictionaryDetailService.getDetail1(typeCode);
    }
}

