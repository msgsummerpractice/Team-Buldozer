package com.example.CheckInApp.dto.request;

import com.example.CheckInApp.model.FoodPreference;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationRequest {

    private Boolean gdprConsent;

    private Boolean photoConsent;

    private FoodPreference foodPreference;

    private Boolean transportNeeded;

    @Size(min = 2, max = 64, message = "Driver name must be between 2 and 64 characters long.")
    private String driverName;

    @Size(min = 5, max = 12, message = "Driver phone number must be between 5 and 12 characters long.")
    private String driverPhoneNumber;

    private Boolean accommodationNeeded;

    @Positive(message = "Number of accommodation days must be positive.")
    private Integer accommodationDays;

}
