package com.ibs.donation.dto;

import com.ibs.donation.domain.DonationType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record DonationPrepareRequest(
        @NotBlank(message = "Order name is required.")
        @Size(max = 100, message = "Order name must be 100 characters or less.")
        String orderName,

        @NotNull(message = "Donation amount is required.")
        @Positive(message = "Donation amount must be greater than zero.")
        Long amount,

        DonationType donationType,

        @NotBlank(message = "Donor name is required.")
        @Size(max = 100, message = "Donor name must be 100 characters or less.")
        String donorName,

        @NotBlank(message = "Email address is required.")
        @Email(message = "Please provide a valid email address.")
        @Size(max = 120, message = "Email address must be 120 characters or less.")
        String donorEmail,
        @Size(max = 30, message = "Phone number must be 30 characters or less.")
        String donorPhone,

        @NotNull(message = "Receipt required status is required.")
        boolean receiptRequired
) {
    public DonationPrepareRequest {
        if (donationType == null) {
            donationType = DonationType.ONE_TIME;
        }
    }
}
