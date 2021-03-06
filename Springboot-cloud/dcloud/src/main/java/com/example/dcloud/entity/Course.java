package com.example.dcloud.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;


public class Course extends Model<Course> {

private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private String name;

    private String code;

    @TableField("className")
    private String className;
    private String semester;
    private String image;
    @TableField("learnRequire")
    private String learnRequire;

    @TableField("teachProgress")
    private String teachProgress;

    @TableField("examSchedule")
    private String examSchedule;
    private String schoolCode;
    private Integer flag;
    private String studentId;
    @TableField("activityId")
    private String activityId;
    @TableField("messageId")
    private String messageId;
    @TableField("isJoin")
    private Integer isJoin;
    @TableField("isDelete")
    private Integer isDelete;
    private Integer teacherId;
    private String qr_code;
    public String getQr_code() {
        return qr_code;
    }

    public void setQr_code(String qr_code) {
        this.qr_code = qr_code;
    }




    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getLearnRequire() {
        return learnRequire;
    }

    public void setLearnRequire(String learnRequire) {
        this.learnRequire = learnRequire;
    }

    public String getTeachProgress() {
        return teachProgress;
    }

    public void setTeachProgress(String teachProgress) {
        this.teachProgress = teachProgress;
    }

    public String getExamSchedule() {
        return examSchedule;
    }

    public void setExamSchedule(String examSchedule) {
        this.examSchedule = examSchedule;
    }

    public String getSchoolCode() {
        return schoolCode;
    }

    public void setSchoolCode(String schoolCode) {
        this.schoolCode = schoolCode;
    }

    public Integer getFlag() {
        return flag;
    }

    public void setFlag(Integer flag) {
        this.flag = flag;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getActivityId() {
        return activityId;
    }

    public void setActivityId(String activityId) {
        this.activityId = activityId;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public Integer getIsJoin() {
        return isJoin;
    }

    public void setIsJoin(Integer isJoin) {
        this.isJoin = isJoin;
    }

    public Integer getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(Integer isDelete) {
        this.isDelete = isDelete;
    }

    public Integer getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Integer teacherId) {
        this.teacherId = teacherId;
    }

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

    @Override
    public String toString() {
        return "Course{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", code='" + code + '\'' +
                ", className='" + className + '\'' +
                ", semester='" + semester + '\'' +
                ", image='" + image + '\'' +
                ", learnRequire='" + learnRequire + '\'' +
                ", teachProgress='" + teachProgress + '\'' +
                ", examSchedule='" + examSchedule + '\'' +
                ", schoolCode='" + schoolCode + '\'' +
                ", flag=" + flag +
                ", studentId='" + studentId + '\'' +
                ", activityId='" + activityId + '\'' +
                ", messageId='" + messageId + '\'' +
                ", isJoin=" + isJoin +
                ", isDelete=" + isDelete +
                ", teacherId=" + teacherId +
                ", qr_code='" + qr_code + '\'' +
                '}';
    }
}
