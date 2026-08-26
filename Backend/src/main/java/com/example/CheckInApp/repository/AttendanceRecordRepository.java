package com.example.CheckInApp.repository;

import com.example.CheckInApp.model.AttendanceRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface AttendanceRecordRepository extends JpaRepository<AttendanceRecord, Long> {

    Optional<AttendanceRecord> findByEventIdAndUserId(Long eventId, Long userId);

    @Query("SELECT ar.user.id FROM AttendanceRecord ar WHERE ar.event.id = :eventId AND ar.checkedIn = true")
    Set<Long> findCheckedInUserIdsByEventId(@Param("eventId") Long eventId);

    @Query("""
            SELECT new com.example.CheckInApp.repository.EventAttendanceView(ar.event.id, ar.checkedIn)
            FROM AttendanceRecord ar WHERE ar.user.id = :userId
            """)
    List<EventAttendanceView> findAttendanceByUserId(@Param("userId") Long userId);

}
