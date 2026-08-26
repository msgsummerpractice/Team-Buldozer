package com.example.CheckInApp.dto.response;

import java.time.Instant;

public record CheckInResponse(
        Long eventId,
        String eventName,
        Instant checkInTime
) {
}
