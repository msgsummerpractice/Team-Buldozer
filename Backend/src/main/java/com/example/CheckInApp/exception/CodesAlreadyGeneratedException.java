package com.example.CheckInApp.exception;

public class CodesAlreadyGeneratedException extends RuntimeException {
    public CodesAlreadyGeneratedException(String message) {
        super(message);
    }
}
