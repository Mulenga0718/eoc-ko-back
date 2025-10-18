package com.ibs.user.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        // 필요한 권한 없이 보호된 리소스에 액세스하려고 할 때 호출됩니다.
        // 여기서는 403 Forbidden 응답을 보냅니다.
        response.sendError(HttpServletResponse.SC_FORBIDDEN, "Forbidden");
    }
}
