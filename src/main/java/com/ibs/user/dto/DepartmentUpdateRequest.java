package com.ibs.user.dto;

import jakarta.validation.constraints.NotBlank;

public record DepartmentUpdateRequest(
    @NotBlank(message = "Department name is required.")
    String name,
    Long parentId
) {}