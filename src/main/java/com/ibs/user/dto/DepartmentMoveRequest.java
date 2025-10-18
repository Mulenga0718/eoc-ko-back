package com.ibs.user.dto;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record DepartmentMoveRequest(
    Long targetParentId,
    @NotNull
    List<Long> siblingIdsInOrder
) {}