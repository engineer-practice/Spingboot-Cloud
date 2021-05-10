package com.example.dcloud.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.dcloud.entity.*;
import com.example.dcloud.service.*;
import com.example.dcloud.util.DistanceUtil;
import com.example.dcloud.util.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author fifteen
 * @since 2020-06-18
 */
@Controller
@CrossOrigin
@RequestMapping("/attendenceResult")
public class AttendenceResultController {
    @Autowired
    private AttendenceService attendenceService;
    @Autowired
    private AttendenceResultService attendenceResultService;
    @Autowired
    private UserService userService;
    @Autowired
    private CourseStudentService courseStudentService;
    @Autowired
    private CourseService courseService;
    @Autowired
    private SystemManageService systemManageService;

    @ResponseBody
    @RequestMapping(method = RequestMethod.POST)
    public String addResult(@RequestBody JSONObject jsonObject) {
        Map map = JSON.toJavaObject(jsonObject, Map.class);
        String code = map.get("code").toString();
        String email = map.get("student_email").toString();
        String studentLocal = map.get("local").toString();
        System.out.println(map);
        QueryWrapper<Attendence> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("code", code)
                .eq("is_delete", 0);

        if (attendenceService.count(queryWrapper) == 0)
            return ResultUtil.error("当前无签到");
        else {
            Attendence attendence = attendenceService.getOne(queryWrapper);
            int attendId = attendence.getId();
            QueryWrapper<AttendenceResult> queryWrapper1 = new QueryWrapper<>();
            queryWrapper1.eq("student_email", email)
                    .eq("attend_id", attendId);

            String teacherLocal = attendence.getLocal();
            Double distance = DistanceUtil.getDistanceMeter(teacherLocal,studentLocal);
            System.out.println(distance);

            //获取设定的距离参数
            List<SystemManage> list = systemManageService.list();
            int systemDistance = list.get(0).getAttendDistance();

            Date d = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ");
            sdf.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
            AttendenceResult attendenceResult = new AttendenceResult();
            attendenceResult.setAttendTime(sdf.format(d));
            attendenceResult.setCode(code);
            attendenceResult.setStudentEmail(email);
            attendenceResult.setAttendId(attendId);
            attendenceResult.setIsDelete(0);

            if (attendenceResultService.count(queryWrapper1) != 0)
                return ResultUtil.error("请勿重复签到！");

            if(systemDistance==0&&distance<=systemDistance){
                attendenceResultService.save(attendenceResult);
                return sdf.format(d);
            }else{
                return ResultUtil.error("签到位置过远！");
            }
        }
    }

    //某学生在某课程的签到历史记录
    @ResponseBody
    @RequestMapping(method = RequestMethod.PUT)
    public String historyAttendenceResult(@RequestBody JSONObject jsonObject) {
        Map map = JSON.toJavaObject(jsonObject, Map.class);
        String code = map.get("code").toString();
        String email = map.get("student_email").toString();
        QueryWrapper<Attendence> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("code", code)
                .eq("is_delete", 1)
                .orderByDesc("id");
        //该课程所有的签到
        List<Attendence> list = attendenceService.list(queryWrapper);
        int k = 0;//该生的签到次数
        int total = list.size();//总次数
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < list.size(); i++) {
            String startTime = list.get(i).getStartTime();
            int attendId = list.get(i).getId();
            QueryWrapper<AttendenceResult> queryWrapper1 = new QueryWrapper<>();
            queryWrapper1.eq("attend_id", attendId)
                    .eq("student_email", email);
            AttendenceResult attendenceResult = attendenceResultService.getOne(queryWrapper1);
            int count;
            int flag;
            if(attendenceResult==null)
            {
                flag=0;
                count=0;
            }else{
                flag = attendenceResult.getIsDelete();
                if(flag==3){
                    total = total - 1;
                    continue;
                }
                else
                    count=1;
            }
            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("time", startTime);

            if(count==0){
                jsonObject1.put("type", "缺勤");
            }else{
                if (flag == 2)
                    jsonObject1.put("type", "缺勤");
                else if(flag==0){
                    jsonObject1.put("type", "已签到");
                    k++;
                }
                else
                    jsonObject1.put("type", "请假");
            }
            jsonArray.add(jsonObject1);
        }
        String strPer;
        if(total==0)
            strPer = "";
        else{
            int per = k * 100/ total ;
            strPer = per + "%";
        }
        JSONObject jsonObject1 = new JSONObject();
        jsonObject1.put("per", strPer);
        jsonArray.add(jsonObject1);
        return jsonArray.toString();
    }

    //签到结果
    @ResponseBody
    @RequestMapping(method = RequestMethod.PATCH)
    public String attendenceResult(@RequestBody JSONObject jsonObject) {
        Map map = JSON.toJavaObject(jsonObject, Map.class);
        String code = map.get("code").toString();
        String attendId = map.get("attend_id").toString();
        QueryWrapper<Course> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("code", code);
        Course course = courseService.getOne(queryWrapper);
        long courseId = course.getId();
        //查询该课程包含的学生
        QueryWrapper<CourseStudent> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.eq("course_id", courseId)
                .eq("is_delete", 0);
        List<CourseStudent> list = courseStudentService.list(queryWrapper1);
        JSONArray jsonArray = new JSONArray();
        JSONArray jsonArray1 = new JSONArray();
        JSONArray jsonArray2 = new JSONArray();
        int k1 = 0, k2 = 0;
        for (int i = 0; i < list.size(); i++) {
            String email = list.get(i).getStudentEmail();
            QueryWrapper<User> queryWrapper2 = new QueryWrapper<>();
            queryWrapper2.eq("email", email);
            User user = userService.getOne(queryWrapper2);
            String name = user.getName();
            String sno = user.getSno();
            QueryWrapper<AttendenceResult> queryWrapper3 = new QueryWrapper<>();
            queryWrapper3.eq("attend_id", attendId)
                    .eq("student_email", email);
            int count = attendenceResultService.count(queryWrapper3);
            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("name", name);
            jsonObject1.put("sno", sno);
            jsonObject1.put("email", email);
            if (count == 0) {
                jsonObject1.put("type", "缺勤");
                jsonArray1.add(jsonObject1);
                k1++;
            } else {
                AttendenceResult attendenceResult = attendenceResultService.getOne(queryWrapper3);
                if (attendenceResult.getIsDelete() == 0) {
                    jsonObject1.put("type", "已签到");
                    jsonArray2.add(jsonObject1);
                    k2++;
                } else if(attendenceResult.getIsDelete() == 1) {
                    jsonObject1.put("type", "请假");
                    jsonArray1.add(jsonObject1);
                    k1++;
                } else{
                    jsonObject1.put("type", "缺勤");
                    jsonArray1.add(jsonObject1);
                    k1++;
                }
            }
        }
        JSONObject jsonObject2 = new JSONObject();
        jsonObject2.put("total",k1);
        jsonArray1.add(jsonObject2);
        JSONObject jsonObject3 = new JSONObject();
        jsonObject3.put("total",k2);
        jsonArray2.add(jsonObject3);
        jsonArray.add(jsonArray1);
        jsonArray.add(jsonArray2);
        return jsonArray.toString();
    }

    //修改结果
    @ResponseBody
    @RequestMapping(value = "/change",method = RequestMethod.PUT)
    public String changeAttendenceResult(@RequestBody List<Map> list) {
        for(int i=0;i<list.size();i++){
            int attendId = Integer.parseInt(list.get(i).get("attend_id").toString());
            String email = list.get(i).get("student_email").toString();
            String code = list.get(i).get("code").toString();
            int type = Integer.parseInt(list.get(i).get("type").toString());
            Date d = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ");
            sdf.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));

            QueryWrapper<AttendenceResult> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("student_email",email)
                    .eq("attend_id",attendId);
            int count = attendenceResultService.count(queryWrapper);

            //获取设定的经验值参数
            List<SystemManage> list1 = systemManageService.list();
            int systemExp = list1.get(0).getAttendExp();

            QueryWrapper<Course> queryWrapper2 = new QueryWrapper<>();
            queryWrapper2.eq("code",code);
            Course course = courseService.getOne(queryWrapper2);
            long courseId = course.getId();
            QueryWrapper<CourseStudent> queryWrapper3 = new QueryWrapper<>();
            queryWrapper3.eq("course_id",courseId)
                    .eq("student_email",email);
            CourseStudent courseStudent = courseStudentService.getOne(queryWrapper3);
            int exp1 = courseStudent.getExp();

            if(count==0){
                AttendenceResult attendenceResult = new AttendenceResult();
                attendenceResult.setAttendTime(sdf.format(d));
                attendenceResult.setCode(code);
                attendenceResult.setStudentEmail(email);
                attendenceResult.setAttendId(attendId);
                attendenceResult.setIsDelete(type);
                attendenceResultService.save(attendenceResult);

                //增加该课程经验值
                CourseStudent courseStudent1 = new CourseStudent();
                courseStudent1.setExp(exp1+systemExp);
                courseStudentService.update(courseStudent1,queryWrapper3);

                QueryWrapper<User> queryWrapper1 = new QueryWrapper<>();
                queryWrapper1.eq("email",email);
                User user = userService.getOne(queryWrapper1);
                int exp;
                if(type==0)
                    exp = user.getExp()+systemExp;
                else
                    exp = user.getExp()+systemExp/2;
                user.setExp(exp);
                userService.update(user,queryWrapper1);
            }
            else{
                AttendenceResult attendenceResult1 = attendenceResultService.getOne(queryWrapper);
                int flag = attendenceResult1.getIsDelete();

                AttendenceResult attendenceResult = new AttendenceResult();
                attendenceResult.setAttendTime(sdf.format(d));
                attendenceResult.setIsDelete(type);
                attendenceResultService.update(attendenceResult,queryWrapper);

                int exp2;
                if(type==2&&flag==0)
                    exp2 = exp1-systemExp;
                else if(type==2&&flag==1)
                    exp2 = exp1-(systemExp/2);
                else if(type==1&&flag==0)
                    exp2 = exp1-(systemExp/2);
                else if(type==1&&flag==2)
                    exp2 = exp1+(systemExp/2);
                else if(type==0&&flag==1)
                    exp2 = exp1+(systemExp/2);
                else
                    exp2 = exp1+systemExp;
                CourseStudent courseStudent1 = new CourseStudent();
                courseStudent1.setExp(exp2);
                courseStudentService.update(courseStudent1,queryWrapper3);

                QueryWrapper<User> queryWrapper1 = new QueryWrapper<>();
                queryWrapper1.eq("email",email);
                User user = userService.getOne(queryWrapper1);
                int exp;
                if(type==2&&flag==0)
                    exp = user.getExp()-systemExp;
                else if(type==2&&flag==1)
                    exp = user.getExp()-(systemExp/2);
                else if(type==1&&flag==0)
                    exp = user.getExp()-(systemExp/2);
                else if(type==1&&flag==2)
                    exp = user.getExp()+(systemExp/2);
                else if(type==0&&flag==1)
                    exp = user.getExp()+(systemExp/2);
                else
                    exp = user.getExp()+systemExp;
                user.setExp(exp);
                userService.update(user,queryWrapper1);
            }
        }
        return ResultUtil.success();
    }
}

