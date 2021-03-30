package com.example.dcloud.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

public class DictionaryDetail extends Model<DictionaryDetail> {

private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 编码
     */
    private String code;

    /**
     * 类型编码
     */
    private String typeCode;

    /**
     * 数据项名称
     */
    private String name;

    /**
     * 数据项值
     */
    private String value;

    /**
     * 是否是默认值
     */
    private Integer isDefault;

    private Integer dictOrder;

    private Integer isDelete;

    public Integer getDictOrder() {
        return dictOrder;
    }

    public void setDictOrder(Integer dictOrder) {
        this.dictOrder = dictOrder;
    }

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

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Integer getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Integer isDefault) {
        this.isDefault = isDefault;
    }

    public Integer getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(Integer isDelete) {
        this.isDelete = isDelete;
    }

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

    @Override
    public String toString() {
        return "DictionaryDetail{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", typeCode='" + typeCode + '\'' +
                ", name='" + name + '\'' +
                ", value='" + value + '\'' +
                ", isDefault=" + isDefault +
                ", dictOrder=" + dictOrder +
                ", isDelete=" + isDelete +
                '}';
    }
}
