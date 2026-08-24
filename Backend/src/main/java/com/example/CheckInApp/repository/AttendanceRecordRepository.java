package com.example.CheckInApp.repository;

import com.example.CheckInApp.model.AttendanceRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface AttendanceRecordRepository extends JpaRepository<AttendanceRecord, Long> {

    Optional<AttendanceRecord> findByEventIdAndUserId(Long eventId, Long userId);

    @Query("select ar.event.id from AttendanceRecord ar where ar.user.id = :userId")
    Set<Long> findEventIdsByUserId(@Param("userId") Long userId);

    @Query("select ar.event.id from AttendanceRecord ar where ar.user.id = :userId and ar.checkedIn = true")
    Set<Long> findCheckedInEventIdsByUserId(@Param("userId") Long userId);

}
