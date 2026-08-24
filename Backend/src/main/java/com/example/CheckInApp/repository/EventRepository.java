package com.example.CheckInApp.repository;

import com.example.CheckInApp.model.Event;
import com.example.CheckInApp.model.EventLocation;
import com.example.CheckInApp.model.EventStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    // Includes events past their registration deadline when the user is already registered, so they can still check in.
    @Query("select e from Event e where e.status = :status and e.location in :locations "
            + "and (e.registrationEndDate >= :today or e.id in :registeredEventIds) "
            + "order by e.startDateTime desc")
    List<Event> findEligibleOrRegisteredEvents(@Param("status") EventStatus status,
            @Param("locations") List<EventLocation> locations,
            @Param("today") LocalDate today,
            @Param("registeredEventIds") Collection<Long> registeredEventIds);

    List<Event> findAllByOrderByStartDateTimeDesc();

    boolean existsByCheckInCode(String checkInCode);

    Optional<Event> findByCheckInCode(String checkInCode);

}