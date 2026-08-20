package com.example.CheckInApp.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CheckInResponse {
    private Long eventId;
    private String eventName;
    private LocalDateTime checkInTime;
}
