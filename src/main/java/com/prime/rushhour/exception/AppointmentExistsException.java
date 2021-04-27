package com.prime.rushhour.exception;

public class AppointmentExistsException extends RuntimeException{
    public AppointmentExistsException(){
        super("There is already an appointment at that time");
    }
}

