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
    ERR_116("Email delivery failed"),
    ERR_216("QR code could not be generated"),
    ERR_217("Codes were already generated for this event"),
    ERR_218("Could not generate a unique check-in code"),
    ERR_219("Invalid check-in code"),
    ERR_220("Not registered for this event"),
    ERR_221("Check-in is closed for this event"),
    ERR_222("Invalid QR code check-in"),
    ERR_223("Already checked in for this event"),
    ERR_224("Too many requests"),
    ERR_225("Registration data is invalid"),
    ERR_226("Registration is closed for this event"),
    ERR_227("Already registered for this event"),
    ERR_228("Reset link has expired"),
    ERR_229("Reset link has already been used"),
    ERR_230("Registration has been withdrawn");

    private final String defaultMessage;

    ErrorCode(String defaultMessage) {
        this.defaultMessage = defaultMessage;
    }

    public String getCode() {
        return this.name().replace("_", "-");
    }
}
