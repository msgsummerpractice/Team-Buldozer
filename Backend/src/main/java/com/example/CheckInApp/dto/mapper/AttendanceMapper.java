package com.example.CheckInApp.dto.mapper;

import com.example.CheckInApp.dto.response.AttendanceResponse;
import com.example.CheckInApp.model.AttendanceRecord;
import org.springframework.stereotype.Component;

@Component
public class AttendanceMapper {

    public AttendanceResponse toResponse(AttendanceRecord attendanceRecord) {
        if (attendanceRecord == null) {
            return null;
        }

        return AttendanceResponse.builder()
                .id(attendanceRecord.getId())
                .eventId(attendanceRecord.getEvent().getId())
                .userId(attendanceRecord.getUser().getId())
                .checkedIn(attendanceRecord.isCheckedIn())
                .build();
    }

}
