package com.example.dcloud.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.example.dcloud.entity.*;
import com.example.dcloud.service.*;
import com.example.dcloud.util.ResultUtil;
import org.apache.tomcat.util.json.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.stereotype.Controller;

import javax.jws.soap.SOAPBinding;
import java.util.*;

import static java.lang.Integer.parseInt;
import static java.lang.Long.parseLong;


@CrossOrigin
@Controller
@RequestMapping("/courses")
public class CourseController {
    @Autowired
    private CourseService courseService;
    @Autowired
    private AttendenceResultService attendenceResultService;
    @Autowired
    private CourseStudentService courseStudentService;
    @Autowired
    private UserService userService;

    private static final Logger LOG = LoggerFactory.getLogger(CourseController.class);

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET)
    public String get(@RequestParam(value="student_email",required = false)String student_email,
                      @RequestParam(value="teacher_email",required = false)String teacher_email,
                      @RequestParam(value="search",required = false)String search,
                      @RequestParam(value="code",required = false)String code){

        if(code != null){//获得班课详情
        //传过来的参数如果有code（班课号），就查找具体的班课详情 并返回
            //输出日志
            LOG.info("======CourseController======");
            //code表示班课号
            QueryWrapper<Course> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("code",code);
            Course course = courseService.getOne(queryWrapper);
            JSONObject result = new JSONObject();
            result.put("no",course.getCode());
            result.put("name",course.getName());
//            result.put("type",course.getFlag());
            if(course.getFlag() == 1){
                result.put("type",true);
            }else{
                result.put("type",false);
            }
            if(course.getIsJoin() == 1){
                result.put("checked",true);
            }else{
                result.put("checked",false);
            }
            QueryWrapper<User> userQuery = new QueryWrapper();
            userQuery.eq("id",course.getTeacherId().toString());
            User user = userService.getOne(userQuery);
            result.put("tname",user.getName());
            result.put("term",course.getSemester());
            result.put("school",course.getSchoolCode());
            result.put("require",course.getLearnRequire());
            result.put("process",course.getTeachProgress());
            result.put("test",course.getExamSchedule());
            result.put("class",course.getClassName());
            return JSON.toJSONString(result);

        }else if(student_email != null) {//加入的班课
            //如果code为空 则查找这个学生加入的所有班课 并返回Jason的数组 因为学生有可能加入了很多班课
            QueryWrapper<CourseStudent> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("student_email",student_email)
            .eq("is_delete",0);
            //找到对应的课程id
            List<CourseStudent> list = courseStudentService.list(queryWrapper);
            JSONArray jsonArray = new JSONArray();
            for (int i = 0; i < list.size(); i++) {
                JSONObject result = new JSONObject();
                QueryWrapper<Course> queryWrapper1 = new QueryWrapper<>();
                queryWrapper1.eq("id", list.get(i).getCourseId());
                Course course = courseService.getOne(queryWrapper1);
                result.put("name", course.getName());
                //根据教师id找教师名字
                QueryWrapper<User> queryWrapper2 = new QueryWrapper<>();
                queryWrapper2.eq("id", course.getTeacherId());
                User user = userService.getOne(queryWrapper2);
                result.put("tname", user.getName());
                result.put("class", course.getClassName());
                result.put("term", course.getSemester());
                result.put("no", course.getCode());
                jsonArray.add(result);
            }
            if(search != null){//搜索
                //如果搜索不为空 额外放入index
                return courseService.searchLesson(JSON.toJSONString(jsonArray),search);
            }
            return JSON.toJSONString(jsonArray);
        }else if(teacher_email != null){//创建的班课
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("email",teacher_email);
            User user = userService.getOne(queryWrapper);
            //找到该userid对应的课程
            QueryWrapper<Course> courseQueryWrapper = new QueryWrapper<>();
            courseQueryWrapper.eq("teacher_id",user.getId())
                                .eq("isDelete",0);
            List<Course> list = courseService.list(courseQueryWrapper);
            JSONArray jsonArray = new JSONArray();
            for(int i = 0 ; i < list.size(); i++){
                JSONObject result = new JSONObject();
                result.put("name",list.get(i).getName());
                result.put("tname",user.getName());
                result.put("class",list.get(i).getClassName());
                result.put("term",list.get(i).getSemester());
                result.put("no",list.get(i).getCode());
                jsonArray.add(result);
            }
            if(search != null){//搜索
                //调这个函数额外放进去了index 和上面一个else if一样
                return courseService.searchLesson(JSON.toJSONString(jsonArray),search);
            }
            return JSON.toJSONString(jsonArray);
        }
        //都为code、XXXemail都为空 不显示 直接返回 1
        return "1";
    }
    @ResponseBody
    @RequestMapping(value = "/member",method = RequestMethod.GET)
    //获取班课成员
    public String getMember(@RequestParam(value="code")String code,
                            @RequestParam(value="order",required = false)String order,
                            @RequestParam(value="search",required = false)String search,
                            @RequestParam(value="email",required = false)String email){
        QueryWrapper<Course> courseQuery = new QueryWrapper<>();
        courseQuery.eq("code",code);
        Course course = courseService.getOne(courseQuery);
        QueryWrapper<CourseStudent> courseId = new QueryWrapper<>();
        courseId.eq("course_id",course.getId())
        .eq("is_delete",0);
        //获得课程的所有学生email
        List<CourseStudent> list = courseStudentService.list(courseId);
        if(list.size()==0){
            return ResultUtil.error("该课程没有学生");
        }
        JSONArray jsonArray = new JSONArray();
        for(int i = 0 ; i < list.size(); i++){
            JSONObject jsonObject = new JSONObject();
            QueryWrapper<User> queryUser = new QueryWrapper<>();
            queryUser.eq("email",list.get(i).getStudentEmail());
            User user = userService.getOne(queryUser);
            jsonObject.put("name",user.getName());
            jsonObject.put("sno",user.getSno());
            jsonObject.put("sex",user.getSex());
            jsonObject.put("email",user.getEmail());
            if(list.get(i).getExp() == null){
                jsonObject.put("exp",0);
            }else{
                jsonObject.put("exp",list.get(i).getExp());
            }

            jsonObject.put("email",user.getEmail());
            jsonArray.add(jsonObject);
        }
        if(order.equals("1")){
            String searchResult = courseService.jsonArraySort(JSON.toJSONString(jsonArray),"sno");
            if(search != null){//搜索
                return courseService.searchArray(searchResult,search);
            }
            return searchResult;
        }else{
            String searchResult = courseService.jsonArraySort(JSON.toJSONString(jsonArray),"exp");
            if(search != null){//搜索
                return courseService.searchArray(searchResult,search);
            }
            if(email != null){
                return courseService.getRank(searchResult,email);
            }
            return searchResult;
        }
    }
    //新增
    @ResponseBody
    @RequestMapping(method = RequestMethod.POST)
    public String add(@RequestBody JSONObject jsonObject){
        Map map = JSON.toJavaObject(jsonObject, Map.class);
        if(map.get("code") != null){//学生使用班课号加入班课
            QueryWrapper<Course> queryWrapper = new QueryWrapper();
            queryWrapper.eq("code",map.get("code"))
                        .eq("isDelete",0);
            Course course = courseService.getOne(queryWrapper);
            if(course!=null){
                if(map.get("email") != null){
                    //不能加入自己创建的课程
                    QueryWrapper<CourseStudent> queryMyCourse = new QueryWrapper();
                    queryMyCourse.eq("course_id",course.getId())
                            .eq("teacher_email",map.get("email"));
                    int count1 = courseStudentService.count(queryMyCourse);
                    if (count1 > 0) {
                        return ResultUtil.error("不能加入自己创建的班课！");
                    }
                    //判断是否已经加入过
                    QueryWrapper<CourseStudent> queryJoined = new QueryWrapper();
                    queryJoined.eq("course_id",course.getId())
                                .eq("student_email",map.get("email"))
                                .eq("is_delete",0);
                    int count = courseStudentService.count(queryJoined);
                    if (count > 0) {
                        return ResultUtil.error("您已加入本班课，请勿重复加入！");
                    }else{
                        //courseStudent更新
                        CourseStudent courseStudent = new CourseStudent();
                        courseStudent.setCourseId(course.getId());
                        QueryWrapper<User> queryWrapper1 = new QueryWrapper();
                        queryWrapper1.eq("id",course.getTeacherId());
                        User user = userService.getOne(queryWrapper1);
                        courseStudent.setTeacherEmail(user.getEmail());
                        courseStudent.setStudentEmail(map.get("email").toString());//学生
                        courseStudentService.save(courseStudent);
                        return ResultUtil.success();
                    }
                }else{
                    JSONObject jsonObject1 = new JSONObject();
                    jsonObject1.put("class",course.getClassName());
                    jsonObject1.put("name",course.getName());
                    QueryWrapper<User> queryWrapper1 = new QueryWrapper();
                    queryWrapper1.eq("id",course.getTeacherId());
                    User user = userService.getOne(queryWrapper1);
                    jsonObject1.put("tname",user.getName());
                    if(course.getFlag() == 1){
                        jsonObject1.put("schoolLesson","学校课表班课");
                    }else{
                        jsonObject1.put("schoolLesson","非学校课表班课");
                    }
                    return JSON.toJSONString(jsonObject1);
//                    return ResultUtil.error("请确认是否加入班课");
                }

            }else{
                return ResultUtil.error("班课号不存在！");
            }

        }else{//老师 创建班课
            Course course = new Course();
            course.setClassName(map.get("class").toString());
            course.setName(map.get("name").toString());
            course.setSchoolCode(map.get("school").toString());
            course.setFlag(parseInt(map.get("isSchoolLesson").toString()));
            //随机生成课程号
//        course.setCode("11111111");
            int count = 1;
            String code = "";
            do{
                StringBuilder str = new StringBuilder();
                for (int i = 0; i < 7; i++) {
                    if (i == 0 && 8 > 1){
                        str.append(new Random().nextInt(9) + 1);
                    }else {
                        str.append(new Random().nextInt(10));
                    }
                }
                code = str.toString();
                //查看数据库中是否存在 若存在则重新生成
                QueryWrapper codeQuery = new QueryWrapper();
                codeQuery.eq("code",code);
                count = courseService.count(codeQuery);
            }while(count > 0);
            course.setCode(code);
            course.setLearnRequire(map.get("require").toString());
            course.setExamSchedule(map.get("examination").toString());
            course.setSemester(map.get("term").toString());
            //通过邮箱找userid
            QueryWrapper queryWrapper = new QueryWrapper();
            queryWrapper.eq("email",map.get("email"));
            User user = userService.getOne(queryWrapper);
            course.setTeacherId(parseLong(user.getId().toString()));
            course.setTeachProgress(map.get("process").toString());
            course.setIsJoin(1);
            course.setIsDelete(0);
            courseService.save(course);
            return code;
        }

    }

    //新增
    @ResponseBody
    @RequestMapping(method = RequestMethod.PUT)
    public String put(@RequestBody JSONObject jsonObject){
        Map map = JSON.toJavaObject(jsonObject, Map.class);
        return "新增";
    }


    //编辑
    @ResponseBody
    @RequestMapping(method = RequestMethod.PATCH)
    public String update(@RequestBody JSONObject jsonObject){
        //        course.setCode(no);
        Map map = JSON.toJavaObject(jsonObject, Map.class);
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("code",map.get("code"));
        Course course = courseService.getOne(queryWrapper);
        if(map.get("name") != null){
            course.setName(map.get("name").toString());
        }
        if(map.get("tname") != null){
            QueryWrapper queryWrapper1 = new QueryWrapper();
            queryWrapper1.eq("code",map.get("code"));
            Course course1 = courseService.getOne(queryWrapper);
            QueryWrapper userWrapper2 = new QueryWrapper();
            userWrapper2.eq("id",course.getTeacherId());
            User user = new User();
            user.setName(map.get("tname").toString());
            userService.update(user,userWrapper2);
        }
        if(map.get("term") != null){
            course.setSemester(map.get("term").toString());
        }
        if(map.get("isjoin") != null){
            if(map.get("isjoin").toString().equals("false")){
                course.setIsJoin(1);
            }else{
                course.setIsJoin(0);
            }
        }
        if(map.get("school") != null){
            course.setSchoolCode(map.get("school").toString());
        }
        if(map.get("require") != null){
            course.setLearnRequire(map.get("require").toString());
        }
        if(map.get("process") != null){
            course.setTeachProgress(map.get("process").toString());
        }
        if(map.get("test") != null){
            course.setExamSchedule(map.get("test").toString());
        }
        if(map.get("class") != null){
            course.setClassName(map.get("class").toString());
        }
        if(map.get("type") != null){
            if(map.get("type").toString().equals("true")){
                course.setFlag(1);
            }else{
                course.setFlag(0);
            }
        }
        courseService.updateById(course);
//        return JSON.toJSONString(courseService.getOne(queryWrapper));
        return ResultUtil.success();
    }

    //删除
    @ResponseBody
    @RequestMapping(method = RequestMethod.DELETE)
    public String delete(@RequestBody JSONObject jsonObject){
        Map map = JSON.toJavaObject(jsonObject, Map.class);
        QueryWrapper<Course> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("code",map.get("code"));
        Course course1 = courseService.getOne(queryWrapper);
        if(map.get("email") != null){//退出班课
            QueryWrapper<CourseStudent> queryWrapper1 = new QueryWrapper<>();
            queryWrapper1.eq("student_email",map.get("email"))
                    .eq("course_id",course1.getId());
            CourseStudent courseStudent = new CourseStudent();
            courseStudent.setIsDelete(1);
            courseStudentService.update(courseStudent,queryWrapper1);

            //清空签到历史记录
            QueryWrapper<AttendenceResult> queryHistory = new QueryWrapper<>();
            queryHistory.eq("code",map.get("code"))
                        .eq("student_email",map.get("email"));
            AttendenceResult attendenceResult = new AttendenceResult();
            attendenceResult.setIsDelete(3);
            attendenceResultService.update(attendenceResult,queryHistory);
            return ResultUtil.success();
        }
        Course course = new Course();
        course.setIsDelete(1);
        boolean flag = courseService.update(course,queryWrapper);
        //coursestudent表中对应的课程设置为删除状态
        if(flag){
            QueryWrapper<CourseStudent> queryWrapper1 = new QueryWrapper();
            queryWrapper1.eq("course_id",course1.getId());
            CourseStudent courseStudent = new CourseStudent();
            courseStudent.setIsDelete(1);
            courseStudentService.update(courseStudent,queryWrapper1);

        }
        return ResultUtil.success();
    }
}

