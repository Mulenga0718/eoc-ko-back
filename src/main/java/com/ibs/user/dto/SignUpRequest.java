package com.ibs.user.dto;

import com.ibs.user.domain.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SignUpRequest(
        @NotBlank(message = "Login ID cannot be blank")
        @Size(min = 4, max = 64, message = "Login ID must be between 4 and 64 characters")
        String loginId,

        @NotBlank(message = "Password cannot be blank")
        @Size(min = 6, max = 20, message = "Password must be between 6 and 20 characters")
        String password,

        @NotBlank(message = "Name cannot be blank")
        String name,

        @NotBlank(message = "Email cannot be blank")
        @Email(message = "Email should be valid")
        String email,

        String phoneNumber,

        String jobTitle,

        Role role
) {
}
