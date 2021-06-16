package com.example.dcloud.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.dcloud.common.ServerResponse;
import com.example.dcloud.entity.Dictionary;
import com.example.dcloud.entity.DictionaryDetail;
import com.example.dcloud.service.DictionaryDetailService;
import com.example.dcloud.service.DictionaryService;
import com.example.dcloud.util.ResultUtil;
import io.swagger.annotations.Api;
import org.apache.tomcat.jni.Directory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.stereotype.Controller;

import java.util.*;

import static java.lang.Integer.parseInt;
import static java.lang.Long.parseLong;
@Api(tags = "数据字典管理接口")
@RestController
@RequestMapping("/dictionaries")
public class DictionaryController {
    @Autowired
    private DictionaryService dictionaryService;
    @Autowired
    private DictionaryDetailService dictionaryDetailService;
    /**
     * 返回数据字典列表,查询，详情
     * @param page
     * @return
     */
    @ResponseBody
    @RequestMapping(method = RequestMethod.GET)
    //获取数据字典的列表
    //public String getList(
//    public ServerResponse<Dictionary> getList(
//            @RequestParam(value="page",required = false)Integer page,
//            @RequestParam(value="code",required = false)String code,
//            @RequestParam(value="name",required = false)String name
//    ) {
//        //有code-->详情
//        ServerResponse<Dictionary> response =new ServerResponse<>();
//        //有传参 根据参数查询
//        if(code!=null){
////            if(name!=null){//有name--->查询
////                System.out.println("111");
////                return dictionaryService.pageListforQuery(code,name,page);
////            }else{
////                System.out.println("222");
////                return JSON.toJSONString(dictionaryDetailService.getDetail(code));
////
////            }
////        }else{
//            //无传参数 获取列表
//            System.out.println("333");
//            return dictionaryService.pageList(page);
//        }else {
//            response.setResult(Boolean.FALSE);
//            response.setMsg("失败");
//            return response;
//        }
//    }
    public String getList(
            @RequestParam(value="page",required = false)Integer page,
            @RequestParam(value="code",required = false)String code,
            @RequestParam(value="name",required = false)String name
    ) {
        //有code-->详情

        //有传参 根据参数查询
        if(code!=null){
            if(name!=null){//有name--->查询
                System.out.println("111");
                return dictionaryService.pageListforQuery(code,name,page);
            }else{
                System.out.println("222");
                return JSON.toJSONString(dictionaryDetailService.getDetail(code));

            }
        }else{
            //无传参数 获取列表
            System.out.println("333");
            return dictionaryService.pageList(page);
        }
    }

    /**
     * 编辑
     * @param jsonObject
     * @return
     */
    @ResponseBody
    @RequestMapping(method = RequestMethod.PATCH)
    public String update(@RequestBody JSONObject jsonObject) {
        Map map = JSON.toJavaObject(jsonObject, Map.class);
        System.out.println("编辑");
        List<DictionaryDetail> entityList = new ArrayList<>();
        //判断new_code的唯一性
        QueryWrapper<Dictionary> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("code", map.get("new_code").toString())
                    .eq("is_delete",0);
        int count = dictionaryService.count(queryWrapper);
        if(!map.get("new_code").toString().equals(map.get("old_code").toString())){
            if(count > 0){
                return ResultUtil.error("英文标识符已存在!");
            }
        }
        //判断new_name的唯一性
        QueryWrapper<Dictionary> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.eq("name", map.get("new_name").toString())
                     .eq("is_delete",0);
        int count1 = dictionaryService.count(queryWrapper1);
        if(!map.get("new_name").toString().equals(map.get("old_name").toString())){
            if(count > 0){
                return ResultUtil.error("中文标识符已存在!");
            }
        }
        //数据项判断
        JSONArray detail=(JSONArray) JSONArray.toJSON(map.get("detail"));
        int defaultMark = 0;
        Set valueSet = new HashSet<>();
        Set nameSet = new HashSet<>();
        int beforeSize = valueSet.size();
        int beforeSize1 = nameSet.size();
        for(int i = 0; i < detail.size(); i++){
            JSONObject tempObject=(JSONObject) JSONObject.toJSON(detail.get(i));
            Map temp = JSON.toJavaObject(tempObject, Map.class);
            valueSet.add(temp.get("value").toString());
            int afterSize = valueSet.size();
            if(beforeSize == afterSize){
                return ResultUtil.error("数据项值重复！");
            }else {
                beforeSize = afterSize;
            }
            nameSet.add(temp.get("name").toString());
            int afterSize1 = nameSet.size();
            if(beforeSize1 == afterSize1){
                return ResultUtil.error("数据项文本重复！");
            }else {
                beforeSize1 = afterSize1;
            }
            if(parseInt(temp.get("is_default").toString()) == 1){
                defaultMark ++;
                if(defaultMark > 1){
                    return ResultUtil.error("多个数据项设置为默认值！");
                }
            }
            DictionaryDetail dictionaryDetail = new DictionaryDetail();
            if(temp.containsKey("id")){
                dictionaryDetail.setId(parseLong(temp.get("id").toString()));
            }
            dictionaryDetail.setCode(temp.get("code").toString());
            dictionaryDetail.setName(temp.get("name").toString());//唯一
            dictionaryDetail.setValue(temp.get("value").toString());//唯一
            dictionaryDetail.setDictOrder(parseInt(temp.get("dict_order").toString()));
            dictionaryDetail.setIsDefault(parseInt(temp.get("is_default").toString()));
            dictionaryDetail.setTypeCode(map.get("new_code").toString());
            dictionaryDetail.setIsDelete(0);
            entityList.add(dictionaryDetail);
        }
        //更新字典
        Dictionary dictionary = new Dictionary();
        dictionary.setId(parseLong(map.get("id").toString()));
        if(map.get("description").toString() == ""){
            dictionary.setDescription(" ");
        }else{
            dictionary.setDescription(map.get("description").toString());
        }
        dictionary.setName(map.get("new_name").toString());
        dictionary.setCode(map.get("new_code").toString());
        dictionary.setIsDelete(0);
        //更新数据项
        dictionaryService.updateById(dictionary);
        for(int i = 0; i < entityList.size(); i++){
                dictionaryDetailService.saveOrUpdate(entityList.get(i));
        }
        return ResultUtil.success();
    }

    /**
     * 新增
     * @param jsonObject(code，name，description)
     * dictinarydetail表type_code(code)
     * @return
     */
    @ResponseBody
    @RequestMapping(method = RequestMethod.POST)
    public String addOne(@RequestBody JSONObject jsonObject) {
        Map map = JSON.toJavaObject(jsonObject, Map.class);
        List<DictionaryDetail> entityList = new ArrayList<>();
        //判断code的唯一性
        QueryWrapper<Dictionary> queryWrapper = new QueryWrapper<>();
        queryWrapper
                .eq("code", map.get("code").toString())
                .eq("is_delete",0);
        int count = dictionaryService.count(queryWrapper);
        if(count > 0){
            return ResultUtil.error("英文标识符已存在！");
        }
        QueryWrapper<Dictionary> queryCode = new QueryWrapper<>();
        queryCode
                .eq("name", map.get("name").toString())
                .eq("is_delete",0);
        int countName = dictionaryService.count(queryWrapper);
        if(countName > 0){
            return ResultUtil.error("中文标识符已存在！");
        }
        //数据项（文本名称，值，数据项标识符，是否默认值，排序）
        JSONArray detail=(JSONArray) JSONArray.toJSON(map.get("detail"));
        int defaultMark = 0;
        Set valueSet = new HashSet<>();
        int beforeSize = valueSet.size();
        for(int i = 0; i < detail.size(); i++){
            JSONObject tempObject=(JSONObject) JSONObject.toJSON(detail.get(i));
            Map temp = JSON.toJavaObject(tempObject, Map.class);
            valueSet.add(temp.get("value").toString());
            int afterSize = valueSet.size();
            if(beforeSize == afterSize){
                return ResultUtil.error("数据项值不允许重复！");
            }else {
                beforeSize = afterSize;
            }
            if(parseInt(temp.get("is_default").toString()) == 1){
                defaultMark ++;
                if(defaultMark > 1){
                    return ResultUtil.error("多个数据项设置为默认值！");
                }
            }
            DictionaryDetail dictionaryDetail = new DictionaryDetail();
            dictionaryDetail.setCode(temp.get("code").toString());
            dictionaryDetail.setName(temp.get("name").toString());//唯一
            dictionaryDetail.setValue(temp.get("value").toString());//唯一
            dictionaryDetail.setDictOrder(parseInt(temp.get("dict_order").toString()));
            dictionaryDetail.setIsDefault(parseInt(temp.get("is_default").toString()));
            dictionaryDetail.setTypeCode(map.get("code").toString());
            dictionaryDetail.setIsDelete(0);
            entityList.add(dictionaryDetail);
        }

        //数据字典(英文标识符，中文标识符，说明)
        Dictionary dictionary = new Dictionary();
        dictionary.setCode(map.get("code").toString());//唯一
        dictionary.setName(map.get("name").toString());//唯一
        if(map.get("description").toString()==("")){
            dictionary.setDescription(" ");
        }else{
            dictionary.setDescription(map.get("description").toString());
        }
        dictionary.setIsDelete(0);
        boolean flag = dictionaryService.saveOrUpdate(dictionary);
        if(flag){
            dictionaryDetailService.saveBatch(entityList);
        }
        return ResultUtil.success();
    }

    /**
     * 删除
     * @param list
     * @return
     */
    @ResponseBody
    @RequestMapping(method = RequestMethod.DELETE)
    public String delDict(@RequestParam(value = "del_list") List list) {
        if(list.size() == 1){//单条删除
            DictionaryDetail dictionaryDetail = new DictionaryDetail();
            dictionaryDetail.setId(parseLong(list.get(0).toString()));
            dictionaryDetail.setIsDelete(1);
            dictionaryDetailService.updateById(dictionaryDetail);
            return ResultUtil.success();
        }
        list.remove(0);
        for(int i = 0; i < list.size(); i++){
            String code = list.get(i).toString();
            QueryWrapper dictWrapper = new QueryWrapper();
            dictWrapper.eq("code",code);
            Dictionary dictionary = new Dictionary();
            dictionary.setIsDelete(1);
            dictionaryService.update(dictionary,dictWrapper);

            //继续删除数据项
            QueryWrapper detailWrapper = new QueryWrapper();
            detailWrapper.eq("type_code",code);
            DictionaryDetail dictionaryDetail = new DictionaryDetail();
            dictionaryDetail.setIsDelete(1);
            dictionaryDetailService.update(dictionaryDetail,detailWrapper);
        }
        return ResultUtil.success();
    }

}

