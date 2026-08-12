package com.example.CheckInApp.mapper.userProfileMapper;

import org.springframework.stereotype.Component;

import com.example.CheckInApp.dto.UserProfile.request.UserProfileRequest;
import com.example.CheckInApp.dto.UserProfile.response.UserProfileResponse;
import com.example.CheckInApp.model.User;

@Component
public class UserProfileMapper {

    public UserProfileResponse toResponse(User user) {
        if (user == null) {
            return null;
        }

        return UserProfileResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .location(user.getLocation())
                .status(user.isStatus())
                .roles(user.getRoles())
                .build();
    }

    public User toEntity(UserProfileRequest request, User existingUser) {
        if(request.getFirstName() != null) {
            existingUser.setFirstName(request.getFirstName());
        }
        if(request.getLastName() != null) {
            existingUser.setLastName(request.getLastName());
        }
        if(request.getEmail() != null) {
            existingUser.setEmail(request.getEmail());
        }
        if(existingUser.getLocation() != null) {
            existingUser.setLocation(request.getLocation());
        }
        return existingUser;
    }

}
