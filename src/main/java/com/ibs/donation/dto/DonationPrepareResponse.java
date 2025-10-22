package com.ibs.donation.dto;

import com.ibs.donation.domain.DonationStatus;
import com.ibs.donation.domain.DonationType;

public record DonationPrepareResponse(
        String orderId,
        String orderName,
        Long amount,
        DonationType donationType,
        DonationStatus status,
        String clientKey,
        boolean receiptRequired
) {
}
