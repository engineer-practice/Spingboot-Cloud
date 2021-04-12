package com.example.dcloud.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.dcloud.entity.SystemManage;
import com.example.dcloud.service.SystemManageService;
import com.example.dcloud.util.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import sun.awt.LightweightFrame;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Integer.parseInt;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author fifteen
 * @since 2020-04-01
 */
@Controller
@RequestMapping("/systems")
public class SystemManageController {
    @Autowired
    private SystemManageService systemManageService;
    //获取
    @ResponseBody
    @RequestMapping(method = RequestMethod.GET)
    public String getAll(){
        List<SystemManage> list = systemManageService.list();
        List<Map<String,String>> result = new ArrayList<>();
        Map<String,String> map = new HashMap();

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("attend_exp",list.get(0).getAttendExp().toString());
        jsonObject.put("activity_exp",list.get(0).getActivityExp().toString());
        jsonObject.put("distance",list.get(0).getAttendDistance().toString());

        JSONArray jsonArray1 = new JSONArray();
        JSONArray jsonArray2 = new JSONArray();
        for(int i = 0 ; i < list.size(); i++){
            JSONObject jsonArrayObject1 = new JSONObject();
            JSONObject jsonArrayObject2 = new JSONObject();
            jsonArrayObject1.put("value",parseInt(list.get(i).getAttendLevel().toString()));
            Map<String,String> map2 = new HashMap();
            if(i == 0){
                jsonArrayObject2.put("ratio1",0);
            }else{
                jsonArrayObject2.put("ratio1",parseInt(list.get(i-1).getAttendRatio().toString()));
            }
            jsonArrayObject2.put("ratio2",parseInt(list.get(i).getAttendRatio().toString()));
            jsonArray1.add(jsonArrayObject1);
            jsonArray2.add(jsonArrayObject2);
        }
        jsonObject.put("levels",jsonArray1);
        jsonObject.put("attendence",jsonArray2);
        return JSON.toJSONString(jsonObject);
    }
    //编辑
    @ResponseBody
    @RequestMapping(method = RequestMethod.PATCH)
    public String update(@RequestBody JSONObject jsonObject){
        //先将数据库中的全部删除
        QueryWrapper<SystemManage> queryWrapper = new QueryWrapper();
        queryWrapper.eq("is_delete",0);
        systemManageService.remove(queryWrapper);
        Map map = JSON.toJavaObject(jsonObject, Map.class);
        JSONArray detail=(JSONArray) JSONArray.toJSON(map.get("detail"));
        for(int i = 0; i < detail.size(); i++){
            JSONObject tempObject=(JSONObject) JSONObject.toJSON(detail.get(i));
            Map temp = JSON.toJavaObject(tempObject, Map.class);
            SystemManage systemManage = new SystemManage();
            systemManage.setIsDelete(0);
            systemManage.setAttendExp(parseInt(map.get("attend_exp").toString()));
            systemManage.setActivityExp(parseInt(map.get("activity_exp").toString()));
            systemManage.setAttendDistance(parseInt(map.get("distance").toString()));
            systemManage.setAttendLevel(parseInt(temp.get("level").toString()));
            systemManage.setAttendRatio(parseInt(temp.get("ratio").toString()));
            systemManageService.saveOrUpdate(systemManage);
        }
        return ResultUtil.success();
    }

}

