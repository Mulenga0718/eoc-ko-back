package com.ibs.post.dto;

import java.time.Instant;
import java.time.LocalDateTime; // Changed from OffsetDateTime
import java.util.List;
import java.util.UUID;

public record PostDetailResponse(
        UUID id,
        String slug,
        String title,
        String summary,
        String content,
        String category, // Will be changed later in the refactoring
        String coverImageUrl,
        Instant publishedAt,
        PostAuthorResponse author,
        List<String> tags,
        List<PostSimpleResponse> recommendedPosts,
        List<PostSimpleResponse> relatedPosts,
        long viewCount,
        LocalDateTime createdAt, // Changed from OffsetDateTime
        LocalDateTime updatedAt // Changed from OffsetDateTime
) {
}
