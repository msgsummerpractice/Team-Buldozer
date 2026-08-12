package com.example.CheckInApp.service;

import com.example.CheckInApp.dto.request.LoginRequest;
import com.example.CheckInApp.dto.response.LoginResponse;
import com.example.CheckInApp.model.User;
import com.example.CheckInApp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class LoginService {

    private final AuthenticationManager authenticationManager;

    private final  JwtUtil jwtUtil;

    private final UserRepository userRepository;
    public LoginResponse authenticate(LoginRequest loginRequest) {
        log.info("Authenticating user: {}", loginRequest.getEmail());

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword())
            );
        } catch (BadCredentialsException e) {
            log.error("Authentication failed for user: {} - Invalid credentials", loginRequest.getEmail());
            throw e;
        } catch (Exception e) {
            log.error("Authentication failed for user: {} - Error: {}", loginRequest.getEmail(), e.getMessage());
            throw e;
        }
        
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> {
                    log.error("User not found: {}", loginRequest.getEmail());
                    return new BadCredentialsException("User not found");
                });
        
        List<String> roles = user.getRoles()
                .stream()
                .map(role -> role.name().toLowerCase())
                .collect(Collectors.toList());
        
        String token = jwtUtil.generateToken(loginRequest.getEmail());
        long expiresIn = jwtUtil.getExpirationTime();

        log.info("Authentication successful for user: {} with roles: {}", loginRequest.getEmail(), roles);

        return new LoginResponse(token, "Bearer", expiresIn, loginRequest.getEmail(), roles, "Login successful");
    }
}

