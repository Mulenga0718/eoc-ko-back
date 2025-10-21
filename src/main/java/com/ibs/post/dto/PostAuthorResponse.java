package com.ibs.post.dto;

import java.util.UUID;

public record PostAuthorResponse(
        UUID id,
        String name,
        String email,
        String jobTitle,
        String profileImageUrl
) {
}
