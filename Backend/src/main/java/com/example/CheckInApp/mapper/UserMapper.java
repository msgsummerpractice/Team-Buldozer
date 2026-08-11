package com.example.CheckInApp.mapper;

import com.example.CheckInApp.dto.response.UserResponse;
import com.example.CheckInApp.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMapper {

    public UserResponse mapUserToUserResponse(User user) {
        UserResponse userResponse = UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .location(user.getLocation())
                .status(user.isStatus())
                .roles(user.getRoles())
                .build();

        return userResponse;
    }

}
