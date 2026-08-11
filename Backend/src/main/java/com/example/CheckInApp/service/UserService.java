package com.example.CheckInApp.service;

import com.example.CheckInApp.dto.response.UserResponse;
import com.example.CheckInApp.mapper.UserMapper;
import com.example.CheckInApp.model.User;
import com.example.CheckInApp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import com.example.CheckInApp.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public List<UserResponse> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream().map(userMapper::mapUserToUserResponse).toList();
    }

}
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + id));
}
    }
