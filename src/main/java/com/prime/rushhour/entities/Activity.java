package com.prime.rushhour.entities;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Activity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String name;
    private int minutes;
    private double price;

    @ManyToMany(mappedBy = "activities")
    private List<Appointment> appointments=new ArrayList<>();

    public Activity(){

    }

    public Activity(String name){
        this.name=name;
    }

    public Activity(String name,int minutes,double price){
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

    public void setMinutes(int duration) {
        this.minutes = duration;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
