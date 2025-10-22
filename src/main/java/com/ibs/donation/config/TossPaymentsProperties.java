package com.ibs.donation.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "toss.payments")
public record TossPaymentsProperties(
        String secretKey,
        String clientKey,
        String baseUrl
) {
    public String baseUrlOrDefault() {
        return baseUrl != null && !baseUrl.isBlank() ? baseUrl : "https://api.tosspayments.com";
    }
}
