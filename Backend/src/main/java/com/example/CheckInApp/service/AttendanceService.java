package com.example.CheckInApp.service;

import com.example.CheckInApp.dto.request.QrCodeCheckInRequest;
import com.example.CheckInApp.dto.request.CheckInRequest;
import com.example.CheckInApp.dto.response.CheckInResponse;
import com.example.CheckInApp.exception.AlreadyCheckedInException;
import com.example.CheckInApp.exception.CheckInClosedException;
import com.example.CheckInApp.exception.InvalidCheckInCodeException;
import com.example.CheckInApp.exception.InvalidQrCodeCheckInException;
import com.example.CheckInApp.exception.NotRegisteredForEventException;
import com.example.CheckInApp.exception.ResourceNotFoundException;
import com.example.CheckInApp.exception.WithdrawnRegistrationException;
import com.example.CheckInApp.model.AttendanceRecord;
import com.example.CheckInApp.model.Registration;
import com.example.CheckInApp.model.Event;
import com.example.CheckInApp.model.EventStatus;
import com.example.CheckInApp.model.RegistrationStatus;
import com.example.CheckInApp.model.User;
import com.example.CheckInApp.repository.AttendanceRecordRepository;
import com.example.CheckInApp.repository.EventRepository;
import com.example.CheckInApp.repository.RegistrationRepository;
import com.example.CheckInApp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceRecordRepository attendanceRecordRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final RegistrationRepository registrationRepository;

    @Transactional
    public CheckInResponse checkInByCode(CheckInRequest request, String userEmail) {
        Event event = eventRepository.findByCheckInCode(request.checkInCode())
                .orElseThrow(() -> new InvalidCheckInCodeException("Invalid check-in code."));

        return performCheckIn(event, userEmail);
    }

    @Transactional
    public CheckInResponse checkInByQrCode(QrCodeCheckInRequest request, String userEmail) {
        Event event = eventRepository.findById(request.eventId())
                .orElseThrow(() -> new InvalidQrCodeCheckInException("Invalid QR code."));

        if (!event.getName().equalsIgnoreCase(request.eventName())) {
            throw new InvalidQrCodeCheckInException("Invalid QR code.");
        }

        return performCheckIn(event, userEmail);
    }

    private CheckInResponse performCheckIn(Event event, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email " + userEmail));

        Registration registration = registrationRepository.findByEventIdAndUserId(event.getId(), user.getId())
                .orElseThrow(() -> new NotRegisteredForEventException("You are not registered for this event."));

        if (registration.getStatus() != RegistrationStatus.CONFIRMED) {
            throw new WithdrawnRegistrationException("You cannot check in with a withdrawn registration.");
        }

        AttendanceRecord attendanceRecord = attendanceRecordRepository
                .findByEventIdAndUserId(event.getId(), user.getId())
                .orElseThrow(
                        () -> new WithdrawnRegistrationException("You cannot check in with a withdrawn registration."));

        if (event.getStatus() != EventStatus.PUBLISHED) {
            throw new CheckInClosedException("Check-in is closed because the event is not published.");
        }

        Instant now = Instant.now();
        if (now.isBefore(event.getStartDateTime())) {
            throw new CheckInClosedException("Check-in is not yet open. The event has not started.");
        }
        if (event.getEndDateTime().isBefore(now)) {
            throw new CheckInClosedException("Check-in is closed because the event has already ended.");
        }

        if (attendanceRecord.isCheckedIn()) {
            throw new AlreadyCheckedInException("You are already checked in for this event.");
        }

        attendanceRecord.setCheckedIn(true);
        attendanceRecord.setCheckedInTime(now);
        attendanceRecordRepository.save(attendanceRecord);

        return new CheckInResponse(event.getId(), event.getName(), attendanceRecord.getCheckedInTime());
    }

    @Transactional(readOnly = true)
    public boolean getAttendanceStatus(Long eventId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email " + userEmail));

        AttendanceRecord record = attendanceRecordRepository.findByEventIdAndUserId(eventId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User is not registered for event " + eventId));

        return record.isCheckedIn();
    }

}
