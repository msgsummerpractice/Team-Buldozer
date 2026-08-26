package com.example.CheckInApp.repository;

import com.example.CheckInApp.model.Registration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RegistrationRepository extends JpaRepository<Registration, Long> {

    boolean existsByEventIdAndUserId(Long eventId, Long userId);

    Optional<Registration> findByEventIdAndUserId(Long eventId, Long userId);

}
