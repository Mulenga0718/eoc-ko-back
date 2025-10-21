package com.ibs.post.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record PostSummaryResponse(
        UUID id,
        String slug,
        String title,
        String summary,
        String category, // Reverted to String
        String coverImageUrl,
        Instant publishedAt,
        List<String> tags
) {
}
