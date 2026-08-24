package com.example.CheckInApp.exception;

public class UsedResetTokenException extends RuntimeException {
    public UsedResetTokenException(String message) {
        super(message);
    }
}
