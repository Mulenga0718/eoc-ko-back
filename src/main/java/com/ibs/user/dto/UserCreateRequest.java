package com.ibs.user.dto;

import com.ibs.user.domain.Role;
import com.ibs.user.domain.UserStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record UserCreateRequest(
        @NotBlank(message = "Username is required")
        @Size(max = 50, message = "Username must be <= 50 chars")
        String username,

        @NotBlank(message = "Name is required")
        @Size(max = 100, message = "Name must be <= 100 chars")
        String name,

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        @Size(max = 255, message = "Email must be <= 255 chars")
        String email,

        String phoneNumber,

        @Size(max = 100, message = "Job title must be <= 100 chars")
        String jobTitle,

        @NotNull(message = "Role is required")
        Role role,

        @NotNull(message = "Status is required")
        UserStatus status,

        @Size(min = 4, max = 100, message = "Password must be 4-100 chars")
        String password
) {
}

