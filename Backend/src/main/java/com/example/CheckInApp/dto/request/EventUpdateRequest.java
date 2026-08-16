package com.example.CheckInApp.dto.request;

import com.example.CheckInApp.model.EventLocation;
import com.example.CheckInApp.model.EventType;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventUpdateRequest {

    @Size(min = 3, max = 256, message = "Name must be between 3 and 256 characters long.")
    private String name;

    private EventLocation location;

    private LocalDateTime startDateTime;

    private LocalDateTime endDateTime;

    private EventType type;

    private String poster;

    private LocalDate registrationStartDate;

    private LocalDate registrationEndDate;

    @Size(min = 3, max = 128, message = "Address must be between 3 and 128 characters long.")
    private String address;

    @Size(min = 3, max = 1024, message = "Description must be between 3 and 1024 characters long.")
    private String description;

    private Boolean foodProvided;
}
