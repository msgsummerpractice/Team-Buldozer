package com.example.CheckInApp.repository;

import com.example.CheckInApp.model.Registration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface RegistrationRepository extends JpaRepository<Registration, Long> {

    boolean existsByEventIdAndUserId(Long eventId, Long userId);

    Optional<Registration> findByEventIdAndUserId(Long eventId, Long userId);

    @Query("""
            SELECT r
            FROM Registration r JOIN FETCH r.user WHERE r.event.id = :eventId ORDER BY r.id
            """)
    List<Registration> findAllByEventIdWithUser(@Param("eventId") Long eventId);

}
