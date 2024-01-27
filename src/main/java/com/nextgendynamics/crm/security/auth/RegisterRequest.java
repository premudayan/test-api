package com.nextgendynamics.crm.security.auth;

import com.nextgendynamics.crm.security.user.Role;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

    @NotNull(message = "First Name cannot be empty")
    @NotEmpty(message = "First Name cannot be empty")
    private String firstname;
    @NotNull(message = "Last Name cannot be empty")
    @NotEmpty(message = "Last Name cannot be empty")
    private String lastname;
    @NotNull(message = "Email cannot be empty")
    @NotEmpty(message = "Email cannot be empty")
    private String email;
    @NotNull(message = "Password cannot be empty")
    @NotEmpty(message = "Password cannot be empty")
    private String password;
    @NotNull(message = "Role cannot be empty. Available roles are ADMIN, MANAGER and USER.")
    @NotEmpty(message = "Role cannot be empty. Available roles are ADMIN, MANAGER and USER.")
    private Role role;
}
