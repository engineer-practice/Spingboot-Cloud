package com.example.dcloud.vo;

import java.util.Date;

public class getAttendenceVo {
    private String name;
    private Date attendTime;
    private Integer hasAttendence;
    private Double longitude;
    private Double latitude;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getAttendTime() {
        return attendTime;
    }

    public void setAttendTime(Date attendTime) {
        this.attendTime = attendTime;
    }

    public Integer getHasAttendence() {
        return hasAttendence;
    }

    public void setHasAttendence(Integer hasAttendence) {
        this.hasAttendence = hasAttendence;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    private Double distance;
}
