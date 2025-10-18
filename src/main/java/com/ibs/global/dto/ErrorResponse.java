package com.ibs.global.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL) // null이 아닌 필드만 JSON에 포함
public class ErrorResponse {

    private final int status;
    private final String code;
    private final String message;

    public ErrorResponse(int status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
