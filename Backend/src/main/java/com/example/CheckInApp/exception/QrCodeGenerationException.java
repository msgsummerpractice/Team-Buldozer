package com.example.CheckInApp.exception;

public class QrCodeGenerationException extends RuntimeException {
    public QrCodeGenerationException(String message) {
        super(message);
    }
}
