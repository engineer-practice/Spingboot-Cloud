package com.example.dcloud.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;


public class Dictionary extends Model<Dictionary> {

private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 英文标识
     */
    @TableField(value = "code")
    private String code;//分类标识

    /**
     * 中文标识
     */
    @TableField(value = "name")
    private String name;

    @TableField(value = "is_delete")
    private Integer isDelete;

    /**
     * 说明
     */
    private String description;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(Integer isDelete) {
        this.isDelete = isDelete;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

    @Override
    public String toString() {
        return "Dictionary{" +
        "id=" + id +
        ", code=" + code +
        ", name=" + name +
        ", isDelete=" + isDelete +
        ", description=" + description +
        "}";
    }
}
