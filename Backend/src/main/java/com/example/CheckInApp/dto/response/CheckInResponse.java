package com.example.CheckInApp.dto.response;

import java.time.LocalDateTime;

public record CheckInResponse(
        Long eventId,
        String eventName,
        LocalDateTime checkInTime
) {
}
