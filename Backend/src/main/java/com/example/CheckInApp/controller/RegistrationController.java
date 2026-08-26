package com.example.CheckInApp.controller;

import com.example.CheckInApp.dto.request.RegistrationRequest;
import com.example.CheckInApp.dto.response.RegistrationResponse;
import com.example.CheckInApp.service.RegistrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/events/{eventId}/registrations")
@RequiredArgsConstructor
public class RegistrationController {

    private final RegistrationService registrationService;

    @PostMapping
    public ResponseEntity<RegistrationResponse> register(
            @PathVariable Long eventId,
            @Valid @RequestBody RegistrationRequest request,
            Authentication authentication) {
        RegistrationResponse response = registrationService.register(eventId, request, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<RegistrationResponse> getMyRegistration(
            @PathVariable Long eventId,
            Authentication authentication) {
        return ResponseEntity.ok(registrationService.getMyRegistration(eventId, authentication.getName()));
    }

    @PutMapping
    public ResponseEntity<RegistrationResponse> editRegistration(
            @PathVariable Long eventId,
            @Valid @RequestBody RegistrationRequest request,
            Authentication authentication) {
        return ResponseEntity.ok(registrationService.editRegistration(eventId, request, authentication.getName()));
    }

    @PatchMapping("/withdraw")
    public ResponseEntity<RegistrationResponse> withdrawRegistration(
            @PathVariable Long eventId,
            Authentication authentication) {
        return ResponseEntity.ok(registrationService.withdrawRegistration(eventId, authentication.getName()));
    }

}
