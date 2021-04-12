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

//    /**
//     * 新增
//     * @param jsonObject(code，name，description)
//     * dictinarydetail表type_code(code)
//     * @return
//     */
//    @ResponseBody
//    @RequestMapping(value = "/addDict",method = RequestMethod.POST)
//    public String addOne(@RequestBody JSONObject jsonObject) {
//        Map map = JSON.toJavaObject(jsonObject, Map.class);
//        List<DictionaryDetail> entityList = new ArrayList<>();
//        //判断code的唯一性
//        QueryWrapper<Dictionary> queryWrapper = new QueryWrapper<>();
//        queryWrapper
//                .eq("code", map.get("code").toString())
//                .eq("is_delete",0);
//        int count = dictionaryService.count(queryWrapper);
//        if(count > 0){
//            return ResultUtil.error("英文标识符已存在！");
//        }
//        QueryWrapper<Dictionary> queryCode = new QueryWrapper<>();
//        queryCode
//                .eq("name", map.get("name").toString())
//                .eq("is_delete",0);
//        int countName = dictionaryService.count(queryWrapper);
//        if(countName > 0){
//            return ResultUtil.error("中文标识符已存在！");
//        }
//        //数据项（文本名称，值，数据项标识符，是否默认值，排序）
//        JSONArray detail=(JSONArray) JSONArray.toJSON(map.get("detail"));
//        int defaultMark = 0;
//        Set valueSet = new HashSet<>();
//        int beforeSize = valueSet.size();
//        for(int i = 0; i < detail.size(); i++){
//            JSONObject tempObject=(JSONObject) JSONObject.toJSON(detail.get(i));
//            Map temp = JSON.toJavaObject(tempObject, Map.class);
//            valueSet.add(temp.get("value").toString());
//            int afterSize = valueSet.size();
//            if(beforeSize == afterSize){
//                return ResultUtil.error("数据项值已存在！");
//            }else {
//                beforeSize = afterSize;
//            }
//            if(parseInt(temp.get("is_default").toString()) == 1){
//                defaultMark ++;
//                if(defaultMark > 1){
//                    return ResultUtil.error("多个数据项设置为默认值！");
//                }
//            }
//            DictionaryDetail dictionaryDetail = new DictionaryDetail();
//            dictionaryDetail.setCode(temp.get("code").toString());
//            dictionaryDetail.setName(temp.get("name").toString());//唯一
//            dictionaryDetail.setValue(temp.get("value").toString());//唯一
//            dictionaryDetail.setDictOrder(parseInt(temp.get("dict_order").toString()));
//            dictionaryDetail.setIsDefault(parseInt(temp.get("is_default").toString()));
//            dictionaryDetail.setTypeCode(map.get("code").toString());
//            dictionaryDetail.setIsDelete(0);
//            entityList.add(dictionaryDetail);
//        }
//
//        //数据字典(英文标识符，中文标识符，说明)
//        Dictionary dictionary = new Dictionary();
//        dictionary.setCode(map.get("code").toString());//唯一
//        dictionary.setName(map.get("name").toString());//唯一
//        if(map.get("description").toString()==("")){
//            dictionary.setDescription(" ");
//        }else{
//            dictionary.setDescription(map.get("description").toString());
//        }
//        dictionary.setIsDelete(0);
//        boolean flag = dictionaryService.saveOrUpdate(dictionary);
//        if(flag){
//            dictionaryDetailService.saveBatch(entityList);
//        }
//        return ResultUtil.success();
//    }

    /**
     * 删除
     * @param jsonObject
     * @return
     */
//    @ResponseBody
//    @RequestMapping(value = "/del",method = RequestMethod.POST)
//    public String del(@RequestBody JSONObject jsonObject) {
//        Map map = JSON.toJavaObject(jsonObject, Map.class);
//        DictionaryDetail dictionaryDetail = new DictionaryDetail();
//        dictionaryDetail.setId(parseLong(map.get("id").toString()));
//        dictionaryDetail.setIsDelete(1);
//        dictionaryDetailService.updateById(dictionaryDetail);
//        return ResultUtil.success();
//    }

}

