package com.example.CheckInApp.dto.response;

import com.example.CheckInApp.model.Location;
import com.example.CheckInApp.model.Role;
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
    private Location location;
    private boolean status;
    private Set<Role> roles;
}