package com.prime.rushhour.exception;

public class UserNotFoundException extends RuntimeException{
    public UserNotFoundException(){
        super("Not found this user!");
    }
}
