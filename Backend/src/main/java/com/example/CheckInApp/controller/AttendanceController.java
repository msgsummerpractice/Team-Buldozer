package com.example.CheckInApp.controller;

import com.example.CheckInApp.dto.request.QrCodeCheckInRequest;
import com.example.CheckInApp.dto.request.CheckInRequest;
import com.example.CheckInApp.dto.response.CheckInResponse;
import com.example.CheckInApp.service.AttendanceService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/attendance/check-ins")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    @PostMapping
    public ResponseEntity<CheckInResponse> checkInByCode(@Valid @RequestBody CheckInRequest request, Authentication authentication) {
        CheckInResponse response = attendanceService.checkInByCode(request, authentication.getName());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/qr-code")
    public ResponseEntity<CheckInResponse> checkInByQrCode(@Valid @RequestBody QrCodeCheckInRequest request, Authentication authentication) {
        CheckInResponse response = attendanceService.checkInByQrCode(request, authentication.getName());
        return ResponseEntity.ok(response);
    }

}
