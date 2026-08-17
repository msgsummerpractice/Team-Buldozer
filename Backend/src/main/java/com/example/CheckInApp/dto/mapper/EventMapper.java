package com.example.CheckInApp.dto.mapper;

import com.example.CheckInApp.dto.request.EventRequest;
import com.example.CheckInApp.dto.response.EventResponse;
import com.example.CheckInApp.model.Event;
import org.springframework.stereotype.Component;
import java.util.Base64;

@Component
public class EventMapper {

    public Event toEntity(EventRequest request) {
        if (request == null) {
            return null;
        }

        return Event.builder()
                .name(request.getName())
                .startDateTime(request.getStartDateTime())
                .endDateTime(request.getEndDateTime())
                .registrationStartDate(request.getRegistrationStartDate())
                .registrationEndDate(request.getRegistrationEndDate())
                .address(request.getAddress())
                .description(request.getDescription())
                .build();
    }

    public EventResponse toResponse(Event event) {
        if (event == null) {
            return null;
        }

        return EventResponse.builder()
                .id(event.getId())
                .name(event.getName())
                .location(event.getLocation())
                .startDateTime(event.getStartDateTime())
                .endDateTime(event.getEndDateTime())
                .type(event.getType())
                .status(event.getStatus())
                .poster(event.getPoster() != null ? Base64.getEncoder().encodeToString(event.getPoster()) : null)
                .registrationStartDate(event.getRegistrationStartDate())
                .registrationEndDate(event.getRegistrationEndDate())
                .address(event.getAddress())
                .description(event.getDescription())
                .createdById(event.getCreatedBy() != null ? event.getCreatedBy().getId() : null)
                .foodProvided(event.getFoodProvided())
                .createdAt(event.getCreatedAt())
                .build();
    }

}
