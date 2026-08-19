package com.example.CheckInApp.controller;

import com.example.CheckInApp.dto.response.AttendanceResponse;
import com.example.CheckInApp.service.AttendanceService;

import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/attendance")
@RequiredArgsConstructor
@Validated
public class AttendanceController {

    private final AttendanceService attendanceService;

    @PatchMapping("/check-in/{checkInCode}")
    public ResponseEntity<AttendanceResponse> checkInCode(
            @PathVariable @Pattern(regexp = "\\d{6}", message = "Check-in code must be 6 digits.") String checkInCode,
            Authentication authentication) {
        AttendanceResponse response = attendanceService.checkIn(checkInCode, authentication.getName());
        return ResponseEntity.ok(response);
    }

}
