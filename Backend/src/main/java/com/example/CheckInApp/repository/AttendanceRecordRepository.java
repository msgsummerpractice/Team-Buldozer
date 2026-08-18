package com.example.CheckInApp.repository;

import com.example.CheckInApp.model.AttendanceRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRecordRepository extends JpaRepository<AttendanceRecord, Long> {

    List<AttendanceRecord> findByEventId(Long eventId);

    List<AttendanceRecord> findByUserId(Long userId);

    Optional<AttendanceRecord> findByEventIdAndUserId(Long eventId, Long userId);
}
