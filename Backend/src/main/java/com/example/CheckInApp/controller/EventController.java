package com.example.CheckInApp.controller;

import com.example.CheckInApp.dto.request.EventRequest;
import com.example.CheckInApp.dto.response.EventResponse;
import com.example.CheckInApp.model.User;
import com.example.CheckInApp.service.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('MARKETING')")
    public ResponseEntity<EventResponse> addEvent(@Valid @ModelAttribute EventRequest request, Authentication authentication) {
        EventResponse createdEvent = eventService.addEvent(request, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdEvent);
    }

}
