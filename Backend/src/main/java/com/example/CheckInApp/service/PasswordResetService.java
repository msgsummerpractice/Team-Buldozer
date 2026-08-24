package com.example.CheckInApp.service;

import com.example.CheckInApp.exception.ResourceNotFoundException;
import com.example.CheckInApp.model.PasswordResetToken;
import com.example.CheckInApp.model.User;
import com.example.CheckInApp.repository.PasswordResetTokenRepository;
import com.example.CheckInApp.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HexFormat;
import com.example.CheckInApp.exception.TooManyRequestsException;

@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private static final SecureRandom secureRandom = new SecureRandom();
    private static final int RATE_LIMIT_SECONDS = 60;

    @Transactional
    public void requestPasswordReset(String email) {
        userRepository.findByEmail(email).ifPresent(user -> {
            tokenRepository.findFirstByUserOrderByCreatedAtDesc(user).ifPresent(lastToken -> {
                if (lastToken.getCreatedAt().plusSeconds(RATE_LIMIT_SECONDS).isAfter(LocalDateTime.now())) {
                    throw new TooManyRequestsException(
                            "Please wait before requesting another password reset.");
                }
            });
            tokenRepository.deleteByUser(user);
            byte[] bytes = new byte[32];
            secureRandom.nextBytes(bytes);
            String token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
            PasswordResetToken resetToken = PasswordResetToken.create(user, hashToken(token));
            tokenRepository.save(resetToken);
            emailService.sendPasswordResetEmail(user.getEmail(), token);
        });
    }

    @Transactional
    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = tokenRepository.findByTokenHash(hashToken(token))
                .orElseThrow(() -> new ResourceNotFoundException("Invalid or expired reset link."));

        if (resetToken.isUsed() || resetToken.isExpired()) {
            throw new ResourceNotFoundException("Invalid or expired reset link.");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setPasswordUpdatedAt(Instant.now());
        userRepository.save(user);

        resetToken.setUsed(true);
        tokenRepository.save(resetToken);
    }

    private static String hashToken(String token) {
        try {
            byte[] hash = MessageDigest.getInstance("SHA-256")
                    .digest(token.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }
}
