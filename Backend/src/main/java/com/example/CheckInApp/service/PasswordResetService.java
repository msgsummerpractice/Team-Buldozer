package com.example.CheckInApp.service;

import com.example.CheckInApp.model.PasswordResetToken;
import com.example.CheckInApp.repository.PasswordResetTokenRepository;
import com.example.CheckInApp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final EmailService emailService;

    public void requestPasswordReset(String email) {
        userRepository.findByEmail(email).ifPresent(user -> {
            String token = UUID.randomUUID().toString();
            PasswordResetToken resetToken = PasswordResetToken.create(user, token);
            tokenRepository.save(resetToken);
            emailService.sendPasswordResetEmail(user.getEmail(), token);
        });
    }
}
