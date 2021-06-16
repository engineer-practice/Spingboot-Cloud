package com.example.dcloud.dto;

import lombok.Data;

@Data
public class PagedSortSearchRequest {
    int maxCount;
    int pageIndex;
    String orderBy;
    boolean isASC;
    String search;
}