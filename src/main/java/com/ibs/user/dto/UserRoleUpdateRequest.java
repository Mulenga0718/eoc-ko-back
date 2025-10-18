package com.ibs.user.dto;

import com.ibs.user.domain.Role;
import jakarta.validation.constraints.NotNull;

public record UserRoleUpdateRequest(
    @NotNull(message = "역할은 필수값입니다.")
    Role role
) {
}
