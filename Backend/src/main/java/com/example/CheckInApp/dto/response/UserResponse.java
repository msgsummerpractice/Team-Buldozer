package com.example.CheckInApp.dto.response;

import com.example.CheckInApp.model.UserLocation;
import com.example.CheckInApp.model.UserRole;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@Builder
public class UserResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private UserLocation userLocation;
    private boolean status;
    private Set<UserRole> userRoles;
    private String profilePicture;
}