package com.ibs.user.dto;

import jakarta.validation.constraints.NotNull;

public record DepartmentReorderRequest(
    @NotNull
    Direction direction
) {
    public enum Direction {
        UP, DOWN
    }
}