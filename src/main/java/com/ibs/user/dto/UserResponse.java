package com.ibs.user.dto;

import com.ibs.user.domain.Role;
import com.ibs.user.domain.User;
import com.ibs.user.domain.UserStatus;

import java.util.UUID;

public record UserResponse(
        UUID id,
        String username,
        String name,
        String email,
        String phoneNumber,
        String jobTitle,
        Role role,
        UserStatus status
) {
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getName(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getJobTitle(),
                user.getRole(),
                user.getStatus()
        );
    }
}
