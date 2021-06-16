package com.example.dcloud.dto;

import lombok.Data;

@Data
public class UserDto {
    private String name;
    private String nickname;
    private String image;
    private String sno;
    private Integer sex;
    private String telephone;
    private String email;
    private String password;
    private String birth;
    private Integer roleId;
    private Integer exp;
    private Integer state;
    private String schoolCode;
    private String powerId;
    private Integer education;

    public UserDto() {

    }

    public String getName() {
        return name;
    }

    public UserDto(String name, String nickname, String image, String sno, Integer sex, String telephone, String email, String password, String birth, Integer roleId, Integer exp, Integer state, String schoolCode, String powerId, Integer education) {
        this.name = name;
        this.nickname = nickname;
        this.image = image;
        this.sno = sno;
        this.sex = sex;
        this.telephone = telephone;
        this.email = email;
        this.password = password;
        this.birth = birth;
        this.roleId = roleId;
        this.exp = exp;
        this.state = state;
        this.schoolCode = schoolCode;
        this.powerId = powerId;
        this.education = education;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getSno() {
        return sno;
    }

    public void setSno(String sno) {
        this.sno = sno;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getBirth() {
        return birth;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public Integer getExp() {
        return exp;
    }

    public void setExp(Integer exp) {
        this.exp = exp;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public String getSchoolCode() {
        return schoolCode;
    }

    public void setSchoolCode(String schoolCode) {
        this.schoolCode = schoolCode;
    }

    public String getPowerId() {
        return powerId;
    }

    public void setPowerId(String powerId) {
        this.powerId = powerId;
    }

    public Integer getEducation() {
        return education;
    }

    public void setEducation(Integer education) {
        this.education = education;
    }
}

