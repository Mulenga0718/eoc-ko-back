package com.ibs.donation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record DonationConfirmRequest(
        @NotBlank(message = "paymentKey is required.")
        String paymentKey,

        @NotBlank(message = "orderId is required.")
        String orderId,

        @NotNull(message = "Donation amount is required.")
        @Positive(message = "Donation amount must be greater than zero.")
        Long amount
) {
}
