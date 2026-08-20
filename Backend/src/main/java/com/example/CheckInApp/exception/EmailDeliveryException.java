package com.example.CheckInApp.exception;

import java.util.List;

public class EmailDeliveryException extends RuntimeException {

    private final List<String> failedRecipients;

    public EmailDeliveryException(List<String> failedRecipients) {
        super("Failed to deliver email to: " + failedRecipients);
        this.failedRecipients = failedRecipients;
    }

    public List<String> getFailedRecipients() {
        return failedRecipients;
    }
}
