package com.example.CheckInApp.service;

import com.example.CheckInApp.dto.request.LoginRequest;
import com.example.CheckInApp.dto.response.LoginResponse;
import com.example.CheckInApp.model.User;
import com.example.CheckInApp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginService {
    private final CustomUserDetailsService customUserDetailsService;

    private final AuthenticationManager authenticationManager;

    private final  JwtUtil jwtUtil;

    private final UserRepository userRepository;


    public LoginResponse authenticate(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> {
                    return new BadCredentialsException("User not found");
                });
                try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword())
                             );
                    } catch(BadCredentialsException e) {
                            throw new BadCredentialsException("User not found");
                    }
        
        String token = jwtUtil.generateToken(customUserDetailsService.loadUserByUsername(user.getEmail()));
        long expiresIn = jwtUtil.getExpirationTime();


        return new LoginResponse(token, "Bearer", expiresIn, loginRequest.getEmail());
    }
}

