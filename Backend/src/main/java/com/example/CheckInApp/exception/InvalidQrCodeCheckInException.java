package com.example.CheckInApp.exception;

public class InvalidQrCodeCheckInException extends RuntimeException {
    public InvalidQrCodeCheckInException(String message) {
        super(message);
    }
}
