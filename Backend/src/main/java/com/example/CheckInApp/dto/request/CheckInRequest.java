package com.example.CheckInApp.dto.request;

import com.example.CheckInApp.model.Event;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record CheckInRequest(
        @NotBlank(message = "Check-in code cannot be blank.")
        @Pattern(regexp = Event.CHECKIN_CODE_REGEX, message = "Check-in code must be 6 digits.")
        String checkInCode
) {
}
