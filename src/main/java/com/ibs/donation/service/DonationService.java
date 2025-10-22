package com.ibs.donation.service;

import com.ibs.donation.client.TossPaymentsClient;
import com.ibs.donation.client.dto.TossPaymentsConfirmResponse;
import com.ibs.donation.config.TossPaymentsProperties;
import com.ibs.donation.domain.Donation;
import com.ibs.donation.dto.DonationConfirmRequest;
import com.ibs.donation.dto.DonationPrepareRequest;
import com.ibs.donation.dto.DonationPrepareResponse;
import com.ibs.donation.dto.DonationResponse;
import com.ibs.donation.repository.DonationRepository;
import com.ibs.donation.service.mapper.DonationMapper;
import com.ibs.global.exception.BusinessException;
import com.ibs.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DonationService {

    private final DonationRepository donationRepository;
    private final TossPaymentsClient tossPaymentsClient;
    private final DonationMapper donationMapper;
    private final TossPaymentsProperties tossPaymentsProperties;

    @Transactional
    public DonationPrepareResponse prepareDonation(DonationPrepareRequest request) {
        String orderId = generateOrderId();

        Donation donation = Donation.createPending(
                orderId,
                request.orderName(),
                request.amount(),
                request.donationType(),
                request.donorName(),
                request.donorEmail(),
                request.donorPhone(),
                request.receiptRequired() // Pass the new field from request
        );

        donationRepository.save(donation);

        return new DonationPrepareResponse(
                donation.getOrderId(),
                donation.getOrderName(),
                donation.getAmount(),
                donation.getDonationType(),
                donation.getStatus(),
                tossPaymentsProperties.clientKey(),
                donation.isReceiptRequired() // Include the new field in the response
        );
    }

    @Transactional
    public DonationResponse confirmDonation(DonationConfirmRequest request) {
        Donation donation = donationRepository.findByOrderId(request.orderId())
                .orElseThrow(() -> new BusinessException(ErrorCode.DONATION_NOT_FOUND));

        if (!donation.getAmount().equals(request.amount())) {
            donation.markFailed("Amount mismatch: expected %d, but %d".formatted(donation.getAmount(), request.amount()));
            throw new BusinessException(ErrorCode.DONATION_AMOUNT_MISMATCH);
        }

        TossPaymentsConfirmResponse confirmResponse = tossPaymentsClient.confirmPayment(request);

        LocalDateTime approvedAt = Optional.ofNullable(confirmResponse.approvedAt())
                .map(OffsetDateTime::toLocalDateTime)
                .orElse(null);
        String receiptUrl = Optional.ofNullable(confirmResponse.receipt()).map(TossPaymentsConfirmResponse.Receipt::url).orElse(null);
        String easyPayProvider = Optional.ofNullable(confirmResponse.easyPay()).map(TossPaymentsConfirmResponse.EasyPay::provider).orElse(null);
        String billingKey = Optional.ofNullable(confirmResponse.card()).map(TossPaymentsConfirmResponse.Card::billingKey).orElse(null);

        donation.markCompleted(
                confirmResponse.paymentKey(),
                approvedAt,
                receiptUrl,
                confirmResponse.method(),
                easyPayProvider,
                billingKey
        );

        return donationMapper.toResponse(donation);
    }

    private String generateOrderId() {
        return "DON-" + UUID.randomUUID().toString().replace("-", "");
    }
}
