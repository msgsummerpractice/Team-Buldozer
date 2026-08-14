package com.example.CheckInApp.dto.mapper;

import java.util.Base64;

import com.example.CheckInApp.dto.response.UserResponse;
import com.example.CheckInApp.dto.request.UserProfileRequest;
import com.example.CheckInApp.dto.request.UserRequest;
import com.example.CheckInApp.model.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    private final PasswordEncoder passwordEncoder;

    public UserMapper(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public User toEntity(UserRequest request) {
        if (request == null) {
            return null;
        }

        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .location(request.getLocation())
                .userRoles(request.getUserRoles())
                .status(true)
                .build();

        return user;
    }

    public UserResponse toResponse(User user) {
        if (user == null) {
            return null;
        }

        return UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .location(user.getLocation())
                .status(user.isStatus())
                .userRoles(user.getUserRoles())
                .profilePicture(user.getProfilePicture() != null ? Base64.getEncoder().encodeToString(user.getProfilePicture()) : null)
                .build();
    }

    public User fromProfileToEntity(UserProfileRequest request, User existingUser) {
        if (request.getFirstName() != null) {
            existingUser.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            existingUser.setLastName(request.getLastName());
        }
        if (request.getEmail() != null) {
            existingUser.setEmail(request.getEmail());
        }
        if (request.getLocation() != null) {
            existingUser.setLocation(request.getLocation());
        }
        if (request.getProfilePicture() != null) {
            existingUser.setProfilePicture(Base64.getDecoder().decode(request.getProfilePicture()));
        }
        return existingUser;
    }
}