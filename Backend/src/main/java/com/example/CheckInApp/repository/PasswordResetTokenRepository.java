package com.example.CheckInApp.repository;

import com.example.CheckInApp.model.PasswordResetToken;
import com.example.CheckInApp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByTokenHash(String tokenHash);
    Optional<PasswordResetToken> findFirstByUserOrderByCreatedAtDesc(User user);
    void deleteByUser(User user);
}
