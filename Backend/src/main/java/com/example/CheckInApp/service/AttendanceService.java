package com.example.CheckInApp.service;

import com.example.CheckInApp.dto.request.CheckInRequest;
import com.example.CheckInApp.dto.response.CheckInResponse;
import com.example.CheckInApp.exception.CheckInClosedException;
import com.example.CheckInApp.exception.InvalidCheckInCodeException;
import com.example.CheckInApp.exception.NotRegisteredForEventException;
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

    @Transactional
    public CheckInResponse checkIn(CheckInRequest request, String userEmail) {
        Event event = eventRepository.findByCheckInCode(request.getCheckInCode())
                .orElseThrow(() -> new InvalidCheckInCodeException("Invalid check-in code."));

        if (event.getEndDateTime().isBefore(LocalDateTime.now())) {
            throw new CheckInClosedException("Check-in is closed because the event has already ended.");
        }

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email " + userEmail));

        AttendanceRecord attendanceRecord = attendanceRecordRepository
                .findByEvent_IdAndUser_Id(event.getId(), user.getId())
                .orElseThrow(() -> new NotRegisteredForEventException("You are not registered for this event."));

        attendanceRecord.setCheckedIn(true);
        AttendanceRecord saved = attendanceRecordRepository.save(attendanceRecord);

        return new CheckInResponse(event.getId(), saved.isCheckedIn());
    }

}
