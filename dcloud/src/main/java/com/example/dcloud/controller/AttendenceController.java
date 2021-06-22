package com.example.dcloud.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.dcloud.common.ServerResponse;
import com.example.dcloud.entity.*;
import com.example.dcloud.service.*;
import com.example.dcloud.util.ResultUtil;
import com.example.dcloud.vo.CourseVo;
import com.example.dcloud.vo.getAttendenceVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.*;

@Api(tags = "签到接口")
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
    //查看这个签到是否结束
    //0表示未结束
    @ApiOperation(value = "查看这个签到是否结束",notes = "get")
    @RequestMapping(value = "/isEnd",method = RequestMethod.POST)
    public String hasAttendence(@RequestParam(value="code")String code) {
        QueryWrapper<Attendence> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("code",code)
                    .eq("is_delete",0);
        int count = attendenceService.count(queryWrapper);
        if(count > 0){
            //有未结束签到
            return ResultUtil.error("签到未结束！");
        }else{
            return ResultUtil.error("签到已结束！");
        }
    }
    //结束签到
    @ResponseBody
    @ApiOperation(value = "结束这个签到",notes = "get")
    @RequestMapping(value = "/end",method = RequestMethod.POST)
    public String endPriorAttendence(@RequestParam(value="code")String code) {
        //Attendence attendence = new Attendence();
        QueryWrapper<Course> queryCourse = new QueryWrapper<>();
        queryCourse.eq("code",code)
                .eq("isDelete",0);
        Course course = courseService.getOne(queryCourse);
        //判断是否有签到正在进行
        QueryWrapper<Attendence> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.eq("course_id", course.getId())
                .eq("is_delete",0);
        int count = attendenceService.count(queryWrapper1);
        if (count == 0)
        {
            return ResultUtil.error("签到均已结束！");
        }
        Attendence attendence = attendenceService.getOne(queryWrapper1);
        attendence.setIsDelete(1);
        attendenceService.update(attendence,queryWrapper1);
        return ResultUtil.success();
    }

    //老师发起签到
    @ResponseBody
    @ApiOperation(value = "发起签到。返回一个签到id，根据签到id去结束签到。返回0说明有签到正在进行，不能再次发起签到",notes = "get")
    @RequestMapping(value = "/createAttendence",method = RequestMethod.POST)
    public int createAttendence(@RequestParam(value="code")String code,
                                @RequestParam(value="start_time")String start_time,
                                @RequestParam(value="end_time",required = false)String end_time,
                                @RequestParam(value="count",required = false)Integer count,
                                @RequestParam(value="longitude")Double longitude,
                                @RequestParam(value="latitude")Double latitude,
                                @RequestParam(value="attendance_type")Integer attendance_type,
                                @RequestParam(value="telephone")String telephone
                                ) {

        Attendence attendence = new Attendence();
        QueryWrapper<Course> queryCourse = new QueryWrapper<>();
        queryCourse.eq("code",code)
        .eq("isDelete",0);
        Course course = courseService.getOne(queryCourse);
        //判断是否有签到正在进行
        QueryWrapper<Attendence> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.eq("course_id", course.getId())
                .eq("is_delete",0);
        int count1 = attendenceService.count(queryWrapper1);
        if(count1>0)
        {
            return 0;
        }
        attendence.setLongitude(longitude);
        attendence.setLatitude(latitude);
        attendence.setAttendanceType(attendance_type);
        QueryWrapper<User> queryUser = new QueryWrapper<>();
        queryUser.eq("telephone",telephone)
                .eq("is_delete",0);
        User user = userService.getOne(queryUser);
        attendence.setCreater(user.getName());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try{
            Date date = sdf.parse(start_time);
            attendence.setStartTime(date);
            attendence.setCreateTime(date);
        }catch (Exception ex)
        {
            System.out.println(ex.getMessage());
        }
        if(end_time!=null)
        {
            try{
                Date date = sdf.parse(end_time);
                attendence.setEndTime(date);
            }catch (Exception ex)
            {
                System.out.println(ex.getMessage());
            }
        }
        if (count!=null)
        {
            attendence.setCount(count);
        }

        attendence.setIsDelete(0);
        attendence.setCourseId(course.getId());
        attendenceService.saveOrUpdate(attendence);
        QueryWrapper<Attendence> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("course_id", course.getId())
               // .eq("start_time", start_time)
                .eq("is_delete",0);
        Attendence attendence1 = attendenceService.getOne(queryWrapper);
        return attendence1.getId();
    }


    @ResponseBody
    @RequestMapping(value = "/checkIn",method = RequestMethod.PUT)
    @ApiOperation(value = "签到",notes = "get")
    public String checkIn(@RequestParam(value="code")String code,
                       @RequestParam(value="telephone")String telephone,
                       @RequestParam(value="longitude")Double longitude,
                       @RequestParam(value="latitude")Double latitude,
                       @RequestParam(value="attend_time")String attend_time
                       ){
        //ban
        //查看签到是否结束
        QueryWrapper<Course> queryCourse = new QueryWrapper<>();
        queryCourse.eq("code",code)
                .eq("isDelete",0);
        Course course = courseService.getOne(queryCourse);
        QueryWrapper<Attendence> queryAttendence = new QueryWrapper<>();
        queryAttendence.eq("course_id",course.getId())
                .eq("is_delete",0);
        //不管签到成功与否，都要新增一条签到的记录
        AttendenceResult attendenceResult = new AttendenceResult();
        attendenceResult.setCourseId(course.getId());
        QueryWrapper<User> queryUser = new QueryWrapper<>();
        queryUser.eq("telephone",telephone)
                .eq("is_delete",0);
        User user = userService.getOne(queryUser);
        attendenceResult.setStudentId(user.getId());
        int count = attendenceService.count(queryAttendence);
        if (count == 0)
        {
            attendenceResult.setLongitude(longitude);
            attendenceResult.setLatitude(latitude);
            attendenceResult.setIsDelete(1);//1表示未签到
            attendenceResultService.save(attendenceResult);
            return ResultUtil.error("签到已结束！");
        }
        //未结束
        Attendence attendence = attendenceService.getOne(queryAttendence);
        QueryWrapper<AttendenceResult> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("student_id",user.getId())
                .eq("attend_id",attendence.getId())
                .eq("is_delete",0);
        int count2 = attendenceResultService.count(queryWrapper);
        if(count2>0)
        {
            return ResultUtil.error("已签到，请勿重复签到！");
        }
        //求距离
        Double distance1 = Math.sqrt((attendence.getLongitude()-longitude)*(attendence.getLongitude()-longitude)+(attendence.getLatitude()-latitude)*(attendence.getLatitude()-latitude));
        //获取系统参数
        QueryWrapper<SystemManage> querySystem = new QueryWrapper<>();
        querySystem.eq("key_name","distance")
                .eq("is_delete",0);
        SystemManage systemManage = systemManageService.getOne(querySystem);
        if (distance1>systemManage.getKeyValue())
        {
            attendenceResult.setLongitude(longitude);
            attendenceResult.setLatitude(latitude);
            attendenceResult.setDistance(distance1);
            attendenceResult.setIsDelete(1);//1表示未签到
            attendenceResultService.save(attendenceResult);
            return ResultUtil.error("签到距离过远，无法签到！");
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try{
            Date date = sdf.parse(attend_time);
            attendenceResult.setAttendTime(date);
        }catch (Exception ex)
        {
            System.out.println(ex.getMessage());
        }
        //保存
        attendenceResult.setIsDelete(0);
        attendenceResult.setLongitude(longitude);
        attendenceResult.setLatitude(latitude);
        attendenceResult.setDistance(distance1);
        attendenceResult.setAttendId(attendence.getId());
        attendenceResultService.save(attendenceResult);
        //对应课程加经验值，课程学生表course_student
        QueryWrapper<CourseStudent> queryCourseStudent = new QueryWrapper<>();
        queryCourseStudent.eq("course_id",course.getId())
                .eq("student_id",user.getId())
                .eq("is_delete",0);
        CourseStudent courseStudent = courseStudentService.getOne(queryCourseStudent);
        //获取系统参数
        QueryWrapper<SystemManage> querySystem1 = new QueryWrapper();
        querySystem1.eq("key_name","experience")
                .eq("is_delete",0);
        SystemManage systemManage1 = systemManageService.getOne(querySystem1);
        //增加该门课程的经验值
        courseStudent.setExp(courseStudent.getExp()+systemManage1.getKeyValue());
        courseStudentService.update(courseStudent,queryCourseStudent);
        //增加总的经验值
        user.setExp(user.getExp()+systemManage1.getKeyValue());
        userService.update(user,queryUser);
        return ResultUtil.success();
    }

    @ResponseBody
    @ApiOperation(value = "查看某门课，某次学生的签到情况",notes = "get")
    @RequestMapping(method = RequestMethod.PUT)
    public ServerResponse<getAttendenceVo> getAttendence(@RequestParam(value="code")String code,
                                                         @RequestParam(value="attend_id")Integer attend_id) {

        ServerResponse<getAttendenceVo> response = new ServerResponse<>();

        QueryWrapper<AttendenceResult> queryWrapper = new QueryWrapper<>();
        QueryWrapper<Course> queryWrapper2= new QueryWrapper<>();
        queryWrapper2.eq("code",code);
        Course course = courseService.getOne(queryWrapper2);
        queryWrapper.eq("attend_id",attend_id)
        .eq("course_id",course.getId());
//        QueryWrapper<Attendence> queryAttendence = new QueryWrapper<>();
//        queryAttendence.eq("id",attend_id);
//        Attendence attendence = attendenceService.getOne(queryAttendence);
        List<AttendenceResult> list = attendenceResultService.list(queryWrapper);
        List<getAttendenceVo> dataList = new ArrayList<>();
        for(int i=0;i<list.size();i++){
            getAttendenceVo getAttendenceVo = new getAttendenceVo();
            int studentId = list.get(i).getStudentId();
            QueryWrapper<User> queryWrapper1 = new QueryWrapper<>();
            queryWrapper1.eq("id",studentId);
            User user = userService.getOne(queryWrapper1);
            getAttendenceVo.setName(user.getName());
            getAttendenceVo.setAttendTime(list.get(i).getAttendTime());
            getAttendenceVo.setDistance(list.get(i).getDistance());
            getAttendenceVo.setHasAttendence(list.get(i).getIsDelete());//0代表已签到，1代表未签到
            getAttendenceVo.setLongitude(list.get(i).getLongitude());
            getAttendenceVo.setLatitude(list.get(i).getLatitude());
            dataList.add(getAttendenceVo);
        }
        response.setResult(true);
        response.setDataList(dataList);
        response.setTotal((long)list.size());
        response.setMsg("查询成功！");

        return response;
    }

//    //手工签到
//    @ResponseBody
//    @RequestMapping(value = "/hand",method = RequestMethod.PUT)
//    @ApiOperation(value = "手工签到，直接给每个学生签到",notes = "get")
//    public int addResultByHand(@RequestBody JSONObject jsonObject){
//        Map map = JSON.toJavaObject(jsonObject, Map.class);
//        String code = map.get("code").toString();
//        Attendence attendence = new Attendence();
//        attendence.setCode(map.get("code").toString());
//        attendence.setLocal("1111");
//        Date d = new Date();
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ");
//        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
//        attendence.setStartTime(sdf.format(d));
//        attendence.setIsDelete(0);
//        attendenceService.saveOrUpdate(attendence);
//        QueryWrapper<Attendence> queryWrapper = new QueryWrapper<>();
//        queryWrapper.eq("code", map.get("code").toString())
//                .eq("start_time", sdf.format(d))
//                .eq("is_delete",0);
//        Attendence attendence1 = attendenceService.getOne(queryWrapper);
//        int attendId = attendence1.getId();
//
//        //根据课程号查找课程id
//        QueryWrapper<Course> queryWrapper2= new QueryWrapper<>();
//        queryWrapper2.eq("code",code);
//        Course course = courseService.getOne(queryWrapper2);
//        long courseId = course.getId();
//        //查询该课程总学生数
//        QueryWrapper<CourseStudent> queryWrapper3 = new QueryWrapper<>();
//        queryWrapper3.eq("course_id",courseId)
//                .eq("is_delete",0);
//        List<CourseStudent> list = courseStudentService.list(queryWrapper3);
//
//        //获取设定的经验值参数
//        List<SystemManage> list1 = systemManageService.list();
//      //  int systemExp = list1.get(0).getAttendExp();
//
//        for(int i=0;i<list.size();i++){
//            //新增签到记录
//            AttendenceResult attendenceResult = new AttendenceResult();
//            attendenceResult.setAttendTime(sdf.format(d));
//            attendenceResult.setCode(code);
//           // attendenceResult.setStudentEmail(list.get(i).getStudentEmail());
//            attendenceResult.setAttendId(attendId);
//            attendenceResult.setIsDelete(0);
//            attendenceResultService.save(attendenceResult);
//
//            //增加该学生在该课程的经验值
//            CourseStudent courseStudent =new CourseStudent();
//            courseStudent.setId(list.get(i).getId());
//        //    courseStudent.setExp(list.get(i).getExp()+systemExp);
//            courseStudentService.updateById(courseStudent);
//
//            //更新该学生总经验值
//            QueryWrapper<User> queryWrapper1 = new QueryWrapper<>();
//         //   queryWrapper1.eq("email",list.get(i).getStudentEmail());
//            User user = userService.getOne(queryWrapper1);
//          //  int exp = user.getExp()+systemExp;
//       //     user.setExp(exp);
//            userService.update(user,queryWrapper1);
//        }
//        Attendence attendence2 = new Attendence();
//        attendence2.setId(attendId);
//        attendence2.setIsDelete(1);
//        attendenceService.updateById(attendence2);
//        return attendId;
//    }

//    @ResponseBody
//    @ApiOperation(value = "查看某门课，某次学生的签到情况",notes = "get")
//    @RequestMapping(method = RequestMethod.PUT)
//    public String getAttendence(@RequestParam(value="code")String code,
//                                @RequestParam(value="attend_id")String attend_id) {
//
//        QueryWrapper<AttendenceResult> queryWrapper = new QueryWrapper<>();
//        queryWrapper.eq("attend_id",attend_id);
//        List<AttendenceResult> list = attendenceResultService.list(queryWrapper);
//        String[] name = new String[list.size()];
//        for(int i=0;i<list.size();i++){
//            int studentId = list.get(i).getStudentId();
//            QueryWrapper<User> queryWrapper1 = new QueryWrapper<>();
//            queryWrapper1.eq("id",studentId);
//            User user = userService.getOne(queryWrapper1);
//            name[i] = user.getName();
//        }
//        //根据课程号查找课程id
//        QueryWrapper<Course> queryWrapper2= new QueryWrapper<>();
//        queryWrapper2.eq("code",code);
//        Course course = courseService.getOne(queryWrapper2);
//        long courseId = course.getId();
//        //查询该课程总学生数
//        QueryWrapper<CourseStudent> queryWrapper3 = new QueryWrapper<>();
//        queryWrapper3.eq("course_id",courseId)
//                    .eq("is_delete",0);
//        JSONObject jsonObject1 = new JSONObject();
//        jsonObject1.put("name",name);
//        jsonObject1.put("count",attendenceResultService.count(queryWrapper));
//        jsonObject1.put("total",courseStudentService.count(queryWrapper3));
//        return jsonObject1.toString();
//    }
//
//    @ResponseBody
//    @ApiOperation(value = "查看这个签到是否结束",notes = "get")
//    @RequestMapping(method = RequestMethod.DELETE)
//    public String endAttendence(@RequestBody JSONObject jsonObject) {
//        Map map = JSON.toJavaObject(jsonObject, Map.class);
//        int attend_id = Integer.parseInt(map.get("attend_id").toString());
//        String type = map.get("type").toString();
//        QueryWrapper<AttendenceResult> queryWrapper = new QueryWrapper<>();
//        queryWrapper.eq("attend_id",attend_id);
//        //获取设定的经验值参数
//        List<SystemManage> list1 = systemManageService.list();
//     //   int systemExp = list1.get(0).getAttendExp();
//        //type为0是放弃签到，不加经验值，为1是结束，加经验值
//        if(type.equals("1")){
//            Attendence attendence = new Attendence();
//            attendence.setId(attend_id);
//            attendence.setIsDelete(1);
//            attendenceService.updateById(attendence);
//            List<AttendenceResult> list = attendenceResultService.list(queryWrapper);
//            if(list.size() != 0){
//                String code = list.get(0).getCode();
//                QueryWrapper<Course> queryWrapper2 = new QueryWrapper<>();
//                queryWrapper2.eq("code",code);
//                Course course = courseService.getOne(queryWrapper2);
//                long courseId = course.getId();
//                for(int i=0;i<list.size();i++ ) {
//                    String email = list.get(i).getStudentEmail();
//                    QueryWrapper<CourseStudent> queryWrapper3 = new QueryWrapper<>();
//                    queryWrapper3.eq("course_id", courseId)
//                            .eq("student_email", email);
//                    CourseStudent courseStudent = courseStudentService.getOne(queryWrapper3);
//                    int exp = courseStudent.getExp();
//                    CourseStudent courseStudent1 = new CourseStudent();
//                //    courseStudent1.setExp(exp + systemExp);
//                    courseStudentService.update(courseStudent1, queryWrapper3);
//
//                    //更新总经验值
//                    QueryWrapper<User> queryWrapper1 = new QueryWrapper<>();
//                    queryWrapper1.eq("email", email);
//                    User user = userService.getOne(queryWrapper1);
//                 //   int exp1 = user.getExp() + systemExp;
//             //       user.setExp(exp1);
//                    userService.update(user, queryWrapper1);
//                }
//            }
//
//        }else{
//            Attendence attendence = new Attendence();
//            attendence.setId(attend_id);
//            attendence.setIsDelete(2);
//            attendenceService.updateById(attendence);
//        }
//        return ResultUtil.success();
//    }

    //教师发起签到的历史记录
//    @ResponseBody
//    @RequestMapping(method = RequestMethod.PATCH)
//    public String historyAttendence(@RequestBody JSONObject jsonObject) {
//        Map map = JSON.toJavaObject(jsonObject, Map.class);
//        String code = map.get("code").toString();
//        QueryWrapper<Attendence> queryWrapper = new QueryWrapper<>();
//        queryWrapper.eq("code",code)
//                .eq("is_delete",1)
//                .orderByDesc("id");
//        List<Attendence> list = attendenceService.list(queryWrapper);
//        //根据课程号查找课程id
//        QueryWrapper<Course> queryWrapper1= new QueryWrapper<>();
//        queryWrapper1.eq("code",code);
//        Course course = courseService.getOne(queryWrapper1);
//        long courseId = course.getId();
//        //查询该课程总学生数
//        QueryWrapper<CourseStudent> queryWrapper2 = new QueryWrapper<>();
//        queryWrapper2.eq("course_id",courseId)
//                .eq("is_delete",0);
//        int total = courseStudentService.count(queryWrapper2);
//        JSONArray jsonArray = new JSONArray();
//        for(int i=0;i<list.size();i++){
//            String startTime = list.get(i).getStartTime();
//            int attendId = list.get(i).getId();
//            QueryWrapper<AttendenceResult> queryWrapper3 = new QueryWrapper<>();
//            queryWrapper3.eq("attend_id",attendId)
//                .eq("is_delete",0);
//            int count = attendenceResultService.count(queryWrapper3);
//            JSONObject jsonObject1 = new JSONObject();
//            jsonObject1.put("time",startTime);
//            jsonObject1.put("count",count);
//            jsonObject1.put("total",total);
//            jsonObject1.put("attend_id",attendId);
//            jsonArray.add(jsonObject1);
//        }
//        return jsonArray.toString();
//    }
}

