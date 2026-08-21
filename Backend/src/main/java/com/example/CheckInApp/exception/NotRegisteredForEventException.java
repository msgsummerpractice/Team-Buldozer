package com.example.CheckInApp.exception;

public class NotRegisteredForEventException extends RuntimeException {
    public NotRegisteredForEventException(String message) {
        super(message);
    }
}
