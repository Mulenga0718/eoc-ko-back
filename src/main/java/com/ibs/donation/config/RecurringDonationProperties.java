package com.ibs.donation.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@ConfigurationProperties(prefix = "donation.recurring")
public record RecurringDonationProperties(
        List<Integer> chargeDays,
        Duration retryInterval,
        Integer maxRetryCount,
        Duration schedulerInterval
) {
    private static final List<Integer> DEFAULT_CHARGE_DAYS = List.of(5, 10, 15, 25);
    private static final Duration DEFAULT_RETRY_INTERVAL = Duration.ofDays(1);
    private static final Duration DEFAULT_SCHEDULER_INTERVAL = Duration.ofHours(1);
    private static final int DEFAULT_MAX_RETRY = 3;

    public List<Integer> chargeDaysOrDefault() {
        if (chargeDays == null || chargeDays.isEmpty()) {
            return DEFAULT_CHARGE_DAYS;
        }

        return chargeDays.stream()
                .filter(Objects::nonNull)
                .map(day -> Math.min(Math.max(day, 1), 31))
                .distinct()
                .sorted(Comparator.naturalOrder())
                .collect(Collectors.toUnmodifiableList());
    }

    public Duration retryIntervalOrDefault() {
        return retryInterval != null ? retryInterval : DEFAULT_RETRY_INTERVAL;
    }

    public int maxRetryCountOrDefault() {
        return maxRetryCount != null ? maxRetryCount : DEFAULT_MAX_RETRY;
    }

    public Duration schedulerIntervalOrDefault() {
        return schedulerInterval != null ? schedulerInterval : DEFAULT_SCHEDULER_INTERVAL;
    }
}
