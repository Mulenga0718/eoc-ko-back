package com.ibs.donation.service;

import com.ibs.donation.client.TossPaymentsClient;
import com.ibs.donation.client.dto.TossPaymentsConfirmResponse;
import com.ibs.donation.config.RecurringDonationProperties;
import com.ibs.donation.config.TossPaymentsProperties;
import com.ibs.donation.domain.Donation;
import com.ibs.donation.domain.DonationType;
import com.ibs.donation.domain.RecurringDonationSchedule;
import com.ibs.donation.dto.DonationConfirmRequest;
import com.ibs.donation.dto.DonationPrepareRequest;
import com.ibs.donation.dto.DonationPrepareResponse;
import com.ibs.donation.dto.DonationResponse;
import com.ibs.donation.repository.DonationRepository;
import com.ibs.donation.repository.RecurringDonationScheduleRepository;
import com.ibs.donation.service.mapper.DonationMapper;
import com.ibs.donation.service.support.RecurringChargeDateCalculator;
import com.ibs.global.exception.BusinessException;
import com.ibs.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
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
    private final RecurringDonationProperties recurringDonationProperties;
    private final RecurringDonationScheduleRepository recurringDonationScheduleRepository;

    @Transactional
    public DonationPrepareResponse prepareDonation(DonationPrepareRequest request) {
        String orderId = generateOrderId();

        Integer recurringChargeDay = determineRecurringChargeDay(request);

        Donation donation = Donation.createPending(
                orderId,
                request.orderName(),
                request.amount(),
                request.donationType(),
                request.donorName(),
                request.donorEmail(),
                request.donorPhone(),
                request.receiptRequired(), // Pass the new field from request
                recurringChargeDay
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

        if (donation.getDonationType() == DonationType.RECURRING && billingKey != null && !billingKey.isBlank()) {
            initializeRecurringSchedule(donation, approvedAt);
        }

        return donationMapper.toResponse(donation);
    }

    private String generateOrderId() {
        return "DON-" + UUID.randomUUID().toString().replace("-", "");
    }

    private void initializeRecurringSchedule(Donation donation, LocalDateTime approvedAt) {
        if (recurringDonationScheduleRepository.existsByDonation(donation)) {
            return;
        }

        LocalDateTime baseTime = Optional.ofNullable(approvedAt).orElse(LocalDateTime.now());
        LocalDateTime nextChargeAt = calculateNextChargeAt(baseTime, donation);
        RecurringDonationSchedule schedule = RecurringDonationSchedule.create(
                donation,
                baseTime,
                recurringDonationProperties.retryIntervalOrDefault(),
                recurringDonationProperties.maxRetryCountOrDefault(),
                nextChargeAt
        );
        recurringDonationScheduleRepository.save(schedule);
    }

    private Integer determineRecurringChargeDay(DonationPrepareRequest request) {
        if (request.donationType() != DonationType.RECURRING) {
            return null;
        }

        Integer requestedDay = request.recurringChargeDay();
        List<Integer> allowedDays = recurringDonationProperties.chargeDaysOrDefault();
        if (requestedDay == null) {
            return allowedDays.get(0);
        }

        if (!allowedDays.contains(requestedDay)) {
            throw new BusinessException(ErrorCode.INVALID_RECURRING_CHARGE_DAY);
        }

        return requestedDay;
    }

    private LocalDateTime calculateNextChargeAt(LocalDateTime reference, Donation donation) {
        Integer chargeDay = donation.getRecurringChargeDay();
        if (chargeDay != null) {
            return RecurringChargeDateCalculator.calculateNextChargeOnDay(reference, chargeDay);
        }

        return RecurringChargeDateCalculator.calculateNextChargeOnDays(reference, recurringDonationProperties.chargeDaysOrDefault());
    }
}
