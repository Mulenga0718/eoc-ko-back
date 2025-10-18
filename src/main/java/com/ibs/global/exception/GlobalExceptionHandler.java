package com.ibs.global.exception;

import com.ibs.global.dto.ApiResponse;
import com.ibs.global.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.AccessDeniedException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * @Valid, @Validated 에러를 처리
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ApiResponse<Object>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("handleMethodArgumentNotValidException", e);
        final ErrorCode errorCode = ErrorCode.INVALID_INPUT;
        final ErrorResponse errorResponse = new ErrorResponse(errorCode.getStatus().value(), errorCode.getCode(), e.getBindingResult().getAllErrors().get(0).getDefaultMessage());
        return new ResponseEntity<>(ApiResponse.error(errorResponse), HttpStatus.valueOf(errorCode.getStatus().value()));
    }

    /**
     * 지원하지 않는 HTTP method 호출 시 발생
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    protected ResponseEntity<ApiResponse<Object>> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        log.error("handleHttpRequestMethodNotSupportedException", e);
        final ErrorCode errorCode = ErrorCode.METHOD_NOT_ALLOWED;
        final ErrorResponse errorResponse = new ErrorResponse(errorCode.getStatus().value(), errorCode.getCode(), errorCode.getMessage());
        return new ResponseEntity<>(ApiResponse.error(errorResponse), HttpStatus.valueOf(errorCode.getStatus().value()));
    }

    /**
     * Authentication 객체가 필요한 권한을 보유하지 않은 경우 발생
     */
    @ExceptionHandler(AccessDeniedException.class)
    protected ResponseEntity<ApiResponse<Object>> handleAccessDeniedException(AccessDeniedException e) {
        log.error("handleAccessDeniedException", e);
        final ErrorCode errorCode = ErrorCode.HANDLE_ACCESS_DENIED;
        final ErrorResponse errorResponse = new ErrorResponse(errorCode.getStatus().value(), errorCode.getCode(), errorCode.getMessage());
        return new ResponseEntity<>(ApiResponse.error(errorResponse), HttpStatus.valueOf(errorCode.getStatus().value()));
    }

    /**
     * 비즈니스 로직 수행 중 발생하는 에러를 처리
     */
    @ExceptionHandler(BusinessException.class)
    protected ResponseEntity<ApiResponse<Object>> handleBusinessException(final BusinessException e) {
        log.error("handleBusinessException", e);
        final ErrorCode errorCode = e.getErrorCode();
        final ErrorResponse errorResponse = new ErrorResponse(errorCode.getStatus().value(), errorCode.getCode(), e.getMessage());
        return new ResponseEntity<>(ApiResponse.error(errorResponse), HttpStatus.valueOf(errorCode.getStatus().value()));
    }

    /**
     * 위에 명시되지 않은 모든 예외를 처리
     */
    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ApiResponse<Object>> handleException(Exception e) {
        log.error("handleException", e);
        final ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
        final ErrorResponse errorResponse = new ErrorResponse(errorCode.getStatus().value(), errorCode.getCode(), errorCode.getMessage());
        return new ResponseEntity<>(ApiResponse.error(errorResponse), HttpStatus.valueOf(errorCode.getStatus().value()));
    }
}
