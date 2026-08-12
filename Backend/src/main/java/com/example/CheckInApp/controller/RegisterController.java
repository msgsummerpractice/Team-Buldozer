package com.example.CheckInApp.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.CheckInApp.dto.request.UserRequest;
import com.example.CheckInApp.dto.response.UserResponse;
import com.example.CheckInApp.service.RegisterService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/register")
@AllArgsConstructor
public class RegisterController {
    
    private final RegisterService registerService;

    @PostMapping
    public ResponseEntity<UserResponse> register(@Valid @RequestBody UserRequest request) {
        UserResponse response = registerService.registerUser(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

}
