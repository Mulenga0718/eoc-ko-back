package com.ibs.post.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CategoryUpdateRequest(
        @NotBlank(message = "Category name cannot be blank")
        String name,
        @NotNull(message = "Display order cannot be null")
        Integer displayOrder
) {
}
