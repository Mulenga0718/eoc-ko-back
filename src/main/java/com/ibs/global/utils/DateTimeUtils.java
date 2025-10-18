package com.ibs.global.utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class DateTimeUtils {

    private static final String DEFAULT_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final DateTimeFormatter DEFAULT_FORMATTER = DateTimeFormatter.ofPattern(DEFAULT_DATE_TIME_FORMAT);

    /**
     * LocalDateTime을 기본 형식(yyyy-MM-dd HH:mm:ss)의 문자열로 포매팅합니다.
     *
     * @param dateTime 포매팅할 LocalDateTime 객체
     * @return 포매팅된 문자열
     */
    public static String format(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.format(DEFAULT_FORMATTER);
    }

    /**
     * LocalDateTime을 지정된 형식의 문자열로 포매팅합니다.
     *
     * @param dateTime 포매팅할 LocalDateTime 객체
     * @param pattern  포매팅할 패턴 (예: "yyyy/MM/dd HH:mm:ss")
     * @return 포매팅된 문자열
     */
    public static String format(LocalDateTime dateTime, String pattern) {
        if (dateTime == null || pattern == null) {
            return null;
        }
        return dateTime.format(DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * Instant를 LocalDateTime으로 변환합니다.
     *
     * @param instant 변환할 Instant 객체
     * @return 변환된 LocalDateTime 객체
     */
    public static LocalDateTime toLocalDateTime(Instant instant) {
        if (instant == null) {
            return null;
        }
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }

    /**
     * LocalDateTime을 Instant로 변환합니다.
     *
     * @param localDateTime 변환할 LocalDateTime 객체
     * @return 변환된 Instant 객체
     */
    public static Instant toInstant(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant();
    }
}
