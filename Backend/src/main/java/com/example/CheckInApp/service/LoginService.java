package com.example.CheckInApp.service;

import com.example.CheckInApp.dto.request.LoginRequest;
import com.example.CheckInApp.dto.response.LoginResponse;
import com.example.CheckInApp.model.User;
import com.example.CheckInApp.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class LoginService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private UserRepository userRepository;
    
    public LoginResponse authenticate(LoginRequest loginRequest) {
        log.info("Authenticating user: {}", loginRequest.getUsername());

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword())
            );
            
            User user = userRepository.findByEmail(loginRequest.getUsername())
                    .orElseThrow(() -> new BadCredentialsException("User not found"));
            
            List<String> roles = user.getRoles()
                    .stream()
                    .map(role -> role.name().toLowerCase())
                    .collect(Collectors.toList());
            
            String token = jwtUtil.generateToken(loginRequest.getUsername());
            long expiresIn = jwtUtil.getExpirationTime();

            log.info("Authentication successful for user: {}", loginRequest.getUsername());

            LoginResponse response = new LoginResponse();
            response.setToken(token);
            response.setType("Bearer");
            response.setExpiresIn(expiresIn);
            response.setRoles(roles);
            response.setMessage("Login successful");
            
            return response;

        } catch (BadCredentialsException e) {
            log.warn("Invalid credentials for user: {}", loginRequest.getUsername());
            throw new BadCredentialsException("Invalid username or password", e);
        } catch (Exception e) {
            log.error("Authentication error: {}", e.getMessage());
            throw new BadCredentialsException("Authentication failed", e);
        }
    }
}

