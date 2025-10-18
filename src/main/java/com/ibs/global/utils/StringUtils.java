package com.ibs.global.utils;

import java.security.SecureRandom;
import java.util.Base64;

public class StringUtils {

    private static final SecureRandom secureRandom = new SecureRandom();
    private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder();

    /**
     * 주어진 문자열이 null이거나 비어있는지 (공백만 있는 경우 포함) 확인합니다.
     *
     * @param str 확인할 문자열
     * @return 문자열이 null이거나 비어있으면 true, 그렇지 않으면 false
     */
    public static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    /**
     * 주어진 문자열이 null이 아니고 비어있지 않은지 (공백만 있는 경우 포함) 확인합니다.
     *
     * @param str 확인할 문자열
     * @return 문자열이 null이 아니고 비어있지 않으면 true, 그렇지 않으면 false
     */
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    /**
     * 지정된 길이의 안전한 무작위 문자열을 생성합니다.
     *
     * @param length 생성할 문자열의 길이 (바이트 단위)
     * @return Base64 인코딩된 무작위 문자열
     */
    public static String generateRandomString(int length) {
        byte[] randomBytes = new byte[length];
        secureRandom.nextBytes(randomBytes);
        return base64Encoder.encodeToString(randomBytes);
    }
}
