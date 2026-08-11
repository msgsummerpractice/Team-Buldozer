package com.example.CheckInApp.service;

import com.example.CheckInApp.model.User;
import com.example.CheckInApp.repository.UserRepository;
import com.example.CheckInApp.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + id));
}
    }


    
