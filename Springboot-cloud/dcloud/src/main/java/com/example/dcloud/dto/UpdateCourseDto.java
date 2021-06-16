package com.example.dcloud.dto;

public class UpdateCourseDto {

    private String code;
    private String tname;
    private String isjoin;
    private String className;
    private String name;
    private String school;

    private String require;
    private String flag;

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getIsjoin() {
        return isjoin;
    }
    public void setIsjoin(String isjoin) {
        this.isjoin = isjoin;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTname() {
        return tname;
    }

    public void setTname(String tname) {
        this.tname = tname;
    }

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



    public String getProcess() {
        return process;
    }

    public void setProcess(String process) {
        this.process = process;
    }

    private String examination;
    private String term;

    private String process;
}
