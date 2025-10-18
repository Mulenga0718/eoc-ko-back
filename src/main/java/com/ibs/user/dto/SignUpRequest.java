package com.ibs.user.dto;

import com.ibs.user.domain.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SignUpRequest(
        @NotBlank(message = "Username cannot be blank")
        @Size(min = 4, max = 20, message = "Username must be between 4 and 20 characters")
        String username,

        @NotBlank(message = "Password cannot be blank")
        @Size(min = 6, max = 20, message = "Password must be between 6 and 20 characters")
        String password,

        @NotBlank(message = "Name cannot be blank")
        String name,

        @NotBlank(message = "Email cannot be blank")
        String email,

        String phoneNumber,

        String jobTitle,

        Role role // optional; default MEMBER
) {
}

