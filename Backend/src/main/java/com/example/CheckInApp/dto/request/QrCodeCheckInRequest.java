package com.example.CheckInApp.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QrCodeCheckInRequest {

    @NotNull(message = "Event id cannot be null.")
    private Long eventId;

    @NotBlank(message = "Event name cannot be blank.")
    private String eventName;

}
