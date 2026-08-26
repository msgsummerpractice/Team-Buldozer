package com.example.CheckInApp.dto.mapper;

import com.example.CheckInApp.dto.response.RegistrationResponse;
import com.example.CheckInApp.model.Registration;
import org.springframework.stereotype.Component;

@Component
public class RegistrationMapper {

    public RegistrationResponse toResponse(Registration registration) {
        if (registration == null) {
            return null;
        }

        return RegistrationResponse.builder()
                .id(registration.getId())
                .eventId(registration.getEvent().getId())
                .userId(registration.getUser().getId())
                .registrationDate(registration.getRegistrationDate())
                .gdprConsent(registration.getGdprConsent())
                .photoConsent(registration.getPhotoConsent())
                .foodPreference(registration.getFoodPreference())
                .transportNeeded(registration.getTransportNeeded())
                .driverName(registration.getDriverName())
                .driverPhoneNumber(registration.getDriverPhoneNumber())
                .accommodationNeeded(registration.getAccommodationNeeded())
                .accommodationDays(registration.getAccommodationDays())
                .status(registration.getStatus())
                .build();
    }

}
