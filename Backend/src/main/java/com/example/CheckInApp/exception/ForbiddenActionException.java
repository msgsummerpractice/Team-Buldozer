package com.example.CheckInApp.exception;

public class ForbiddenActionException  extends RuntimeException{
    public ForbiddenActionException(String message){
        super(message);
    }
}
