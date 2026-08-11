package com.example.CheckInApp.mapper;

import com.example.CheckInApp.dto.request.UpdateUserRequest;
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
        
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setEmail(user.getEmail());
        response.setLocation(user.getLocation());
        response.setStatus(user.isStatus());
        response.setRoles(user.getRoles());
        
        return response;
    }
    public void updateEntityFromRequest(User user, UserRequest request) {
        if (request == null) {
            return;
        }
        
        if (request.getFirstName() != null && !request.getFirstName().isBlank()) {
            user.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null && !request.getLastName().isBlank()) {
            user.setLastName(request.getLastName());
        }
        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            user.setEmail(request.getEmail());
        }
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(request.getPassword());
        }
        if (request.getLocation() != null) {
            user.setLocation(request.getLocation());
        }
        if (request.getRoles() != null && !request.getRoles().isEmpty()) {
            user.setRoles(request.getRoles());
        }
    }

    public void applyPatch(User user, UpdateUserRequest patch) {
        if (patch == null) {
            return;
        }
        
        if (patch.getFirstName() != null && !patch.getFirstName().isBlank()) {
            user.setFirstName(patch.getFirstName());
        }
        if (patch.getLastName() != null && !patch.getLastName().isBlank()) {
            user.setLastName(patch.getLastName());
        }
        if (patch.getEmail() != null && !patch.getEmail().isBlank()) {
            user.setEmail(patch.getEmail());
        }
        if (patch.getPassword() != null && !patch.getPassword().isBlank()) {
            user.setPassword(patch.getPassword());
        }
        if (patch.getLocation() != null) {
            user.setLocation(patch.getLocation());
        }
        if (patch.getRoles() != null && !patch.getRoles().isEmpty()) {
            user.setRoles(patch.getRoles());
        }
        user.setStatus(patch.isStatus());
    }
}
