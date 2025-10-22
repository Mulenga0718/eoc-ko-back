package com.ibs.donation.dto;

import com.ibs.donation.domain.DonationStatus;
import com.ibs.donation.domain.DonationType;

import java.time.LocalDateTime;
import java.util.UUID;

public record DonationResponse(
        UUID id,
        String orderId,
        String orderName,
        Long amount,
        String currency,
        DonationType donationType,
        DonationStatus status,
        String paymentKey,
        String paymentMethod,
        String easyPayProvider,
        String receiptUrl,
        String donorName,
        String donorEmail,
        String donorPhone,
        Integer recurringChargeDay,
        LocalDateTime approvedAt,
        String failureMessage,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
