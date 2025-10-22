package com.ibs.donation.service.support;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public final class RecurringChargeDateCalculator {

    private RecurringChargeDateCalculator() {
    }

    public static LocalDateTime calculateNextChargeOnDay(LocalDateTime referenceTime, int chargeDay) {
        Objects.requireNonNull(referenceTime, "referenceTime must not be null");
        int sanitizedDay = sanitizeDay(chargeDay);

        LocalDate referenceDate = referenceTime.toLocalDate();
        LocalTime chargeTime = referenceTime.toLocalTime();

        LocalDateTime candidate = LocalDateTime.of(adjustDate(referenceDate, sanitizedDay), chargeTime);
        if (candidate.isAfter(referenceTime)) {
            return candidate;
        }

        LocalDate nextMonthDate = referenceDate.plusMonths(1);
        return LocalDateTime.of(adjustDate(nextMonthDate, sanitizedDay), chargeTime);
    }

    public static LocalDateTime calculateNextChargeOnDays(LocalDateTime referenceTime, List<Integer> chargeDays) {
        Objects.requireNonNull(referenceTime, "referenceTime must not be null");
        Objects.requireNonNull(chargeDays, "chargeDays must not be null");

        List<Integer> sortedDays = chargeDays.stream()
                .filter(Objects::nonNull)
                .map(RecurringChargeDateCalculator::sanitizeDay)
                .distinct()
                .sorted(Comparator.naturalOrder())
                .collect(Collectors.toList());

        if (sortedDays.isEmpty()) {
            throw new IllegalArgumentException("chargeDays must not be empty");
        }

        LocalDate referenceDate = referenceTime.toLocalDate();
        LocalTime chargeTime = referenceTime.toLocalTime();

        LocalDateTime nextInCurrentMonth = findCandidate(referenceTime, referenceDate, chargeTime, sortedDays);
        if (nextInCurrentMonth != null) {
            return nextInCurrentMonth;
        }

        LocalDate nextMonthBase = referenceDate.plusMonths(1).withDayOfMonth(1);
        LocalDateTime nextInNextMonth = findCandidate(referenceTime, nextMonthBase, chargeTime, sortedDays);
        if (nextInNextMonth != null) {
            return nextInNextMonth;
        }

        throw new IllegalStateException("Unable to calculate next charge date for the provided schedule");
    }

    private static LocalDateTime findCandidate(LocalDateTime referenceTime,
                                               LocalDate baseDate,
                                               LocalTime chargeTime,
                                               List<Integer> chargeDays) {
        for (Integer day : chargeDays) {
            LocalDate candidateDate = adjustDate(baseDate, day);
            LocalDateTime candidate = LocalDateTime.of(candidateDate, chargeTime);
            if (candidate.isAfter(referenceTime)) {
                return candidate;
            }
        }
        return null;
    }

    private static LocalDate adjustDate(LocalDate baseDate, int dayOfMonth) {
        int lengthOfMonth = baseDate.lengthOfMonth();
        int effectiveDay = Math.min(Math.max(dayOfMonth, 1), lengthOfMonth);
        return baseDate.withDayOfMonth(effectiveDay);
    }

    private static int sanitizeDay(int day) {
        if (day < 1) {
            throw new IllegalArgumentException("chargeDay must be greater than 0");
        }
        return Math.min(day, 31);
    }
}
