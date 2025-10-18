package com.ibs.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TokenResponse(
        @JsonProperty("token_type")
        String tokenType,
        @JsonProperty("access_token")
        String accessToken,
        @JsonProperty("refresh_token")
        String refreshToken
) {
    public TokenResponse(String accessToken, String refreshToken) {
        this("Bearer", accessToken, refreshToken);
    }
}
