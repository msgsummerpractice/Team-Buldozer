package com.example.CheckInApp.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.CheckInApp.service.UserService;
import com.example.CheckInApp.dto.UserProfile.request.UserProfileRequest;
import com.example.CheckInApp.dto.response.UserResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/users/profile")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserService userService;



    @PatchMapping("/{id}")
    public ResponseEntity<UserResponse> patchUserProfile(@PathVariable Long id, @Valid @RequestBody UserProfileRequest userProfileRequest) {
        UserResponse updatedUserProfile = userService.patchUserProfile(id, userProfileRequest);
        return ResponseEntity.ok(updatedUserProfile);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUserProfile(@PathVariable Long id, @Valid @RequestBody UserProfileRequest userProfileRequest) {
        UserResponse updatedUserProfile = userService.updateUserProfile(id, userProfileRequest);
        return ResponseEntity.ok(updatedUserProfile);
    }

}
