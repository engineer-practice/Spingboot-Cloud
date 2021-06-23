package com.example.dcloud.vo;

import java.util.Date;

public class historyAttendenceVo {
    private Date startTime;
    private Integer total;
    private Integer count;
    private Integer attendId;

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Integer getAttendId() {
        return attendId;
    }

    public void setAttendId(Integer attendId) {
        this.attendId = attendId;
    }
}
