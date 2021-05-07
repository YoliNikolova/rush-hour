package com.prime.rushhour.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;
import java.util.List;

public class AppointmentRequestDTO {
    @JsonProperty("startDate")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm", iso = DateTimeFormat.ISO.DATE_TIME)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime startDate;

    private int userId;

    @NotEmpty(message = "No activity names")
    private List<String> activitiesName;

    public AppointmentRequestDTO() {

    }

    public AppointmentRequestDTO(LocalDateTime startDate,List<String> activitiesName){
        this.startDate=startDate;
        this.activitiesName=activitiesName;
    }

    public AppointmentRequestDTO(LocalDateTime startDate,int userId,List<String> activitiesName){
        this.userId=userId;
        this.startDate=startDate;
        this.activitiesName=activitiesName;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public List<String> getActivitiesName() {
        return activitiesName;
    }

    public void setActivitiesName(List<String> activitiesName) {
        this.activitiesName = activitiesName;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
