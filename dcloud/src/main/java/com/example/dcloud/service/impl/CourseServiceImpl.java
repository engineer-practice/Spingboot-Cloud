package com.example.dcloud.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.example.dcloud.entity.Course;
import com.example.dcloud.mapper.CourseMapper;
import com.example.dcloud.service.CourseService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static java.lang.Integer.parseInt;


@Service
public class CourseServiceImpl extends ServiceImpl<CourseMapper, Course> implements CourseService {
    /**
     * 按照JSONArray中的对象的某个字段进行排序(采用fastJson)
     * @param jsonArrStr json数组字符串
     */
    @Override
    public String jsonArraySort(String jsonArrStr,String sortKey) {
        JSONArray jsonArr = JSON.parseArray(jsonArrStr);
        JSONArray sortedJsonArray = new JSONArray();
        List<JSONObject> jsonValues = new ArrayList<JSONObject>();
        for (int i = 0; i < jsonArr.size(); i++) {
            jsonValues.add(jsonArr.getJSONObject(i));
        }
        Collections.sort(jsonValues, new Comparator<JSONObject>() {
            String string1;
            String string2;
            @Override
            public int compare(JSONObject a, JSONObject b) {
                try {
                    string1= a.getString(sortKey).replaceAll("-", "");
                    string2= b.getString(sortKey).replaceAll("-", "");
                } catch (JSONException e) {
                    // 处理异常
                }
                //这里是按照字段逆序排列,不加负号为正序排列
                if(sortKey.equals("sno")){
                    return string1.compareTo(string2);
                }else{
                    return -string1.compareTo(string2);
                }

            }
        });

        for (int i = 0; i < jsonArr.size(); i++) {

            jsonValues.get(i).put("index",i);
            sortedJsonArray.add(jsonValues.get(i));
        }
        return JSON.toJSONString(sortedJsonArray);
    }

    @Override
    public String searchArray(String jsonArr, String search) {
        JSONArray searchResult = JSON.parseArray(jsonArr);
        JSONArray searchArray = new JSONArray();
        for(int i = 0 ; i < searchResult.size(); i++){
            JSONObject user = JSON.parseObject(searchResult.get(i).toString());
            JSONObject result = new JSONObject();
            if(search!=null && search !="" && (user.get("name").toString().contains(search)
                    ||user.get("sno").toString().contains(search))){
                result.put("name",user.get("name"));
                result.put("sno",user.get("sno"));
                result.put("sex",user.get("sex"));
                result.put("exp",user.get("exp"));
                result.put("index",user.get("index"));
                searchArray.add(result);
            }
        }
        return JSON.toJSONString(searchArray);
    }

    //按班课名称或班课号来搜索课程
    @Override
    public String searchLesson(String jsonArr, String search) {
        JSONArray searchResult = JSON.parseArray(jsonArr);
        JSONArray searchArray = new JSONArray();
        int index = 0;
        for(int i = 0 ; i < searchResult.size(); i++){
            JSONObject lesson = JSON.parseObject(searchResult.get(i).toString());
            JSONObject result = new JSONObject();
            if(search!=null && search !="" && (lesson.get("name").toString().contains(search)
                    ||lesson.get("no").toString().contains(search))){
                result = lesson;
                //额外放入了index
                result.put("index",index);
                index++;
                searchArray.add(result);
            }
        }
        return JSON.toJSONString(searchArray);
    }

    @Override
    public String getRank(String jsonArr, String email) {
        JSONArray searchResult = JSON.parseArray(jsonArr);
        JSONObject result = new JSONObject();
//        for(int i = 0 ; i < searchResult.size(); i++){
//            JSONObject member = JSON.parseObject(searchResult.get(i).toString());
//            if(member.get("email").toString().equals(email)){
//                result.put("rank",parseInt(member.get("index").toString())+1);
//                result.put("exp",member.get("exp").toString());
//                break;
//            }
//        }

        int rank = 1;
        int exp = parseInt(JSON.parseObject(searchResult.get(0).toString()).get("exp").toString());
        if(JSON.parseObject(searchResult.get(0).toString()).get("email").toString().equals(email)) {
            result.put("rank",1);
            result.put("exp",JSON.parseObject(searchResult.get(0).toString()).get("exp").toString().toString());
        }
            //大--->小
            for(int i = 1 ; i < searchResult.size(); i++){
                JSONObject member = JSON.parseObject(searchResult.get(i).toString());
                if(parseInt(member.get("exp").toString()) < exp){//说明经验值不等
                    rank++;
                }
                if(member.get("email").toString().equals(email)){
                    result.put("rank",rank);
                    result.put("exp",member.get("exp").toString());
                    break;
                }
            }
        return JSON.toJSONString(result);
    }
}
