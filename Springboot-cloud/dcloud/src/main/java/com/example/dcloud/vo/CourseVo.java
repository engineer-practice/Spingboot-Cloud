package com.example.dcloud.vo;

public class CourseVo {

    /**
     * 班课名
     */
    private String name;

    /**
     * 班课号
     */
    private String code;

    /**
     *
     */
    private String className;

    /**
     * 学期
     */
    private String semester;



    private String teacherName;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }
}
