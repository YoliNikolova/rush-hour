package com.prime.rushhour.exception;

public class AppointmentExistsException extends RuntimeException{
    public AppointmentExistsException(){
        super("There is already a appointment at that time");
    }
}

