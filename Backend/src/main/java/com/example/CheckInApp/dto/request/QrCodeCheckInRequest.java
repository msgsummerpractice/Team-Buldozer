package com.example.CheckInApp.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record QrCodeCheckInRequest(
        @NotNull(message = "Event id cannot be null.")
        Long eventId,
        @NotBlank(message = "Event name cannot be blank.")
        String eventName
) {
}
