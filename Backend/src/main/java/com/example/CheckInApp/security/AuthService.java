package com.example.CheckInApp.security;

import com.example.CheckInApp.dto.request.LoginRequest;
import com.example.CheckInApp.dto.request.UserRequest;
import com.example.CheckInApp.dto.response.LoginResponse;
import com.example.CheckInApp.dto.response.UserResponse;
import com.example.CheckInApp.exception.DuplicateEmailException;
import com.example.CheckInApp.dto.mapper.UserMapper;
import com.example.CheckInApp.model.Role;
import com.example.CheckInApp.model.User;
import com.example.CheckInApp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService customUserDetailsService;

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

    public LoginResponse authenticate(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new BadCredentialsException("User not found"));

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword())
            );
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("User not found");
        }

        String token = jwtUtil.generateToken(customUserDetailsService.loadUserByUsername(user.getEmail()));
        long expiresIn = jwtUtil.getExpiration();

        return new LoginResponse(token, "Bearer", expiresIn, loginRequest.getEmail(), user.getId());
    }

}
