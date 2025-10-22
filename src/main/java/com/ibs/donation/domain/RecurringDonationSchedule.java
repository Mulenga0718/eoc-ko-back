package com.ibs.donation.domain;

import com.ibs.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "recurring_donation_schedules")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RecurringDonationSchedule extends BaseTimeEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "schedule_id", updatable = false, nullable = false)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "donation_id", nullable = false, unique = true)
    private Donation donation;

    @Column(nullable = false)
    private LocalDateTime nextChargeAt;

    @Column
    private LocalDateTime lastChargedAt;

    @Column(nullable = false)
    private int retryCount;

    @Column(nullable = false)
    private int maxRetryCount;

    @Column(nullable = false)
    private long retryIntervalSeconds;

    @Column(nullable = false)
    private boolean active;

    public static RecurringDonationSchedule create(Donation donation,
                                                   LocalDateTime lastChargedAt,
                                                   Duration retryInterval,
                                                   int maxRetryCount,
                                                   LocalDateTime nextChargeAt) {
        Objects.requireNonNull(donation, "donation must not be null");
        Objects.requireNonNull(retryInterval, "retryInterval must not be null");
        Objects.requireNonNull(nextChargeAt, "nextChargeAt must not be null");

        long retrySeconds = Math.max(1, retryInterval.toSeconds());
        int effectiveMaxRetryCount = Math.max(0, maxRetryCount);

        return RecurringDonationSchedule.builder()
                .donation(donation)
                .lastChargedAt(lastChargedAt)
                .nextChargeAt(nextChargeAt)
                .retryCount(0)
                .maxRetryCount(effectiveMaxRetryCount)
                .retryIntervalSeconds(retrySeconds)
                .active(true)
                .build();
    }

    public Duration retryInterval() {
        return Duration.ofSeconds(retryIntervalSeconds);
    }

    public void markChargeSuccess(LocalDateTime chargedAt, LocalDateTime nextChargeAt) {
        LocalDateTime baseTime = chargedAt != null ? chargedAt : LocalDateTime.now();
        Objects.requireNonNull(nextChargeAt, "nextChargeAt must not be null");
        if (!nextChargeAt.isAfter(baseTime)) {
            throw new IllegalArgumentException("nextChargeAt must be after the charge time");
        }

        this.lastChargedAt = baseTime;
        this.retryCount = 0;
        this.nextChargeAt = nextChargeAt;
        this.active = true;
    }

    public void markChargeFailure(LocalDateTime attemptedAt, LocalDateTime nextRetryAt) {
        LocalDateTime baseTime = attemptedAt != null ? attemptedAt : LocalDateTime.now();
        this.retryCount += 1;
        if (this.retryCount >= this.maxRetryCount) {
            this.active = false;
        } else {
            Objects.requireNonNull(nextRetryAt, "nextRetryAt must not be null when schedule remains active");
            if (!nextRetryAt.isAfter(baseTime)) {
                throw new IllegalArgumentException("nextRetryAt must be after the attempted time");
            }
            this.nextChargeAt = nextRetryAt;
        }
    }

    public void deactivate() {
        this.active = false;
    }
}
