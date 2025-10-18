package com.ibs.user.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "jwt")
public record JwtProperties(
        String secret,
        long tokenValidityInSeconds,
        long refreshTokenValidityInSeconds
) {
}
