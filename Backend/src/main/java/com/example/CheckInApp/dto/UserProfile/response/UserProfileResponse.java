package com.example.CheckInApp.dto.UserProfile.response;

import java.util.Set;

import com.example.CheckInApp.model.Location;
import com.example.CheckInApp.model.Role;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserProfileResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private Location location;
    private boolean status;
    private Set<Role> roles;

}
