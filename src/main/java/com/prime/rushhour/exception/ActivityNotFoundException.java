package com.prime.rushhour.exception;

public class ActivityNotFoundException extends RuntimeException{
    public ActivityNotFoundException(){
        super("Not found this activity!");
    }
}
