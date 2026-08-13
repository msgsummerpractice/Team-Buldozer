package com.example.CheckInApp.service;

import com.example.CheckInApp.dto.request.UserProfileRequest;
import com.example.CheckInApp.dto.response.UserResponse;
import com.example.CheckInApp.dto.UserProfile.request.UserProfileRequest;
import com.example.CheckInApp.dto.mapper.UserMapper;
import com.example.CheckInApp.model.User;
import com.example.CheckInApp.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import com.example.CheckInApp.exception.DuplicateEmailException;
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
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + id));
        return userMapper.toResponse(user);
    }

    @Transactional
    public UserResponse updateUserProfile(Long id, UserProfileRequest userProfileRequest) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + id));
        
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

}