package com.mts.backend.application.account.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        
        String errorMessage = "Bạn cần đăng nhập để thực hiện hành động này";
        
        var problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatusCode.valueOf(HttpServletResponse.SC_UNAUTHORIZED),
                errorMessage
        );
        
        problemDetail.setTitle("Unauthorized");
        problemDetail.setType(URI.create("about:blank"));
        
        response.getWriter().write(problemDetail.toString());
    }
} 