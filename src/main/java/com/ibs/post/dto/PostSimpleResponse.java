package com.ibs.post.dto;

import java.time.Instant;
import java.util.UUID;

public record PostSimpleResponse(
        UUID id,
        String slug,
        String title,
        String summary,
        String category, // Reverted to String
        String coverImageUrl,
        Instant publishedAt
) {
}
