package com.ibs.post.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder
public record PostCreateRequest(
        @NotBlank @Size(max = 160) String title,
        @NotBlank @Size(max = 255) String summary,
        @NotBlank String content,
        @NotBlank String category, // Reverted to String
        String coverImageUrl,
        List<String> tags,
        List<UUID> recommendedPostIds,
        List<UUID> relatedPostIds,
        boolean publish
) {
}
