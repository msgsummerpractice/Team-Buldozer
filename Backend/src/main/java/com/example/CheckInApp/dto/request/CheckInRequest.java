package com.example.CheckInApp.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CheckInRequest {

    @NotBlank(message = "Check-in code cannot be blank.")
    @Pattern(regexp = "^[0-9]{6}$", message = "Check-in code must be 6 digits.")
    private String checkInCode;

}
