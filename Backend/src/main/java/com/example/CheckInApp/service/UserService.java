package com.example.CheckInApp.service;

import com.example.CheckInApp.dto.request.UpdateUserRequest;
import com.example.CheckInApp.dto.request.UserRequest;
import com.example.CheckInApp.model.User;
import com.example.CheckInApp.repository.UserRepository;
import com.example.CheckInApp.mapper.UserMapper;
import com.example.CheckInApp.exception.DuplicateEmailException;
import com.example.CheckInApp.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    public User addUser(UserRequest request) {
        userRepository.findByEmail(request.getEmail()).ifPresent(u -> {
            throw new DuplicateEmailException("A user with email " + request.getEmail() + " already exists");
        });
        User userToSave = userMapper.toEntity(request);
        return userRepository.save(userToSave);
    }

    public User updateUser(Long id, UserRequest request) {
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + id));
        userMapper.updateEntityFromRequest(existing, request);
        return userRepository.save(existing);
    }

    public User patchUser(Long id, UpdateUserRequest patch) {
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + id));
        userMapper.applyPatch(existing, patch);
        return userRepository.save(existing);
    }

    public void deleteUser(Long id) {
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + id));
        userRepository.delete(existing);
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + id));
    }

    public Page<User> getAllUsers(@NonNull Pageable pageable) {
        return userRepository.findAll(pageable);
    }
}

    
