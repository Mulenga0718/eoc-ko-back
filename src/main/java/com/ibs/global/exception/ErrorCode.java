package com.ibs.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // Common Errors
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "C001", "Invalid input."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "C002", "HTTP method is not allowed."),
    NOT_FOUND(HttpStatus.NOT_FOUND, "C003", "Resource not found."),
    ENTITY_NOT_FOUND(HttpStatus.NOT_FOUND, "C007", "Entity not found."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C004", "Unexpected server error."),
    INVALID_TYPE_VALUE(HttpStatus.BAD_REQUEST, "C005", "Invalid type value."),
    DUPLICATE_ENTITY(HttpStatus.CONFLICT, "C006", "Duplicate entity."),
    FILE_NOT_FOUND(HttpStatus.NOT_FOUND, "C008", "File not found."),

    // Authentication Errors
    HANDLE_ACCESS_DENIED(HttpStatus.FORBIDDEN, "A001", "Access is denied."),
    AUTHENTICATION_FAILED(HttpStatus.UNAUTHORIZED, "A002", "Authentication failed."),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "A003", "Refresh token is invalid."),
    EXPIRED_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "A004", "Refresh token expired."),

    // Payment & Donation Errors
    PAYMENT_CONFIRM_FAILED(HttpStatus.BAD_GATEWAY, "P001", "Failed to confirm the payment."),
    DONATION_NOT_FOUND(HttpStatus.NOT_FOUND, "D001", "Donation record was not found."),
    DONATION_AMOUNT_MISMATCH(HttpStatus.BAD_REQUEST, "D002", "Donation amount does not match."),
    INVALID_RECURRING_CHARGE_DAY(HttpStatus.BAD_REQUEST, "D003", "Invalid recurring charge day provided.");

    private final HttpStatus status;
    private final String code;
    private final String message;

    ErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }

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
