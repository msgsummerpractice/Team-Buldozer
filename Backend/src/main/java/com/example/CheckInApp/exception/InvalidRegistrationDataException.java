package com.example.CheckInApp.exception;

public class InvalidRegistrationDataException extends RuntimeException {
    public InvalidRegistrationDataException(String message) {
        super(message);
    }
}
