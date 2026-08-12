package com.example.CheckInApp.service;

import com.example.CheckInApp.dto.response.UserResponse;
import com.example.CheckInApp.mapper.userProfileMapper.UserProfileMapper;
import com.example.CheckInApp.dto.UserProfile.request.UserProfileRequest;
import com.example.CheckInApp.dto.UserProfile.response.UserProfileResponse;
import com.example.CheckInApp.dto.request.UserRequest;
import com.example.CheckInApp.mapper.UserMapper;
import com.example.CheckInApp.model.User;
import com.example.CheckInApp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import com.example.CheckInApp.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final  UserProfileMapper userProfileMapper;

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
    public UserResponse updateUser(Long id, UserRequest userRequest) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + id));

        existingUser.setFirstName(userRequest.getFirstName());
        existingUser.setLastName(userRequest.getLastName());
        existingUser.setEmail(userRequest.getEmail());
        existingUser.setLocation(userRequest.getLocation());
        existingUser.setRoles(userRequest.getRoles());

        User updatedUser = userRepository.save(existingUser);
        return userMapper.toResponse(updatedUser);
    }
    
    @Transactional
    public UserResponse patchUser(Long id, UserRequest userRequest) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + id));

        if (userRequest.getFirstName() != null) {
            existingUser.setFirstName(userRequest.getFirstName());
        }
        if (userRequest.getLastName() != null) {
            existingUser.setLastName(userRequest.getLastName());
        }
        if (userRequest.getEmail() != null) {
            existingUser.setEmail(userRequest.getEmail());
            try {
                userRepository.save(existingUser);
            } catch (Exception e) {
                throw new IllegalArgumentException("Email already exists: " + userRequest.getEmail());
            }
        }
        if (userRequest.getLocation() != null) {
            existingUser.setLocation(userRequest.getLocation());
        }
        if (userRequest.getRoles() != null && !userRequest.getRoles().isEmpty()) {
            existingUser.setRoles(userRequest.getRoles());
        }

        User updatedUser = userRepository.save(existingUser);
        return userMapper.toResponse(updatedUser);
    }

    @Transactional
    public UserProfileResponse updateUserProfile(Long id, UserProfileRequest userProfileRequest) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + id));
        
        userProfileMapper.toEntity(userProfileRequest, existingUser);
        try {
            User updatedUser = userRepository.save(existingUser);
            return userProfileMapper.toResponse(updatedUser);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error updating user profile: (email invalid)" + e.getMessage());
        }
        
    }

    @Transactional
    public UserProfileResponse patchUserProfile(Long id, UserProfileRequest userProfileRequest) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + id));
        
        userProfileMapper.toEntity(userProfileRequest, existingUser);
        try {
            User updatedUser = userRepository.save(existingUser);
            return userProfileMapper.toResponse(updatedUser);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error patching user profile: (email invalid)" + e.getMessage());
        }
    }

}