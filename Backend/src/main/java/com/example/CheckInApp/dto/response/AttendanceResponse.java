package com.example.CheckInApp.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AttendanceResponse {

    private Float id;
    private Long eventId;
    private Long userId;
    private boolean checkedIn;

}
