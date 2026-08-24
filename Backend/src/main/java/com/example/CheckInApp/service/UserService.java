package com.example.CheckInApp.service;

import com.example.CheckInApp.dto.request.UserProfileRequest;
import com.example.CheckInApp.dto.request.UserRequestByAdmin;
import com.example.CheckInApp.dto.response.UserResponse;
import com.example.CheckInApp.dto.mapper.UserMapper;
import com.example.CheckInApp.model.User;
import com.example.CheckInApp.model.UserRole;
import com.example.CheckInApp.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import com.example.CheckInApp.exception.DataBaseException;
import com.example.CheckInApp.exception.DuplicateEmailException;
import com.example.CheckInApp.exception.ForbiddenActionException;
import com.example.CheckInApp.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream().map(userMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id, String authenticatedUserEmail) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + id));
        if (!authenticatedUserEmail.equalsIgnoreCase(user.getEmail())) {
            throw new ForbiddenActionException("You can only access your own profile.");
        }
        return userMapper.toResponse(user);
    }

    @Transactional
    public UserResponse updateUserProfile(Long id, UserProfileRequest userProfileRequest, String authenticatedUserEmail) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + id));

        if (!authenticatedUserEmail.equalsIgnoreCase(existingUser.getEmail())) {
            throw new ForbiddenActionException("You can only update your own profile.");
        }
        if (userProfileRequest.getEmail() != null
                && !userProfileRequest.getEmail().equalsIgnoreCase(existingUser.getEmail())
                && userRepository.existsByEmail(userProfileRequest.getEmail())) {
            throw new DuplicateEmailException("Email already exists");
        }
        userMapper.fromProfileToEntity(userProfileRequest, existingUser);
        try {
            User updatedUser = userRepository.save(existingUser);
            return userMapper.toResponse(updatedUser);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error updating user profile: " + e.getMessage());
        }

    }

    @Transactional
    public UserResponse updateUserStatusAndRoles(Long id, UserRequestByAdmin request) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + id));

        boolean userIsAdmin = existingUser.getRoles().contains(UserRole.ADMIN);
        boolean removingAdmin = !request.getRoles().contains(UserRole.ADMIN);
        boolean activeUser = request.getStatus();
        if (userIsAdmin && removingAdmin && userRepository.countByRolesContaining(UserRole.ADMIN) == 1) {
            throw new ForbiddenActionException("Last admin must remain admin!");
        }

        if(userIsAdmin && !activeUser && userRepository.countByRolesContaining(UserRole.ADMIN) == 1) {
            throw new ForbiddenActionException("Last admin can not be disabled!");
        }

        existingUser.setStatus(request.getStatus());
        existingUser.setRoles(request.getRoles());

        try {
            User updatedUser = userRepository.save(existingUser);
            return userMapper.toResponse(updatedUser);
        } catch (Exception e) {
            throw new DataBaseException("Error updating user status and roles: " + e.getMessage());
        }
    }

}