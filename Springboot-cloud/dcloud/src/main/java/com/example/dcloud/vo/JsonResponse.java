package com.example.dcloud.vo;

import io.swagger.annotations.ApiModelProperty;

public class JsonResponse<T> {
    @ApiModelProperty(value = "总记录数")
    private Long total;                 //总记录数
}
