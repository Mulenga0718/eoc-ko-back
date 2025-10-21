package com.ibs.post.dto;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CategoryOrderUpdateRequest(
        @NotNull(message = "Category IDs cannot be null")
        List<Long> categoryIds
) {
}
