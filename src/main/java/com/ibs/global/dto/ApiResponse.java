package com.ibs.global.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL) // null이 아닌 필드만 JSON에 포함
public class ApiResponse<T> {

    private boolean success;
    private T data;

    // 성공 응답 (데이터 포함)
    public static <T> ApiResponse<T> success(T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.success = true;
        response.data = data;
        return response;
    }

    // 에러 응답 (ErrorResponse를 데이터로 포함)
    public static <T> ApiResponse<T> error(T errorData) {
        ApiResponse<T> response = new ApiResponse<>();
        response.success = false;
        response.data = errorData;
        return response;
    }
}
