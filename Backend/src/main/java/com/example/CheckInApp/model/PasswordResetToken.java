package com.example.CheckInApp.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "password_reset_tokens")
@Builder
public class PasswordResetToken {

    private static final int EXPIRATION_MINUTES = 15;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String tokenHash;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant expiresAt;

    private boolean used;

    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }

    public static PasswordResetToken create(User user, String tokenHash) {
        Instant now = Instant.now();
        return PasswordResetToken.builder()
                .tokenHash(tokenHash)
                .user(user)
                .createdAt(now)
                .expiresAt(now.plus(EXPIRATION_MINUTES, ChronoUnit.MINUTES))
                .used(false)
                .build();
    }
}
