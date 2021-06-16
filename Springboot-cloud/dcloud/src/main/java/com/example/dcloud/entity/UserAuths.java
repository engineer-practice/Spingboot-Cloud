package com.example.dcloud.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;

@TableName(value = "user_auths")
public class UserAuths extends Model<UserAuths> {

    private static final long serialVersionUID = 1L;

    /**
     * id,自增
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Integer user_id;

    private String identity_type;

    private String identifier;

    private String credentail;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "UserAuths{" +
                "id=" + id +
                ", user_id=" + user_id +
                ", identity_type='" + identity_type + '\'' +
                ", identifier='" + identifier + '\'' +
                ", credentail='" + credentail + '\'' +
                '}';
    }

    public Integer getUser_id() {
        return user_id;
    }

    public void setUser_id(Integer user_id) {
        this.user_id = user_id;
    }

    public String getIdentity_type() {
        return identity_type;
    }

    public void setIdentity_type(String identity_type) {
        this.identity_type = identity_type;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getCredentail() {
        return credentail;
    }

    public void setCredentail(String credentail) {
        this.credentail = credentail;
    }
}
