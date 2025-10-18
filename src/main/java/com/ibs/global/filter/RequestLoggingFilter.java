package com.ibs.global.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Component
@Slf4j
public class RequestLoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // Swagger 요청은 로깅에서 제외
        if (request.getRequestURI().contains("swagger") || request.getRequestURI().contains("api-docs")) {
            filterChain.doFilter(request, response);
            return;
        }

        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

        long startTime = System.currentTimeMillis();
        String requestId = UUID.randomUUID().toString().substring(0, 8);

        log.info("[Request:{}] >> {} {}", requestId, request.getMethod(), request.getRequestURI());

        filterChain.doFilter(requestWrapper, responseWrapper);

        long duration = System.currentTimeMillis() - startTime;

        String requestBody = new String(requestWrapper.getContentAsByteArray(), StandardCharsets.UTF_8);
        if (!requestBody.isEmpty()) {
            log.info("[Request:{}] Request Body: {}", requestId, requestBody);
        }

        log.info("[Request:{}] << {} {} ({}ms)", requestId, response.getStatus(), request.getMethod(), duration);

        String responseBody = new String(responseWrapper.getContentAsByteArray(), StandardCharsets.UTF_8);
        if (!responseBody.isEmpty()) {
            // 응답 본문이 너무 길 경우 로그가 넘치는 것을 방지하기 위해 일부만 로깅
            int maxLength = 1000;
            String loggedBody = responseBody.length() > maxLength ? responseBody.substring(0, maxLength) + "..." : responseBody;
            log.info("[Request:{}] Response Body: {}", requestId, loggedBody);
        }

        responseWrapper.copyBodyToResponse();
    }
}
