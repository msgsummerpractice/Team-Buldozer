package com.example.CheckInApp.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventCodesResponse {
    private String checkInCode;
    private String qrCode;
}
