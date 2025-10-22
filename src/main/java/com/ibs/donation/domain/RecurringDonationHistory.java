package com.ibs.donation.domain;

import com.ibs.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "recurring_donation_history")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RecurringDonationHistory extends BaseTimeEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "history_id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "schedule_id", nullable = false)
    private RecurringDonationSchedule schedule;

    @Column(nullable = false, length = 64)
    private String orderId;

    @Column(nullable = false)
    private Long amount;

    @Column(length = 120)
    private String paymentKey;

    @Column(nullable = false, length = 20)
    private String status;

    @Column
    private LocalDateTime chargedAt;

    @Column(length = 512)
    private String failureMessage;

    public static RecurringDonationHistory success(RecurringDonationSchedule schedule,
                                                   String orderId,
                                                   Long amount,
                                                   String paymentKey,
                                                   LocalDateTime chargedAt,
                                                   String status) {
        return RecurringDonationHistory.builder()
                .schedule(schedule)
                .orderId(orderId)
                .amount(amount)
                .paymentKey(paymentKey)
                .chargedAt(chargedAt)
                .status(status != null ? status : "SUCCEEDED")
                .build();
    }

    public static RecurringDonationHistory failure(RecurringDonationSchedule schedule,
                                                   String orderId,
                                                   Long amount,
                                                   String failureMessage) {
        return RecurringDonationHistory.builder()
                .schedule(schedule)
                .orderId(orderId)
                .amount(amount)
                .status("FAILED")
                .failureMessage(failureMessage)
                .build();
    }
}
