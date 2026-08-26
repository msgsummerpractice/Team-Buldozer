package com.example.CheckInApp.dto.response;

import com.example.CheckInApp.model.FoodPreference;
import com.example.CheckInApp.model.RegistrationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationResponse {

    private Long id;
    private Long eventId;
    private Long userId;
    private Instant registrationDate;
    private Boolean gdprConsent;
    private Boolean photoConsent;
    private FoodPreference foodPreference;
    private Boolean transportNeeded;
    private String driverName;
    private String driverPhoneNumber;
    private Boolean accommodationNeeded;
    private Integer accommodationDays;
    private RegistrationStatus status;

}
