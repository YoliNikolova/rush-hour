package com.prime.rushhour.exception;

public class ForbiddenException extends RuntimeException{
    public ForbiddenException(){
        super("No access rights!");
    }
}
