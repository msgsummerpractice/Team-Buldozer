package com.example.CheckInApp.service;

import java.util.Set;

import org.springframework.stereotype.Service;
import com.example.CheckInApp.exception.DuplicateEmailException;
import com.example.CheckInApp.repository.UserRepository;
import com.example.CheckInApp.dto.request.UserRequest;
import com.example.CheckInApp.dto.response.UserResponse;
import com.example.CheckInApp.model.Role;
import com.example.CheckInApp.model.User;

import lombok.RequiredArgsConstructor;
import com.example.CheckInApp.mapper.UserMapper;

@Service
@RequiredArgsConstructor
public class RegisterService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserResponse registerUser(UserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateEmailException("Email already exists");
        }

        User user = userMapper.toEntity(request);
        user.setRoles(Set.of(Role.PARTICIPANT));
        user.setStatus(true);

        User savedUser = userRepository.save(user);
        return userMapper.toResponse(savedUser);
    }
}
