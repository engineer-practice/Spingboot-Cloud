package com.example.dcloud.dto;

public class CourseDto {
    private String className;//班级名字，比如一班、二班
    private String name;//课程名，比如 数据结构，机器学习
    private String school;//学校代码，比如123、134
    private Integer isSchoolLesson;//是否学校课表班课
    private String require;//学习要求
    private String examination;//考试安排
    private String term;//学期
    private String telephone;//加入者的手机号
    private String process;//教学进度

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public Integer getIsSchoolLesson() {
        return isSchoolLesson;
    }

    public void setIsSchoolLesson(Integer isSchoolLesson) {
        this.isSchoolLesson = isSchoolLesson;
    }

    public String getRequire() {
        return require;
    }

    public void setRequire(String require) {
        this.require = require;
    }

    public String getExamination() {
        return examination;
    }

    public void setExamination(String examination) {
        this.examination = examination;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getProcess() {
        return process;
    }

    public void setProcess(String process) {
        this.process = process;
    }
}
