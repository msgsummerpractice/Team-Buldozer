package com.example.CheckInApp.service;

import com.example.CheckInApp.dto.request.UserRequest;
import com.example.CheckInApp.model.User;
import com.example.CheckInApp.repository.UserRepository;
import com.example.CheckInApp.mapper.UserMapper;
import com.example.CheckInApp.exception.DuplicateEmailException;
import com.example.CheckInApp.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
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

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + id));
    }

}

    
