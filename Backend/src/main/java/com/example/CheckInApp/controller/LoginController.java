package com.example.CheckInApp.controller;

import com.example.CheckInApp.dto.request.LoginRequest;
import com.example.CheckInApp.dto.response.LoginResponse;
import com.example.CheckInApp.exception.AuthenticationException;
import com.example.CheckInApp.service.LoginService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/authentication")
@Slf4j
public class LoginController {

    @Autowired
    private LoginService loginService;
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            LoginResponse response = loginService.authenticate(loginRequest);
            return ResponseEntity.ok(response);
        } catch (BadCredentialsException e) {
            log.warn("Login failed for user: {}", loginRequest.getEmail());
            throw new AuthenticationException("Invalid username or password");
        } catch (Exception e) {
            log.error("Authentication error: {}", e.getMessage());
            throw new AuthenticationException("Authentication failed: " + e.getMessage(), e);
        }
    }
}
