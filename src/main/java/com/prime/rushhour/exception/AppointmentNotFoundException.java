package com.prime.rushhour.exception;

public class AppointmentNotFoundException extends RuntimeException{
    public AppointmentNotFoundException(){
        super("Appointment with this id not found");
    }
}
