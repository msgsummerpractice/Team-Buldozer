package com.example.CheckInApp.mapper;

import com.example.CheckInApp.dto.request.UserRequest;
import com.example.CheckInApp.dto.response.UserResponse;
import com.example.CheckInApp.model.User;
import org.springframework.stereotype.Component;


@Component
public class UserMapper {

    public User toEntity(UserRequest request) {
        if (request == null) {
            return null;
        }
        
        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
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
                .build();
    }
}
