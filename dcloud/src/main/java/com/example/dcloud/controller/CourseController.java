package com.example.dcloud.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.example.dcloud.common.ServerResponse;
import com.example.dcloud.dto.CourseDto;
import com.example.dcloud.dto.UpdateCourseDto;
import com.example.dcloud.entity.*;
import com.example.dcloud.service.*;
import com.example.dcloud.util.QRCodeGenUtil;
import com.example.dcloud.util.ResultUtil;
import com.example.dcloud.vo.CourseStudentVo;
import com.example.dcloud.vo.CourseVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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

@Api(tags = "班课管理接口")
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
    @RequestMapping(value = "/courseInfo",method = RequestMethod.GET)
    @ApiOperation(value = "根据班课号，获取具体班课信息",notes = "get")
    public ServerResponse<Course> courseInfo(@RequestParam(value="code")String code) {
        ServerResponse<Course> response = new ServerResponse<>();

        QueryWrapper<Course> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("code",code);
        int count = courseService.count(queryWrapper);
        if(count == 0)
        {
            response.setMsg("不存在此班课");
            response.setResult(false);
            return response;
        }
        Course course = courseService.getOne(queryWrapper);
        List<Course> dataList = new ArrayList<>();
        dataList.add(course);
        response.setDataList(dataList);
        response.setResult(true);
        response.setMsg("查询成功");
//        CourseVo courseVo = new CourseVo();
//        courseVo.setCode(course.getCode());
//        courseVo.setName(course.getName());
//        courseVo.setFlag(course.getFlag());
//        courseVo.setIsJoin(course.getIsJoin());

        return response;
    }
    @ResponseBody
    @RequestMapping(value = "/hasJoined",method = RequestMethod.GET)
    @ApiOperation(value = "根据手机号，查询用户已经加入的课程",notes = "get")
    public ServerResponse<CourseVo> hasJoined(@RequestParam(value="telephone")String telephone) {

        ServerResponse<CourseVo> response = new ServerResponse<>();
        QueryWrapper<User> queryUser = new QueryWrapper<>();
        queryUser.eq("telephone",telephone)
                .eq("is_delete",0);
        User user = userService.getOne(queryUser);
        QueryWrapper<CourseStudent> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("student_id",user.getId())
                .eq("is_delete",0);
        int count = courseStudentService.count(queryWrapper);
        if(count == 0)
        {
            response.setMsg("没有加入任何班课！");
            response.setResult(false);
            return response;
        }

        //找到对应的课程id
        List<CourseStudent> list = courseStudentService.list(queryWrapper);

        List<CourseVo> dataList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            CourseVo courseVo = new CourseVo();
            QueryWrapper<Course> queryWrapper1 = new QueryWrapper<>();
            queryWrapper1.eq("id", list.get(i).getCourseId());
            Course course = courseService.getOne(queryWrapper1);
            courseVo.setName(course.getName());
            //根据教师id找教师名字
            QueryWrapper<User> queryWrapper2 = new QueryWrapper<>();
            queryWrapper2.eq("id", course.getTeacherId());
            User user1 = userService.getOne(queryWrapper2);
            courseVo.setTeacherName(user1.getName());
            courseVo.setClassName(course.getClassName());
            courseVo.setSemester(course.getSemester());
            courseVo.setCode(course.getCode());
            dataList.add(courseVo);
        }
        response.setDataList(dataList);
        response.setMsg("查询成功");
        response.setResult(true);
        response.setTotal((long)list.size());
        return response;
    }
    @ResponseBody
    @RequestMapping(value = "/hasCreated",method = RequestMethod.GET)
    @ApiOperation(value = "根据手机号，查询用户已经创建的课程",notes = "get")
    public ServerResponse<CourseVo> hasCreated(@RequestParam(value="telephone")String telephone) {

        ServerResponse<CourseVo> response = new ServerResponse<>();
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("telephone",telephone);
        User user = userService.getOne(queryWrapper);
        //找到该userid对应的课程
        QueryWrapper<Course> courseQueryWrapper = new QueryWrapper<>();
        courseQueryWrapper.eq("teacher_id",user.getId())
                .eq("isDelete",0);
        int count = courseService.count(courseQueryWrapper);
        if(count == 0)
        {
            response.setMsg("没有创建任何班课！");
            response.setResult(false);
            return response;
        }
        List<Course> list = courseService.list(courseQueryWrapper);

        List<CourseVo> dataList = new ArrayList<>();
        for(int i = 0 ; i < list.size(); i++) {
            CourseVo courseVo = new CourseVo();
            courseVo.setCode(list.get(i).getCode());
            courseVo.setSemester(list.get(i).getSemester());
            courseVo.setClassName(list.get(i).getClassName());
            courseVo.setTeacherName(user.getName());
            courseVo.setName(list.get(i).getName());
            dataList.add(courseVo);
        }
        response.setDataList(dataList);
        response.setTotal((long)list.size());
        response.setResult(true);
        response.setMsg("查询成功!");
        return response;
    }



    @ResponseBody
    @ApiOperation(value = "获取班课成员",notes = "get")
    @RequestMapping(value = "/getMember",method = RequestMethod.GET)
    //获取班课成员
    public ServerResponse<CourseStudentVo> getMember(@RequestParam(value="code")String code
                            //@RequestParam(value="order",required = false)String order,
                            //@RequestParam(value="search",required = false)String search,
                           // @RequestParam(value="email",required = false)String email
    ){
        ServerResponse<CourseStudentVo> response = new ServerResponse<CourseStudentVo>();
        QueryWrapper<Course> courseQuery = new QueryWrapper<>();
        courseQuery.eq("code",code);
        Course course = courseService.getOne(courseQuery);
        QueryWrapper<CourseStudent> courseId = new QueryWrapper<>();
        courseId.eq("course_id",course.getId())
        .eq("is_delete",0);
        //获得课程的所有学生id

        List<CourseStudent> list = courseStudentService.list(courseId);
        if(list.size()==0){
            response.setMsg("该课程暂无学生！");
            response.setResult(false);
            return response;
        }
//        System.out.println("code:"+code);
//        System.out.println("order:"+order);
        List<CourseStudentVo> dataList= new ArrayList<>();
        CourseStudentVo courseStudentVo = new CourseStudentVo();
        for(int i = 0 ; i < list.size(); i++){

            QueryWrapper<User> queryUser = new QueryWrapper<>();
            queryUser.eq("id",list.get(i).getStudentId());
            User user = userService.getOne(queryUser);
            if(list.get(i).getExp() == null){
               courseStudentVo.setExp(0);
            }else{
                courseStudentVo.setExp(list.get(i).getExp());
            }
            courseStudentVo.setName(user.getName());
            courseStudentVo.setSno(user.getSno());
            courseStudentVo.setSex(user.getSex());
            dataList.add(courseStudentVo);
        }
        response.setResult(true);
        response.setMsg("查询成功");
        response.setDataList(dataList);
        response.setTotal((long)list.size());
        return response;

    }
    @ApiOperation(value = "学生使用班课号加入班课",notes = "get")
    @ResponseBody
    @RequestMapping(method = RequestMethod.POST)
    public String JoinClass(@RequestParam(value="code")String code,
                             @RequestParam(value="telephone")String telephone) {
            //学生使用班课号加入班课
            QueryWrapper<Course> queryWrapper = new QueryWrapper();
            queryWrapper.eq("code", code)
                    .eq("isDelete", 0);
            int count1 = courseService.count(queryWrapper);
            if(count1 == 0)
                return ResultUtil.error("班课号不存在！");
            Course course = courseService.getOne(queryWrapper);
            if(course.getFlag()==1)
                return ResultUtil.error("班课已经结束！");
        if(course.getIsJoin()==0)
            return ResultUtil.error("班课不允许加入！");
            if (course != null) {
              //  if (email != null) {
                    //不能加入自己创建的课程
                    QueryWrapper<User> queryUser = new QueryWrapper();
                queryUser.eq("telephone", telephone)
                        .eq("is_delete", 0);
                User user1 = userService.getOne(queryUser);
                if(user1.getId()==course.getTeacherId())
                {
                    return ResultUtil.error("不能加入自己创建的班课！");
                }
//                    QueryWrapper<CourseStudent> queryMyCourse = new QueryWrapper();
//                    queryMyCourse.eq("course_id", course.getId())
//                            .eq("teacher_email", email);
//
//                    int count1 = courseStudentService.count(queryMyCourse);
//                    if (count1 > 0) {
//                        return ResultUtil.error("不能加入自己创建的班课！");
//                    }
                    //判断是否已经加入过
                    QueryWrapper<CourseStudent> queryJoined = new QueryWrapper();
                    queryJoined.eq("course_id", course.getId())
                            .eq("student_id", user1.getId())
                            .eq("is_delete", 0);
                    int count = courseStudentService.count(queryJoined);
                    if (count > 0) {
                        return ResultUtil.error("您已加入本班课，请勿重复加入！");
                    } else {
                        //courseStudent更新
                        CourseStudent courseStudent = new CourseStudent();
                        courseStudent.setCourseId(course.getId());
                        courseStudent.setStudentId(user1.getId());
//                        QueryWrapper<User> queryWrapper1 = new QueryWrapper();
//                        queryWrapper1.eq("id", course.getTeacherId());
//                        User user = userService.getOne(queryWrapper1);
//                        courseStudent.setTeacherEmail(user.getEmail());
//                        courseStudent.setStudentEmail(email);//学生
                        courseStudentService.save(courseStudent);
                        return ResultUtil.success();
                    }
                }
                //else {
//                    JSONObject jsonObject1 = new JSONObject();
//                    jsonObject1.put("class", course.getClassName());
//                    jsonObject1.put("name", course.getName());
//                    QueryWrapper<User> queryWrapper1 = new QueryWrapper();
//                    queryWrapper1.eq("id", course.getTeacherId());
//                    User user = userService.getOne(queryWrapper1);
//                    jsonObject1.put("tname", user.getName());
//                    if (course.getFlag() == 1) {
//                        jsonObject1.put("schoolLesson", "学校课表班课");
//                    } else {
//                        jsonObject1.put("schoolLesson", "非学校课表班课");
//                    }
//                    return JSON.toJSONString(jsonObject1);
////                    return ResultUtil.error("请确认是否加入班课");
//                }

          //  }
            else {
                return ResultUtil.error("班课号不存在！");
            }

    }
//    @ApiOperation(value = "创建班课",notes = "get")
//    @ResponseBody
//    @RequestMapping(value = "/CreateClass",method = RequestMethod.POST)
//    public String CreateClass(CourseDto courseDto) {
//        //老师 创建班课
//            Course course = new Course();
//            course.setClassName(courseDto.getClassName());
//            course.setName(courseDto.getName());
//            course.setSchoolCode(courseDto.getSchool());
//            course.setFlag(courseDto.getIsSchoolLesson());
//            //随机生成课程号
////        course.setCode("11111111");
//            int count = 1;
//            String code = "";
//            do{
//                StringBuilder str = new StringBuilder();
//                for (int i = 0; i < 7; i++) {
//                    if (i == 0 && 8 > 1){
//                        str.append(new Random().nextInt(9) + 1);
//                    }else {
//                        str.append(new Random().nextInt(10));
//                    }
//                }
//                code = str.toString();
//                //查看数据库中是否存在 若存在则重新生成
//                QueryWrapper codeQuery = new QueryWrapper();
//                codeQuery.eq("code",code);
//                count = courseService.count(codeQuery);
//            }while(count > 0);
//            course.setCode(code);
//
//         //   QRCodeGenUtil.generateQRCodeImage(code,350,350,System.getProperty("user.dir"));
//           // course.setQr_code(System.getProperty("user.dir")+);
//            course.setLearnRequire(courseDto.getRequire());
//            course.setExamSchedule(courseDto.getExamination());
//            course.setSemester(courseDto.getTerm());
//            //通过邮箱找userid
//            QueryWrapper queryWrapper = new QueryWrapper();
//            queryWrapper.eq("telephone",courseDto.getTelephone());
//            User user = userService.getOne(queryWrapper);
//            course.setTeacherId(parseInt(user.getId().toString()));
//            course.setTeachProgress(courseDto.getProcess());
//            course.setIsJoin(1);
//            course.setIsDelete(0);
//            courseService.save(course);
//            return code;
//        }


    @ApiOperation(value = "创建班课",notes = "get")
    @ResponseBody
    @RequestMapping(value = "/CreateClass",method = RequestMethod.POST)
    public String CreateClass(@RequestParam(value="className")String className,
                              @RequestParam(value="name")String name,
                              @RequestParam(value="school",required = false)String school,
                              @RequestParam(value="isFinish",required = false)Integer isFinish,
                              @RequestParam(value="require",required = false)String require,
                              @RequestParam(value="term",required = false)String term,
                              @RequestParam(value="examination",required = false)String examination,
                              @RequestParam(value="telephone")String telephone,
                              @RequestParam(value="process",required = false)String process
                              ) {
        //老师 创建班课
        Course course = new Course();
        course.setClassName(className);
        course.setName(name);
        if(school!=null)
            course.setSchoolCode(school);
        if(isFinish!=null)
            course.setFlag(isFinish);
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

        //   QRCodeGenUtil.generateQRCodeImage(code,350,350,System.getProperty("user.dir"));
        // course.setQr_code(System.getProperty("user.dir")+);
        if(require!=null)
             course.setLearnRequire(require);
        if(examination!=null)
            course.setExamSchedule(examination);
        if(term!=null)
            course.setSemester(term);
        //通过邮箱找userid
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("telephone",telephone);
        User user = userService.getOne(queryWrapper);
        course.setTeacherId(parseInt(user.getId().toString()));
        if(process!=null)
            course.setTeachProgress(process);
        course.setIsJoin(1);
        course.setIsDelete(0);
        courseService.save(course);
        return code;
    }

    //编辑
    @ApiOperation(value = "编辑班课信息",notes = "get")
    @ResponseBody
    @RequestMapping(method = RequestMethod.PATCH)
    public String update(
            //@RequestBody UpdateCourseDto updateCourseDto
            @RequestParam(value="code")String code,
            @RequestParam(value="className",required = false)String className,
            @RequestParam(value="name",required = false)String name,
            @RequestParam(value="isJoin",required = false)Integer isJoin,
            @RequestParam(value="school",required = false)String school,
            @RequestParam(value="isFinish",required = false)Integer isFinish,
            @RequestParam(value="require",required = false)String require,
            @RequestParam(value="term",required = false)String term,
            @RequestParam(value="examination",required = false)String examination,
            @RequestParam(value="process",required = false)String process
    ){
        //        course.setCode(no);

        QueryWrapper<Course> queryWrapper = new QueryWrapper();
        queryWrapper.eq("code",code);
        Course course = courseService.getOne(queryWrapper);
        if(course == null)
            return ResultUtil.error("没有这门课程!");
        if( name != null){
            course.setName(name);
        }

        if(term != null){
            course.setSemester(term);
        }
        if(isJoin != null){
                course.setIsJoin(isJoin);
        }
        if(school!= null){
            course.setSchoolCode(school);
        }
        if(require != null){
            course.setLearnRequire(require);
        }
        if(process != null){
            course.setTeachProgress(process);
        }
        if(examination != null){
            course.setExamSchedule(examination);
        }
        if(className != null){
            course.setClassName(className);
        }
        if(isFinish != null){

                course.setFlag(isFinish);

        }
        courseService.updateById(course);
//        return JSON.toJSONString(courseService.getOne(queryWrapper));
        return ResultUtil.success();
    }

    //删除
    @ResponseBody
    @ApiOperation(value = "退出、删除班课",notes = "get")
    @RequestMapping(method = RequestMethod.DELETE)
    public String delete(@RequestParam(value="code")String code,
                         @RequestParam(value="telephone",required = false)String telephone
                         ){

        QueryWrapper<Course> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("code",code);
        Course course1 = courseService.getOne(queryWrapper);
        if(telephone != null){//退出班课
            QueryWrapper<User> queryUser = new QueryWrapper<>();
            queryUser.eq("telephone",telephone)
                    .eq("is_delete",0);
            User user = userService.getOne(queryUser);
            QueryWrapper<CourseStudent> queryWrapper1 = new QueryWrapper<>();
            queryWrapper1.eq("student_id",user.getId())
                    .eq("course_id",course1.getId());
            CourseStudent courseStudent = new CourseStudent();
            courseStudent.setIsDelete(1);
            courseStudentService.update(courseStudent,queryWrapper1);

            //清空签到历史记录
            QueryWrapper<AttendenceResult> queryHistory = new QueryWrapper<>();
            queryHistory.eq("course_id",course1.getId())
                        .eq("student_id",user.getId());
            AttendenceResult attendenceResult = attendenceResultService.getOne(queryHistory);
            if(attendenceResult != null)
                attendenceResultService.removeById(attendenceResult.getId());
         //   attendenceResultService.update(attendenceResult,queryHistory);
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

