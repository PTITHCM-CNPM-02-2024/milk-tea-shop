package com.mts.backend.application.account.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json;charset=UTF-8");

        String errorMessage = "Bạn không có quyền thực hiện hành động này";

        var problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatusCode.valueOf(HttpServletResponse.SC_FORBIDDEN),
                errorMessage
        );
        
        problemDetail.setTitle("Forbidden");
        problemDetail.setType(URI.create("about:blank"));
        response.getWriter().write(problemDetail.toString());
    }
} 