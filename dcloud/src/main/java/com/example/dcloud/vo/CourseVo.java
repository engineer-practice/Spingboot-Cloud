package com.example.dcloud.vo;

public class CourseVo {
    private Long id;

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

    /**
     * 班课封面
     */
    private String image;

    /**
     * 学习要求
     */
    private String learnRequire;

    /**
     * 教学进度
     */
    private String teachProgress;

    /**
     * 考试安排
     */
    private String examSchedule;

    /**
     * 学校和院系（外键，连接school表）
     */
    private String schoolCode;

    /**
     * 是否学校课表班课
     */
    private Integer flag;



    /**
     * 是否可以加入班课，默认可以，0可以，1不可以
     */
    private Integer isJoin;

    /**
     * 0表示未删除，1表示删除
     */
    private Integer isDelete;

    private Long teacherId;

    private String teacherName;

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

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
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

    public Long getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Long teacherId) {
        this.teacherId = teacherId;
    }


}
