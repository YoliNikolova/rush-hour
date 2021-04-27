package com.prime.rushhour.exception;

public class AppointmentNotFoundException extends RuntimeException{
    public AppointmentNotFoundException(int id){
        super(String.format("Appointment with id {%d} not found",id));
    }
}
