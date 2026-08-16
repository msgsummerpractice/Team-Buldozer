package com.example.CheckInApp.dto.request;

import com.example.CheckInApp.model.EventLocation;
import com.example.CheckInApp.model.EventType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventRequest {

    @NotBlank(message = "Name cannot be blank.")
    @Size(min = 3, max = 256, message = "Name must be between 3 and 256 characters long.")
    private String name;

    @NotNull(message = "Location cannot be null.")
    private EventLocation location;

    @NotNull(message = "Start date time cannot be null.")
    private LocalDateTime startDateTime;

    @NotNull(message = "End date time cannot be null.")
    private LocalDateTime endDateTime;

    @NotNull(message = "Type cannot be null.")
    private EventType type;

    MultipartFile poster;

    @NotNull(message = "Registration start date cannot be null.")
    private LocalDate registrationStartDate;

    @NotNull(message = "Registration end date cannot be null.")
    private LocalDate registrationEndDate;

    @NotBlank(message = "Address cannot be blank.")
    @Size(min = 3, max = 128, message = "Address must be between 3 and 128 characters long.")
    private String address;

    @NotBlank(message = "Description cannot be blank.")
    @Size(min = 3, max = 1024, message = "Description must be between 3 and 1024 characters long.")
    private String description;

    private Boolean foodProvided;
}
