package com.example.CheckInApp.repository;

import com.example.CheckInApp.model.AttendanceRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AttendanceRecordRepository extends JpaRepository<AttendanceRecord, Float> {

    Optional<AttendanceRecord> findByEvent_IdAndUser_Id(Long eventId, Long userId);

}
