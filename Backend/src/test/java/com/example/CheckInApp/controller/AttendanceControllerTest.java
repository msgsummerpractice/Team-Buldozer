package com.example.CheckInApp.controller;

import com.example.CheckInApp.config.SecurityConfig;
import com.example.CheckInApp.dto.request.CheckInRequest;
import com.example.CheckInApp.dto.request.QrCodeCheckInRequest;
import com.example.CheckInApp.dto.response.CheckInResponse;
import com.example.CheckInApp.exception.AlreadyCheckedInException;
import com.example.CheckInApp.exception.CheckInClosedException;
import com.example.CheckInApp.exception.InvalidCheckInCodeException;
import com.example.CheckInApp.exception.InvalidQrCodeCheckInException;
import com.example.CheckInApp.exception.NotRegisteredForEventException;
import com.example.CheckInApp.exception.ResourceNotFoundException;
import com.example.CheckInApp.repository.UserRepository;
import com.example.CheckInApp.security.JwtUtil;
import com.example.CheckInApp.service.AttendanceService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AttendanceController.class)
@Import(SecurityConfig.class)
class AttendanceControllerTest {

    @Autowired
    private WebApplicationContext context;

    @MockitoBean
    private AttendanceService attendanceService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @MockitoBean
    private UserRepository userRepository;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private static final String USER_EMAIL = "user@example.com";

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    // ---- checkInByCode ----

    @Test
    void checkInByCode_returnsOkWithCheckInResponse_whenValid() throws Exception {
        CheckInRequest request = new CheckInRequest("123456");
        CheckInResponse response = new CheckInResponse(1L, "Team Building Event", Instant.parse("2026-09-15T10:00:00Z"));

        when(attendanceService.checkInByCode(any(CheckInRequest.class), any(String.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/v1/attendance/check-ins")
                        .with(user(USER_EMAIL))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.eventId", is(1)))
                .andExpect(jsonPath("$.eventName", is("Team Building Event")));

        verify(attendanceService).checkInByCode(any(CheckInRequest.class), any(String.class));
    }

    @Test
    void checkInByCode_returnsBadRequest_whenCheckInCodeInvalid() throws Exception {
        CheckInRequest request = new CheckInRequest("123456");

        when(attendanceService.checkInByCode(any(CheckInRequest.class), any(String.class)))
                .thenThrow(new InvalidCheckInCodeException("Invalid check-in code."));

        mockMvc.perform(post("/api/v1/attendance/check-ins")
                        .with(user(USER_EMAIL))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void checkInByCode_returnsNotFound_whenUserNotFound() throws Exception {
        CheckInRequest request = new CheckInRequest("123456");

        when(attendanceService.checkInByCode(any(CheckInRequest.class), any(String.class)))
                .thenThrow(new ResourceNotFoundException("User not found with email " + USER_EMAIL));

        mockMvc.perform(post("/api/v1/attendance/check-ins")
                        .with(user(USER_EMAIL))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void checkInByCode_returnsForbidden_whenUserNotRegisteredForEvent() throws Exception {
        CheckInRequest request = new CheckInRequest("123456");

        when(attendanceService.checkInByCode(any(CheckInRequest.class), any(String.class)))
                .thenThrow(new NotRegisteredForEventException("You are not registered for this event."));

        mockMvc.perform(post("/api/v1/attendance/check-ins")
                        .with(user(USER_EMAIL))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void checkInByCode_returnsConflict_whenCheckInClosed() throws Exception {
        CheckInRequest request = new CheckInRequest("123456");

        when(attendanceService.checkInByCode(any(CheckInRequest.class), any(String.class)))
                .thenThrow(new CheckInClosedException("Check-in is closed because the event is not published."));

        mockMvc.perform(post("/api/v1/attendance/check-ins")
                        .with(user(USER_EMAIL))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    void checkInByCode_returnsConflict_whenAlreadyCheckedIn() throws Exception {
        CheckInRequest request = new CheckInRequest("123456");

        when(attendanceService.checkInByCode(any(CheckInRequest.class), any(String.class)))
                .thenThrow(new AlreadyCheckedInException("You are already checked in for this event."));

        mockMvc.perform(post("/api/v1/attendance/check-ins")
                        .with(user(USER_EMAIL))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    // ---- checkInByQrCode ----

    @Test
    void checkInByQrCode_returnsOkWithCheckInResponse_whenValid() throws Exception {
        QrCodeCheckInRequest request = new QrCodeCheckInRequest(1L, "Team Building Event");
        CheckInResponse response = new CheckInResponse(1L, "Team Building Event", Instant.parse("2026-09-15T10:00:00Z"));

        when(attendanceService.checkInByQrCode(any(QrCodeCheckInRequest.class), any(String.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/v1/attendance/check-ins/qr-code")
                        .with(user(USER_EMAIL))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.eventId", is(1)))
                .andExpect(jsonPath("$.eventName", is("Team Building Event")));

        verify(attendanceService).checkInByQrCode(any(QrCodeCheckInRequest.class), any(String.class));
    }

    @Test
    void checkInByQrCode_returnsBadRequest_whenQrCodeInvalid() throws Exception {
        QrCodeCheckInRequest request = new QrCodeCheckInRequest(1L, "Team Building Event");

        when(attendanceService.checkInByQrCode(any(QrCodeCheckInRequest.class), any(String.class)))
                .thenThrow(new InvalidQrCodeCheckInException("Invalid QR code."));

        mockMvc.perform(post("/api/v1/attendance/check-ins/qr-code")
                        .with(user(USER_EMAIL))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void checkInByQrCode_returnsNotFound_whenUserNotFound() throws Exception {
        QrCodeCheckInRequest request = new QrCodeCheckInRequest(1L, "Team Building Event");

        when(attendanceService.checkInByQrCode(any(QrCodeCheckInRequest.class), any(String.class)))
                .thenThrow(new ResourceNotFoundException("User not found with email " + USER_EMAIL));

        mockMvc.perform(post("/api/v1/attendance/check-ins/qr-code")
                        .with(user(USER_EMAIL))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void checkInByQrCode_returnsForbidden_whenUserNotRegisteredForEvent() throws Exception {
        QrCodeCheckInRequest request = new QrCodeCheckInRequest(1L, "Team Building Event");

        when(attendanceService.checkInByQrCode(any(QrCodeCheckInRequest.class), any(String.class)))
                .thenThrow(new NotRegisteredForEventException("You are not registered for this event."));

        mockMvc.perform(post("/api/v1/attendance/check-ins/qr-code")
                        .with(user(USER_EMAIL))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void checkInByQrCode_returnsConflict_whenCheckInClosed() throws Exception {
        QrCodeCheckInRequest request = new QrCodeCheckInRequest(1L, "Team Building Event");

        when(attendanceService.checkInByQrCode(any(QrCodeCheckInRequest.class), any(String.class)))
                .thenThrow(new CheckInClosedException("Check-in is closed because the event has already ended."));

        mockMvc.perform(post("/api/v1/attendance/check-ins/qr-code")
                        .with(user(USER_EMAIL))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    void checkInByQrCode_returnsConflict_whenAlreadyCheckedIn() throws Exception {
        QrCodeCheckInRequest request = new QrCodeCheckInRequest(1L, "Team Building Event");

        when(attendanceService.checkInByQrCode(any(QrCodeCheckInRequest.class), any(String.class)))
                .thenThrow(new AlreadyCheckedInException("You are already checked in for this event."));

        mockMvc.perform(post("/api/v1/attendance/check-ins/qr-code")
                        .with(user(USER_EMAIL))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

}
