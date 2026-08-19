package com.example.CheckInApp.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {

    ERR_01("Invalid credentials"),
    ERR_02("User not found"),
    ERR_04("Token expired"),
    ERR_05("Invalid token"),

    ERR_10("Email already exists"),
    ERR_11("Invalid email format"),
    ERR_12("Password too weak"),
    ERR_13("Required field missing"),

    ERR_20("Resource not found"),
    ERR_21("Resource already exists"),

    ERR_30("Insufficient permissions"),
    ERR_31("Access denied"),

    ERR_40("Invalid input"),
    ERR_41("Required parameter missing"),
    ERR_99("Unknown error"),

    ERR_110("Poster could not be read"),
    ERR_111("File is invalid"),
    ERR_112("Event data is invalid"),
    ERR_113("Event is not editable"),
    ERR_114("Last admin can not be disabled or have its role changed ♥"),
    ERR_115("Database error :("),
    ERR_116("QR code could not be generated");

    private final String defaultMessage;

    ErrorCode(String defaultMessage) {
        this.defaultMessage = defaultMessage;
    }

    public String getCode() {
        return this.name().replace("_", "-");
    }
}
