package com.example.CheckInApp.repository;

import com.example.CheckInApp.model.Event;
import com.example.CheckInApp.model.EventLocation;
import com.example.CheckInApp.model.EventStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    // Includes events past their registration deadline when the user is already registered, so they can still check in.
    @Query("""
            SELECT e FROM Event e WHERE e.status = :status AND e.location IN :locations
            AND (e.registrationEndDate >= :today OR e.id IN :registeredEventIds)
            ORDER BY e.startDateTime DESC
            """)
    List<Event> findEligibleOrRegisteredEvents(@Param("status") EventStatus status,
            @Param("locations") List<EventLocation> locations,
            @Param("today") Instant today,
            @Param("registeredEventIds") Collection<Long> registeredEventIds);

    List<Event> findAllByOrderByStartDateTimeDesc();

    boolean existsByCheckInCode(String checkInCode);

    Optional<Event> findByCheckInCode(String checkInCode);

}