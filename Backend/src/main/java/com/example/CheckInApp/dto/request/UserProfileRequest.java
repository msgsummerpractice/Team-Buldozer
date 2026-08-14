package com.example.CheckInApp.dto.request;

import com.example.CheckInApp.model.UserLocation;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileRequest {

    @NotBlank(message = "First name is required")
    @Size(min = 3, max = 32, message = "First name must be at most 32 characters and at least 3 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 3, max = 32, message = "Last name must be at most 32 characters and at least 3 characters")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email is not valid")
    @Size(max = 64, message = "Email must be at most 64 characters")
    private String email;

    @NotNull(message = "Location is required")
    private UserLocation location;

    private String profilePicture;
}
