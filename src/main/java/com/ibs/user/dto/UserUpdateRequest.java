package com.ibs.user.dto;

import com.ibs.user.domain.Role;
import com.ibs.user.domain.UserStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UserUpdateRequest(
        @NotBlank(message = "Username is required")
        @Size(min = 4, max = 20, message = "Username must be 4-20 chars")
        String username,

        @Size(min = 8, message = "Password must be >= 8 chars")
        String password,

        @NotBlank(message = "Name is required")
        String name,

        @NotBlank(message = "Email is required")
        String email,

        String phoneNumber,

        String jobTitle,

        String profileImage,

        @NotNull(message = "Role is required")
        Role role,

        @NotNull(message = "Status is required")
        UserStatus status
) {
}

