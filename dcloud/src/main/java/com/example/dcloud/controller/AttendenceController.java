package com.example.dcloud.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.dcloud.entity.*;
import com.example.dcloud.service.*;
import com.example.dcloud.util.ResultUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
@RequestMapping("/attendence")
public class AttendenceController {

    private static final Logger LOG = LoggerFactory.getLogger(AttendenceController.class);

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
    @RequestMapping(value = "/isEnd",method = RequestMethod.POST)
    public String hasAttendence(@RequestBody JSONObject jsonObject) {
        Map map = JSON.toJavaObject(jsonObject, Map.class);
        QueryWrapper<Attendence> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("code",map.get("code").toString())
                    .eq("is_delete",0);
        int count = attendenceService.count(queryWrapper);
        if(count > 0){
            //有未结束签到
            return ResultUtil.error("0");
        }else{
            return ResultUtil.success();
        }
    }

    @ResponseBody
    @RequestMapping(value = "/end",method = RequestMethod.POST)
    public String endPriorAttendence(@RequestBody JSONObject jsonObject) {
        Map map = JSON.toJavaObject(jsonObject, Map.class);
        QueryWrapper<Attendence> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("code",map.get("code").toString())
                    .eq("is_delete",0);
        Attendence attendence = new Attendence();
        attendence.setIsDelete(2);
        attendenceService.update(attendence,queryWrapper);
        return ResultUtil.success();
//        int  = attendenceService.count(queryWrapper);
//        if(count > 0){
//            //有未结束签到
//            return ResultUtil.error("0");
//        }else{
//            return ResultUtil.success();
//        }
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.POST)
    public int createAttendence(@RequestBody JSONObject jsonObject) {
        Map map = JSON.toJavaObject(jsonObject, Map.class);
        Attendence attendence = new Attendence();
        attendence.setCode(map.get("code").toString());
        attendence.setLocal(map.get("local").toString());
        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ");
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));

        //输出日志
        LOG.info("======attendence签到===开始时间：{}===code：{}, local：{}",sdf.format(d),map.get("code").toString(),map.get("local").toString());
        
        attendence.setStartTime(sdf.format(d));
        attendence.setIsDelete(0);
        attendenceService.saveOrUpdate(attendence);
        QueryWrapper<Attendence> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("code", map.get("code").toString())
                .eq("start_time", sdf.format(d))
                .eq("is_delete",0);
        Attendence attendence1 = attendenceService.getOne(queryWrapper);
        return attendence1.getId();
    }


    //手工签到
    @ResponseBody
    @RequestMapping(value = "/hand",method = RequestMethod.PUT)
    public int addResultByHand(@RequestBody JSONObject jsonObject){
        Map map = JSON.toJavaObject(jsonObject, Map.class);
        String code = map.get("code").toString();
        Attendence attendence = new Attendence();
        attendence.setCode(map.get("code").toString());
        attendence.setLocal("1111");
        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ");
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        attendence.setStartTime(sdf.format(d));
        attendence.setIsDelete(0);
        attendenceService.saveOrUpdate(attendence);
        QueryWrapper<Attendence> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("code", map.get("code").toString())
                .eq("start_time", sdf.format(d))
                .eq("is_delete",0);
        Attendence attendence1 = attendenceService.getOne(queryWrapper);
        int attendId = attendence1.getId();

        //根据课程号查找课程id
        QueryWrapper<Course> queryWrapper2= new QueryWrapper<>();
        queryWrapper2.eq("code",code);
        Course course = courseService.getOne(queryWrapper2);
        long courseId = course.getId();
        //查询该课程总学生数
        QueryWrapper<CourseStudent> queryWrapper3 = new QueryWrapper<>();
        queryWrapper3.eq("course_id",courseId)
                .eq("is_delete",0);
        List<CourseStudent> list = courseStudentService.list(queryWrapper3);

        //获取设定的经验值参数
        List<SystemManage> list1 = systemManageService.list();
        int systemExp = list1.get(0).getAttendExp();

        for(int i=0;i<list.size();i++){
            //新增签到记录
            AttendenceResult attendenceResult = new AttendenceResult();
            attendenceResult.setAttendTime(sdf.format(d));
            attendenceResult.setCode(code);
            attendenceResult.setStudentEmail(list.get(i).getStudentEmail());
            attendenceResult.setAttendId(attendId);
            attendenceResult.setIsDelete(0);
            attendenceResultService.save(attendenceResult);

            //增加该学生在该课程的经验值
            CourseStudent courseStudent =new CourseStudent();
            courseStudent.setId(list.get(i).getId());
            courseStudent.setExp(list.get(i).getExp()+systemExp);
            courseStudentService.updateById(courseStudent);

            //更新该学生总经验值
            QueryWrapper<User> queryWrapper1 = new QueryWrapper<>();
            queryWrapper1.eq("email",list.get(i).getStudentEmail());
            User user = userService.getOne(queryWrapper1);
            int exp = user.getExp()+systemExp;
            user.setExp(exp);
            userService.update(user,queryWrapper1);
        }
        Attendence attendence2 = new Attendence();
        attendence2.setId(attendId);
        attendence2.setIsDelete(1);
        attendenceService.updateById(attendence2);
        return attendId;
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.PUT)
    public String getAttendence(@RequestBody JSONObject jsonObject) {
        Map map = JSON.toJavaObject(jsonObject, Map.class);
        String code = map.get("code").toString();
//        System.out.println(map.get("attend_id").toString());
        int attend_id = Integer.parseInt(map.get("attend_id").toString());
        QueryWrapper<AttendenceResult> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("attend_id",attend_id);
        List<AttendenceResult> list = attendenceResultService.list(queryWrapper);
        String[] name = new String[list.size()];
        for(int i=0;i<list.size();i++){
            String studentEmail = list.get(i).getStudentEmail();
            QueryWrapper<User> queryWrapper1 = new QueryWrapper<>();
            queryWrapper1.eq("email",studentEmail);
            User user = userService.getOne(queryWrapper1);
            name[i] = user.getName();
        }
        //根据课程号查找课程id
        QueryWrapper<Course> queryWrapper2= new QueryWrapper<>();
        queryWrapper2.eq("code",code);
        Course course = courseService.getOne(queryWrapper2);
        long courseId = course.getId();
        //查询该课程总学生数
        QueryWrapper<CourseStudent> queryWrapper3 = new QueryWrapper<>();
        queryWrapper3.eq("course_id",courseId)
                    .eq("is_delete",0);
        JSONObject jsonObject1 = new JSONObject();
        jsonObject1.put("name",name);
        jsonObject1.put("count",attendenceResultService.count(queryWrapper));
        jsonObject1.put("total",courseStudentService.count(queryWrapper3));
        return jsonObject1.toString();
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.DELETE)
    public String endAttendence(@RequestBody JSONObject jsonObject) {
        Map map = JSON.toJavaObject(jsonObject, Map.class);
        int attend_id = Integer.parseInt(map.get("attend_id").toString());
        String type = map.get("type").toString();
        QueryWrapper<AttendenceResult> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("attend_id",attend_id);
        //获取设定的经验值参数
        List<SystemManage> list1 = systemManageService.list();
        int systemExp = list1.get(0).getAttendExp();
        //type为0是放弃签到，不加经验值，为1是结束，加经验值
        if(type.equals("1")){
            Attendence attendence = new Attendence();
            attendence.setId(attend_id);
            attendence.setIsDelete(1);
            attendenceService.updateById(attendence);
            List<AttendenceResult> list = attendenceResultService.list(queryWrapper);
            if(list.size() != 0){
                String code = list.get(0).getCode();
                QueryWrapper<Course> queryWrapper2 = new QueryWrapper<>();
                queryWrapper2.eq("code",code);
                Course course = courseService.getOne(queryWrapper2);
                long courseId = course.getId();
                for(int i=0;i<list.size();i++ ) {
                    String email = list.get(i).getStudentEmail();
                    QueryWrapper<CourseStudent> queryWrapper3 = new QueryWrapper<>();
                    queryWrapper3.eq("course_id", courseId)
                            .eq("student_email", email);
                    CourseStudent courseStudent = courseStudentService.getOne(queryWrapper3);
                    int exp = courseStudent.getExp();
                    CourseStudent courseStudent1 = new CourseStudent();
                    courseStudent1.setExp(exp + systemExp);
                    courseStudentService.update(courseStudent1, queryWrapper3);

                    //更新总经验值
                    QueryWrapper<User> queryWrapper1 = new QueryWrapper<>();
                    queryWrapper1.eq("email", email);
                    User user = userService.getOne(queryWrapper1);
                    int exp1 = user.getExp() + systemExp;
                    user.setExp(exp1);
                    userService.update(user, queryWrapper1);
                }
            }

        }else{
            Attendence attendence = new Attendence();
            attendence.setId(attend_id);
            attendence.setIsDelete(2);
            attendenceService.updateById(attendence);
        }
        return ResultUtil.success();
    }

    //教师发起签到的历史记录
    @ResponseBody
    @RequestMapping(method = RequestMethod.PATCH)
    public String historyAttendence(@RequestBody JSONObject jsonObject) {
        Map map = JSON.toJavaObject(jsonObject, Map.class);
        String code = map.get("code").toString();
        QueryWrapper<Attendence> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("code",code)
                .eq("is_delete",1)
                .orderByDesc("id");
        List<Attendence> list = attendenceService.list(queryWrapper);
        //根据课程号查找课程id
        QueryWrapper<Course> queryWrapper1= new QueryWrapper<>();
        queryWrapper1.eq("code",code);
        Course course = courseService.getOne(queryWrapper1);
        long courseId = course.getId();
        //查询该课程总学生数
        QueryWrapper<CourseStudent> queryWrapper2 = new QueryWrapper<>();
        queryWrapper2.eq("course_id",courseId)
                .eq("is_delete",0);
        int total = courseStudentService.count(queryWrapper2);
        JSONArray jsonArray = new JSONArray();
        for(int i=0;i<list.size();i++){
            String startTime = list.get(i).getStartTime();
            int attendId = list.get(i).getId();
            QueryWrapper<AttendenceResult> queryWrapper3 = new QueryWrapper<>();
            queryWrapper3.eq("attend_id",attendId)
                .eq("is_delete",0);
            int count = attendenceResultService.count(queryWrapper3);
            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("time",startTime);
            jsonObject1.put("count",count);
            jsonObject1.put("total",total);
            jsonObject1.put("attend_id",attendId);
            jsonArray.add(jsonObject1);
        }
        return jsonArray.toString();
    }
}

