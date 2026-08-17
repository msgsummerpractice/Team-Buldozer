package com.example.CheckInApp.dto.response;

import com.example.CheckInApp.model.EventLocation;
import com.example.CheckInApp.model.EventStatus;
import com.example.CheckInApp.model.EventType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventResponse {
    private Long id;
    private String name;
    private EventLocation location;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private EventType type;
    private EventStatus status;
    private byte[] poster;
    private LocalDate registrationStartDate;
    private LocalDate registrationEndDate;
    private String address;
    private String description;
    private Long createdById;
    private Boolean foodProvided;
    private LocalDateTime createdAt;
}
