package com.ibs.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // Common Errors
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "C001", "Invalid Input"),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "C002", "Method Not Allowed"),
    NOT_FOUND(HttpStatus.NOT_FOUND, "C003", "Resource Not Found"), // 기존 NOT_FOUND 유지
    ENTITY_NOT_FOUND(HttpStatus.NOT_FOUND, "C007", "Entity Not Found"), // ENTITY_NOT_FOUND 추가
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C004", "Server Error"),
    INVALID_TYPE_VALUE(HttpStatus.BAD_REQUEST, "C005", "Invalid Type Value"),
    DUPLICATE_ENTITY(HttpStatus.CONFLICT, "C006", "Duplicate Entity"),
    FILE_NOT_FOUND(HttpStatus.NOT_FOUND, "C008", "File Not Found"), // FILE_NOT_FOUND 추가

    // Authentication Errors
    HANDLE_ACCESS_DENIED(HttpStatus.FORBIDDEN, "A001", "Access is Denied"),
    AUTHENTICATION_FAILED(HttpStatus.UNAUTHORIZED, "A002", "Authentication Failed"),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "A003", "Invalid Refresh Token"),
    EXPIRED_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "A004", "Expired Refresh Token");

    private final HttpStatus status;
    private final String code;
    private final String message;

    // 명시적 생성자 정의
    ErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }

    // 명시적 getter 메소드 추가
    public HttpStatus getStatus() {
        return status;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
