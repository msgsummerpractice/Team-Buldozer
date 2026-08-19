package com.example.CheckInApp.service;

import com.example.CheckInApp.dto.mapper.AttendanceMapper;
import com.example.CheckInApp.dto.response.AttendanceResponse;
import com.example.CheckInApp.exception.AlreadyCheckedInException;
import com.example.CheckInApp.exception.EventCheckInClosedException;
import com.example.CheckInApp.exception.ForbiddenActionException;
import com.example.CheckInApp.exception.ResourceNotFoundException;
import com.example.CheckInApp.model.AttendanceRecord;
import com.example.CheckInApp.model.Event;
import com.example.CheckInApp.model.User;
import com.example.CheckInApp.repository.AttendanceRecordRepository;
import com.example.CheckInApp.repository.EventRepository;
import com.example.CheckInApp.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceRecordRepository attendanceRecordRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final AttendanceMapper attendanceMapper;

    @Transactional
    public AttendanceResponse checkIn(String checkInCode, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email " + userEmail));

        // Same error for an unknown code and a user who never registered, so neither case is revealed.
        Event event = eventRepository.findByCheckInCode(checkInCode)
                .orElseThrow(() -> new ForbiddenActionException("You are not registered for this event."));

        AttendanceRecord attendanceRecord = attendanceRecordRepository
                .findByEvent_IdAndUser_Id(event.getId(), user.getId())
                .orElseThrow(() -> new ForbiddenActionException("You are not registered for this event."));

        if (event.getEndDateTime().isBefore(LocalDateTime.now())) {
            throw new EventCheckInClosedException("Check-in is closed because the event has already ended.");
        }

        if (attendanceRecord.isCheckedIn()) {
            throw new AlreadyCheckedInException("You are already checked in for this event.");
        }

        attendanceRecord.setCheckedIn(true);
        AttendanceRecord saved = attendanceRecordRepository.save(attendanceRecord);

        return attendanceMapper.toResponse(saved);
    }

}
