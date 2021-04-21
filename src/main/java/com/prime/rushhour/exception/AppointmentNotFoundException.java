package com.prime.rushhour.exception;

public class AppointmentNotFoundException extends RuntimeException{
    public AppointmentNotFoundException(){
        super("Not found this appointment!");
    }
}
