package com.prime.rushhour.exception;

public class ThisEmailAlreadyExistsException extends RuntimeException{
    public ThisEmailAlreadyExistsException(){
        super("This email already exists!");
    }
}
