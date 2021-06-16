package com.example.dcloud.dto;

public class UpdatePasswordDto {
    private String email;
    private String newpassword1;
    private String newpassword2;
    private String oldpassword;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNewpassword1() {
        return newpassword1;
    }

    public void setNewpassword1(String newpassword1) {
        this.newpassword1 = newpassword1;
    }

    public String getNewpasswor2() {
        return newpassword2;
    }

    public void setNewpasswor2(String newpasswor2) {
        this.newpassword2 = newpasswor2;
    }

    public String getOldpassword() {
        return oldpassword;
    }

    public void setOldpassword(String oldpassword) {
        this.oldpassword = oldpassword;
    }
}
