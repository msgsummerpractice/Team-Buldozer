package com.example.CheckInApp.dto.request;

import com.example.CheckInApp.model.Location;

import jakarta.validation.constraints.Email;
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

    @Size(max = 32, message = "First name must be at most 32 characters")
    private String firstName;

    @Size(max = 32, message = "Last name must be at most 32 characters")
    private String lastName;

    @Email(message = "Email is not valid")
    @Size(max = 64, message = "Email must be at most 64 characters")
    private String email;

    private Location location;

    private String profilePicture; // Base64 encoded string
}
