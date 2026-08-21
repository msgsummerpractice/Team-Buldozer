package com.example.CheckInApp.service;

import com.example.CheckInApp.dto.request.CheckInRequest;
import com.example.CheckInApp.dto.request.QrCodeCheckInRequest;
import com.example.CheckInApp.dto.response.CheckInResponse;
import com.example.CheckInApp.exception.AlreadyCheckedInException;
import com.example.CheckInApp.exception.CheckInClosedException;
import com.example.CheckInApp.exception.InvalidCheckInCodeException;
import com.example.CheckInApp.exception.InvalidQrCodeCheckInException;
import com.example.CheckInApp.exception.NotRegisteredForEventException;
import com.example.CheckInApp.exception.ResourceNotFoundException;
import com.example.CheckInApp.model.AttendanceRecord;
import com.example.CheckInApp.model.Event;
import com.example.CheckInApp.model.EventStatus;
import com.example.CheckInApp.model.User;
import com.example.CheckInApp.repository.AttendanceRecordRepository;
import com.example.CheckInApp.repository.EventRepository;
import com.example.CheckInApp.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AttendanceServiceTest {

    @Mock
    private AttendanceRecordRepository attendanceRecordRepository;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AttendanceService attendanceService;

    private static final String USER_EMAIL = "user@example.com";
    private static final String CHECK_IN_CODE = "123456";

    private Event publishedEvent() {
        return Event.builder()
                .id(1L)
                .name("Team Building Event")
                .status(EventStatus.PUBLISHED)
                .checkInCode(CHECK_IN_CODE)
                .startDateTime(LocalDateTime.now().minusHours(1))
                .endDateTime(LocalDateTime.now().plusHours(1))
                .build();
    }

    private User user() {
        return User.builder().id(1L).email(USER_EMAIL).build();
    }

    private AttendanceRecord attendanceRecord(Event event, User user, boolean checkedIn) {
        return AttendanceRecord.builder()
                .id(1L)
                .event(event)
                .user(user)
                .checkedIn(checkedIn)
                .build();
    }

    // ---- checkInByCode ----

    @Test
    void checkInByCode_returnsCheckInResponse_whenValid() {
        Event event = publishedEvent();
        User user = user();
        AttendanceRecord record = attendanceRecord(event, user, false);
        CheckInRequest request = new CheckInRequest(CHECK_IN_CODE);

        when(eventRepository.findByCheckInCode(CHECK_IN_CODE)).thenReturn(Optional.of(event));
        when(userRepository.findByEmail(USER_EMAIL)).thenReturn(Optional.of(user));
        when(attendanceRecordRepository.findByEventIdAndUserId(1L, 1L)).thenReturn(Optional.of(record));

        CheckInResponse response = attendanceService.checkInByCode(request, USER_EMAIL);

        assertThat(response.eventId()).isEqualTo(1L);
        assertThat(response.eventName()).isEqualTo("Team Building Event");
        assertThat(record.isCheckedIn()).isTrue();
        verify(attendanceRecordRepository).save(record);
    }

    @Test
    void checkInByCode_throwsInvalidCheckInCodeException_whenCodeNotFound() {
        CheckInRequest request = new CheckInRequest(CHECK_IN_CODE);

        when(eventRepository.findByCheckInCode(CHECK_IN_CODE)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> attendanceService.checkInByCode(request, USER_EMAIL))
                .isInstanceOf(InvalidCheckInCodeException.class)
                .hasMessage("Invalid check-in code.");

        verify(attendanceRecordRepository, never()).save(any(AttendanceRecord.class));
    }

    @Test
    void checkInByCode_throwsResourceNotFoundException_whenUserNotFound() {
        Event event = publishedEvent();
        CheckInRequest request = new CheckInRequest(CHECK_IN_CODE);

        when(eventRepository.findByCheckInCode(CHECK_IN_CODE)).thenReturn(Optional.of(event));
        when(userRepository.findByEmail(USER_EMAIL)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> attendanceService.checkInByCode(request, USER_EMAIL))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User not found with email " + USER_EMAIL);

        verify(attendanceRecordRepository, never()).save(any(AttendanceRecord.class));
    }

    @Test
    void checkInByCode_throwsNotRegisteredForEventException_whenAttendanceRecordMissing() {
        Event event = publishedEvent();
        User user = user();
        CheckInRequest request = new CheckInRequest(CHECK_IN_CODE);

        when(eventRepository.findByCheckInCode(CHECK_IN_CODE)).thenReturn(Optional.of(event));
        when(userRepository.findByEmail(USER_EMAIL)).thenReturn(Optional.of(user));
        when(attendanceRecordRepository.findByEventIdAndUserId(1L, 1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> attendanceService.checkInByCode(request, USER_EMAIL))
                .isInstanceOf(NotRegisteredForEventException.class)
                .hasMessage("You are not registered for this event.");

        verify(attendanceRecordRepository, never()).save(any(AttendanceRecord.class));
    }

    @Test
    void checkInByCode_throwsCheckInClosedException_whenEventNotPublished() {
        Event event = publishedEvent();
        event.setStatus(EventStatus.DRAFT);
        User user = user();
        AttendanceRecord record = attendanceRecord(event, user, false);
        CheckInRequest request = new CheckInRequest(CHECK_IN_CODE);

        when(eventRepository.findByCheckInCode(CHECK_IN_CODE)).thenReturn(Optional.of(event));
        when(userRepository.findByEmail(USER_EMAIL)).thenReturn(Optional.of(user));
        when(attendanceRecordRepository.findByEventIdAndUserId(1L, 1L)).thenReturn(Optional.of(record));

        assertThatThrownBy(() -> attendanceService.checkInByCode(request, USER_EMAIL))
                .isInstanceOf(CheckInClosedException.class)
                .hasMessage("Check-in is closed because the event is not published.");

        verify(attendanceRecordRepository, never()).save(any(AttendanceRecord.class));
    }

    @Test
    void checkInByCode_throwsAlreadyCheckedInException_whenAlreadyCheckedIn() {
        Event event = publishedEvent();
        User user = user();
        AttendanceRecord record = attendanceRecord(event, user, true);
        CheckInRequest request = new CheckInRequest(CHECK_IN_CODE);

        when(eventRepository.findByCheckInCode(CHECK_IN_CODE)).thenReturn(Optional.of(event));
        when(userRepository.findByEmail(USER_EMAIL)).thenReturn(Optional.of(user));
        when(attendanceRecordRepository.findByEventIdAndUserId(1L, 1L)).thenReturn(Optional.of(record));

        assertThatThrownBy(() -> attendanceService.checkInByCode(request, USER_EMAIL))
                .isInstanceOf(AlreadyCheckedInException.class)
                .hasMessage("You are already checked in for this event.");

        verify(attendanceRecordRepository, never()).save(any(AttendanceRecord.class));
    }

    // ---- checkInByQrCode ----

    @Test
    void checkInByQrCode_returnsCheckInResponse_whenValid() {
        Event event = publishedEvent();
        User user = user();
        AttendanceRecord record = attendanceRecord(event, user, false);
        QrCodeCheckInRequest request = new QrCodeCheckInRequest(1L, event.getName());

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(userRepository.findByEmail(USER_EMAIL)).thenReturn(Optional.of(user));
        when(attendanceRecordRepository.findByEventIdAndUserId(1L, 1L)).thenReturn(Optional.of(record));

        CheckInResponse response = attendanceService.checkInByQrCode(request, USER_EMAIL);

        assertThat(response.eventId()).isEqualTo(1L);
        assertThat(response.eventName()).isEqualTo("Team Building Event");
        assertThat(record.isCheckedIn()).isTrue();
        verify(attendanceRecordRepository).save(record);
    }

    @Test
    void checkInByQrCode_throwsInvalidQrCodeCheckInException_whenEventNotFound() {
        QrCodeCheckInRequest request = new QrCodeCheckInRequest(1L, "Team Building Event");

        when(eventRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> attendanceService.checkInByQrCode(request, USER_EMAIL))
                .isInstanceOf(InvalidQrCodeCheckInException.class)
                .hasMessage("Invalid QR code.");

        verify(attendanceRecordRepository, never()).save(any(AttendanceRecord.class));
    }

    @Test
    void checkInByQrCode_throwsInvalidQrCodeCheckInException_whenEventNameMismatch() {
        Event event = publishedEvent();
        QrCodeCheckInRequest request = new QrCodeCheckInRequest(1L, "Wrong Event Name");

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

        assertThatThrownBy(() -> attendanceService.checkInByQrCode(request, USER_EMAIL))
                .isInstanceOf(InvalidQrCodeCheckInException.class)
                .hasMessage("Invalid QR code.");

        verify(attendanceRecordRepository, never()).save(any(AttendanceRecord.class));
    }

    @Test
    void checkInByQrCode_throwsResourceNotFoundException_whenUserNotFound() {
        Event event = publishedEvent();
        QrCodeCheckInRequest request = new QrCodeCheckInRequest(1L, event.getName());

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(userRepository.findByEmail(USER_EMAIL)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> attendanceService.checkInByQrCode(request, USER_EMAIL))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User not found with email " + USER_EMAIL);

        verify(attendanceRecordRepository, never()).save(any(AttendanceRecord.class));
    }

    @Test
    void checkInByQrCode_throwsNotRegisteredForEventException_whenAttendanceRecordMissing() {
        Event event = publishedEvent();
        User user = user();
        QrCodeCheckInRequest request = new QrCodeCheckInRequest(1L, event.getName());

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(userRepository.findByEmail(USER_EMAIL)).thenReturn(Optional.of(user));
        when(attendanceRecordRepository.findByEventIdAndUserId(1L, 1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> attendanceService.checkInByQrCode(request, USER_EMAIL))
                .isInstanceOf(NotRegisteredForEventException.class)
                .hasMessage("You are not registered for this event.");

        verify(attendanceRecordRepository, never()).save(any(AttendanceRecord.class));
    }

    @Test
    void checkInByQrCode_throwsCheckInClosedException_whenEventAlreadyEnded() {
        Event event = publishedEvent();
        event.setEndDateTime(LocalDateTime.now().minusHours(1));
        User user = user();
        AttendanceRecord record = attendanceRecord(event, user, false);
        QrCodeCheckInRequest request = new QrCodeCheckInRequest(1L, event.getName());

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(userRepository.findByEmail(USER_EMAIL)).thenReturn(Optional.of(user));
        when(attendanceRecordRepository.findByEventIdAndUserId(1L, 1L)).thenReturn(Optional.of(record));

        assertThatThrownBy(() -> attendanceService.checkInByQrCode(request, USER_EMAIL))
                .isInstanceOf(CheckInClosedException.class)
                .hasMessage("Check-in is closed because the event has already ended.");

        verify(attendanceRecordRepository, never()).save(any(AttendanceRecord.class));
    }

    @Test
    void checkInByQrCode_throwsAlreadyCheckedInException_whenAlreadyCheckedIn() {
        Event event = publishedEvent();
        User user = user();
        AttendanceRecord record = attendanceRecord(event, user, true);
        QrCodeCheckInRequest request = new QrCodeCheckInRequest(1L, event.getName());

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(userRepository.findByEmail(USER_EMAIL)).thenReturn(Optional.of(user));
        when(attendanceRecordRepository.findByEventIdAndUserId(1L, 1L)).thenReturn(Optional.of(record));

        assertThatThrownBy(() -> attendanceService.checkInByQrCode(request, USER_EMAIL))
                .isInstanceOf(AlreadyCheckedInException.class)
                .hasMessage("You are already checked in for this event.");

        verify(attendanceRecordRepository, never()).save(any(AttendanceRecord.class));
    }

}
