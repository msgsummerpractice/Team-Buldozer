package com.example.CheckInApp.dto.response;

import com.example.CheckInApp.model.EventLocation;
import com.example.CheckInApp.model.EventStatus;
import com.example.CheckInApp.model.EventType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventResponse {
    private Long id;
    private String name;
    private EventLocation location;
    private Instant startDateTime;
    private Instant endDateTime;
    private EventType type;
    private EventStatus status;
    private String poster;
    private Instant registrationStartDate;
    private Instant registrationEndDate;
    private String address;
    private String description;
    private Long createdById;
    private Boolean foodProvided;
    private Instant createdAt;
    private boolean codesGenerated;
    private boolean userRegistered;
    private boolean userCheckedIn;
}
