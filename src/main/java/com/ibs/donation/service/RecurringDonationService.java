package com.ibs.donation.service;

import com.ibs.donation.client.TossPaymentsClient;
import com.ibs.donation.client.dto.TossPaymentsBillingKeyChargeRequest;
import com.ibs.donation.client.dto.TossPaymentsConfirmResponse;
import com.ibs.donation.config.RecurringDonationProperties;
import com.ibs.donation.domain.Donation;
import com.ibs.donation.domain.DonationType;
import com.ibs.donation.domain.RecurringDonationHistory;
import com.ibs.donation.domain.RecurringDonationSchedule;
import com.ibs.donation.exception.TossPaymentsApiException;
import com.ibs.donation.repository.RecurringDonationHistoryRepository;
import com.ibs.donation.repository.RecurringDonationScheduleRepository;
import com.ibs.donation.service.support.RecurringChargeDateCalculator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecurringDonationService {

    private static final DateTimeFormatter ORDER_ID_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private final RecurringDonationScheduleRepository scheduleRepository;
    private final RecurringDonationHistoryRepository historyRepository;
    private final TossPaymentsClient tossPaymentsClient;
    private final RecurringDonationProperties recurringDonationProperties;

    @Scheduled(fixedDelayString = "${donation.recurring.scheduler-interval:PT1H}")
    @Transactional
    public void processRecurringDonations() {
        LocalDateTime now = LocalDateTime.now();
        List<RecurringDonationSchedule> schedules = scheduleRepository.findByActiveTrueAndNextChargeAtLessThanEqual(now);

        if (schedules.isEmpty()) {
            return;
        }

        for (RecurringDonationSchedule schedule : schedules) {
            Donation donation = schedule.getDonation();

            if (donation.getDonationType() != DonationType.RECURRING) {
                log.warn("Donation {} is not marked as recurring. Deactivating schedule {}.", donation.getId(), schedule.getId());
                schedule.deactivate();
                continue;
            }

            if (donation.getBillingKey() == null || donation.getBillingKey().isBlank()) {
                log.warn("Recurring donation {} lacks a billing key. Deactivating schedule {}.", donation.getId(), schedule.getId());
                schedule.deactivate();
                continue;
            }

            String orderId = buildRecurringOrderId(donation.getOrderId());
            TossPaymentsBillingKeyChargeRequest request = new TossPaymentsBillingKeyChargeRequest(
                    orderId,
                    donation.getAmount(),
                    donation.getOrderName(),
                    donation.getDonorName(),
                    donation.getDonorEmail(),
                    donation.getDonorPhone(),
                    null
            );

            try {
                TossPaymentsConfirmResponse response = tossPaymentsClient.chargeBillingKey(donation.getBillingKey(), request);
                LocalDateTime approvedAt = Optional.ofNullable(response.approvedAt())
                        .map(OffsetDateTime::toLocalDateTime)
                        .orElse(LocalDateTime.now());

                LocalDateTime nextChargeAt = determineNextChargeAt(approvedAt, donation);
                schedule.markChargeSuccess(approvedAt, nextChargeAt);
                historyRepository.save(RecurringDonationHistory.success(
                        schedule,
                        orderId,
                        donation.getAmount(),
                        response.paymentKey(),
                        approvedAt,
                        response.status()
                ));

                log.info("Recurring donation charged successfully. scheduleId={}, orderId={}, paymentKey={}",
                        schedule.getId(),
                        orderId,
                        response.paymentKey());
            } catch (TossPaymentsApiException ex) {
                handleFailure(schedule, donation, orderId, ex);
            } catch (Exception ex) {
                handleFailure(schedule, donation, orderId, ex);
            }
        }
    }

    private void handleFailure(RecurringDonationSchedule schedule,
                               Donation donation,
                               String orderId,
                               Exception exception) {
        LocalDateTime attemptTime = LocalDateTime.now();
        LocalDateTime nextRetryAt = null;
        if (schedule.getRetryCount() + 1 < schedule.getMaxRetryCount()) {
            long retrySeconds = Math.max(1, schedule.getRetryIntervalSeconds());
            LocalDateTime retryBaseline = attemptTime.plusSeconds(retrySeconds);
            nextRetryAt = determineNextChargeAt(retryBaseline, donation);
        }

        schedule.markChargeFailure(attemptTime, nextRetryAt);
        historyRepository.save(RecurringDonationHistory.failure(
                schedule,
                orderId,
                donation.getAmount(),
                exception.getMessage()
        ));

        if (!schedule.isActive()) {
            log.error("Recurring donation charge failed and schedule was deactivated after reaching retry limit. scheduleId={}, orderId={}, message={}",
                    schedule.getId(),
                    orderId,
                    exception.getMessage(),
                    exception);
        } else {
            log.error("Recurring donation charge failed. scheduleId={}, orderId={}, nextAttemptAt={}, message={}",
                    schedule.getId(),
                    orderId,
                    schedule.getNextChargeAt(),
                    exception.getMessage(),
                    exception);
        }
    }

    private String buildRecurringOrderId(String baseOrderId) {
        String base = baseOrderId != null && !baseOrderId.isBlank() ? baseOrderId : "DON";
        String suffix = ORDER_ID_FORMATTER.format(LocalDateTime.now());
        int maxBaseLength = Math.max(1, 64 - suffix.length() - 3);
        String normalizedBase = base.length() > maxBaseLength ? base.substring(0, maxBaseLength) : base;
        return normalizedBase + "-R-" + suffix;
    }

    private LocalDateTime determineNextChargeAt(LocalDateTime reference, Donation donation) {
        Integer chargeDay = donation.getRecurringChargeDay();
        if (chargeDay != null) {
            return RecurringChargeDateCalculator.calculateNextChargeOnDay(reference, chargeDay);
        }

        return RecurringChargeDateCalculator.calculateNextChargeOnDays(reference, recurringDonationProperties.chargeDaysOrDefault());
    }
}
