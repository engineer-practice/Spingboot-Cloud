package com.example.dcloud.dto;

public class CourseDto {
    private String className;
    private String name;
    private String school;
    private Integer isSchoolLesson;
    private String require;
    private String examination;
    private String term;
    private String email;
    private String process;

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProcess() {
        return process;
    }

    public void setProcess(String process) {
        this.process = process;
    }
}
