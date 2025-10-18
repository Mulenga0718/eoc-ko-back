package com.ibs.user.dto;

import jakarta.validation.constraints.NotBlank;

public record DepartmentCreateRequest(
    @NotBlank(message = "Department name is required.")
    String name,
    Long parentId
) {}