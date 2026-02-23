package com.capgemini.chat_history.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Slf4j
public class ApiKeyFilter extends OncePerRequestFilter {

    @Value("${app.security.api-key}")
    private String configuredApiKey;

    private static final String AUTH_HEADER = "X-API-KEY";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String requestPath = request.getServletPath();

        // 1. Skip Authentication for Swagger & OpenAPI docs
        if (isPublicEndpoint(requestPath)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 2. Extract Key from Header
        String requestApiKey = request.getHeader(AUTH_HEADER);

        // 3. Validate the Key
        if (configuredApiKey.equals(requestApiKey)) {
            log.debug("API Key authenticated successfully for path: {}", requestPath);
            filterChain.doFilter(request, response);
        } else {
            log.warn("Unauthorized access attempt on path: {} with key: {}", requestPath, requestApiKey);

            // 4. Send Custom Error Response
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Invalid or missing API Key in " + AUTH_HEADER + " header\"}");
        }
    }

    private boolean isPublicEndpoint(String path) {
        return path.contains("/v3/api-docs") ||
                path.contains("/swagger-ui") ||
                path.contains("/favicon.ico");
    }
}