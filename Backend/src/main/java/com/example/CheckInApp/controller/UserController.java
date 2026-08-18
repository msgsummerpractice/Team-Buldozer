package com.example.CheckInApp.controller;

import com.example.CheckInApp.dto.request.UserProfileRequest;
import com.example.CheckInApp.dto.response.UserResponse;
import com.example.CheckInApp.service.UserService;
import com.example.CheckInApp.dto.request.UserRequestByAdmin;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        UserResponse user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @PatchMapping("/profile/{id}")
    public ResponseEntity<UserResponse> updateUserProfile(@PathVariable Long id, @Valid @RequestBody UserProfileRequest userProfileRequest) {
        UserResponse updatedUserProfile = userService.updateUserProfile(id, userProfileRequest);
        return ResponseEntity.ok(updatedUserProfile);
    }

    @PatchMapping("/{id}/status-roles")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> updateUserStatusAndRoles(@PathVariable Long id, @Valid @RequestBody UserRequestByAdmin userRequestByAdmin) {
        UserResponse updatedUser = userService.updateUserStatusAndRoles(id, userRequestByAdmin);
        return ResponseEntity.ok(updatedUser);
    }

}
