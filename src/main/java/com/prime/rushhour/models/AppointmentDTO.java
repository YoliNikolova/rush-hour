package com.prime.rushhour.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.prime.rushhour.entities.Activity;

import java.sql.Date;
import java.util.List;

public class AppointmentDTO {
    @JsonProperty("startDate")
    private Date startDate;
    @JsonProperty("endDate")
    private Date endDate;
    private List<Activity> activities;

    public AppointmentDTO() {

    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public List<Activity> getActivities() {
        return activities;
    }

    public void setActivities(List<Activity> activities) {
        this.activities = activities;
    }
}
