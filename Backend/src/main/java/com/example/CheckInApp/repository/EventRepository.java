package com.example.CheckInApp.repository;

import com.example.CheckInApp.model.Event;
import com.example.CheckInApp.model.EventLocation;
import com.example.CheckInApp.model.EventStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByStatus(EventStatus status);

    List<Event> findByStatusAndLocationInOrderByStartDateTimeDesc(EventStatus status, List<EventLocation> locations);

    List<Event> findAllByOrderByStartDateTimeDesc();

}