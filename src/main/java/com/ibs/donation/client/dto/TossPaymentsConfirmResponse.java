package com.ibs.donation.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.OffsetDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TossPaymentsConfirmResponse(
        String paymentKey,
        String orderId,
        String orderName,
        String status,
        Long totalAmount,
        String method,
        OffsetDateTime requestedAt,
        OffsetDateTime approvedAt,
        EasyPay easyPay,
        Receipt receipt,
        Card card
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record EasyPay(String provider) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Receipt(String url) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Card(String billingKey) {
    }
}
