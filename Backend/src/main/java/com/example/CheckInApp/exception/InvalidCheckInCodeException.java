package com.example.CheckInApp.exception;

public class InvalidCheckInCodeException extends RuntimeException {
    public InvalidCheckInCodeException(String message) {
        super(message);
    }
}
