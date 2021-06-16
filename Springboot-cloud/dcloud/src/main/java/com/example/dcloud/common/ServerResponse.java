package com.example.dcloud.common;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Api(value = "响应数据")
public class ServerResponse<T> {
    @ApiModelProperty(value = "总条数")
    private Long total;
    @ApiModelProperty(value = "返回的数据")
    private List<T> dataList;
    @ApiModelProperty(value = "结果（成功/失败）")
    private boolean result;
    @ApiModelProperty(value = "成功或者失败的一些信息")
    private String msg;

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public List<T> getDataList() {
        return dataList;
    }

    public void setDataList(List<T> dataList) {
        this.dataList = dataList;
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
