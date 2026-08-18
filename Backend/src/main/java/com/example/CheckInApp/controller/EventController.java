package com.example.CheckInApp.controller;

import com.example.CheckInApp.dto.request.EventRequest;
import com.example.CheckInApp.dto.request.EventUpdateRequest;
import com.example.CheckInApp.dto.response.CreateEventResponse;
import com.example.CheckInApp.dto.response.EventResponse;
import com.example.CheckInApp.service.EventService;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestBody;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @PostMapping
    @PreAuthorize("hasRole('MARKETING')")
    public ResponseEntity<CreateEventResponse> addEvent(@Valid @RequestBody EventRequest request, Authentication authentication) {
        CreateEventResponse createdEvent = eventService.addEvent(request, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdEvent);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventResponse> getEventById(@PathVariable Long id, Authentication authentication) {
        EventResponse event = eventService.getEventById(id, authentication.getName());
        return ResponseEntity.ok(event);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('MARKETING')")
    public ResponseEntity<EventResponse> updateEvent(
            @PathVariable Long id,
            @Valid @RequestBody EventUpdateRequest request) {
        EventResponse updatedEvent = eventService.updateEvent(id, request);
        return ResponseEntity.ok(updatedEvent);
    }

    @GetMapping("/{id}/check-in-code")
    @PreAuthorize("hasRole('MARKETING')")
    public ResponseEntity<String> getEventCheckInCode(@PathVariable Long id) {
        return ResponseEntity.ok(eventService.getEventCheckInCode(id));
    }

    @GetMapping
    public ResponseEntity<List<EventResponse>> getAllEvents(Authentication authentication) {
        List<EventResponse> events = eventService.getAllEvents(authentication.getName());
        return ResponseEntity.ok(events);
    }

    @PatchMapping("/{id}/publish")
    @PreAuthorize("hasRole('MARKETING')")
    public ResponseEntity<EventResponse> publishEvent(@PathVariable Long id) {
        EventResponse publishedEvent = eventService.publishEvent(id);
        return ResponseEntity.ok(publishedEvent);
    }

    @PatchMapping("/{id}/complete")
    @PreAuthorize("hasRole('MARKETING')")
    public ResponseEntity<EventResponse> completeEvent(@PathVariable Long id) {
        return ResponseEntity.ok(eventService.completeEvent(id));
    }

}
