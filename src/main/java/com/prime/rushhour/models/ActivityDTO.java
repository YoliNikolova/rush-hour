package com.prime.rushhour.models;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;

public class ActivityDTO {
    @NotEmpty
    private String name;

    @Positive
    @Min(value = 5, message = "Minutes should not be less than 5")
    private int minutes;

    @Positive
    private double price;

    public ActivityDTO() {

    }

    public ActivityDTO(String name,int minutes,double price){
        this.name=name;
        this.minutes=minutes;
        this.price=price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
