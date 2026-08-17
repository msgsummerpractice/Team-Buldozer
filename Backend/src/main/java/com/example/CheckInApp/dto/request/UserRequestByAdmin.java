package com.example.CheckInApp.dto.request;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.example.CheckInApp.model.UserRole;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserRequestByAdmin {

    @NotNull(message = "Status cannot be null")
    private boolean status;

    @NotNull(message = "Roles cannot be null")
    @NotEmpty(message = "At least one role must be provided")
    private Set<UserRole> roles;
}