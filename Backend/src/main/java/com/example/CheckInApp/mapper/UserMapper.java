package com.example.CheckInApp.mapper;

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

        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setLocation(request.getLocation());
        user.setRoles(request.getRoles());
        user.setStatus(true);

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
                .roles(user.getRoles())
                .profilePicture(user.getProfilePicture() != null ? Base64.getEncoder().encodeToString(user.getProfilePicture()) : null)
                .build();
    }
    
    public User fromProfileToEntity(UserProfileRequest request, User existingUser) {
        if(request.getFirstName() != null) {
            existingUser.setFirstName(request.getFirstName());
        }
        if(request.getLastName() != null) {
            existingUser.setLastName(request.getLastName());
        }
        if(request.getEmail() != null) {
            existingUser.setEmail(request.getEmail());
        }
        if(request.getLocation() != null) {
            existingUser.setLocation(request.getLocation());
        }
        if(request.getProfilePicture() != null) {
            existingUser.setProfilePicture(Base64.getDecoder().decode(request.getProfilePicture()));
        }
        return existingUser;
    }
}