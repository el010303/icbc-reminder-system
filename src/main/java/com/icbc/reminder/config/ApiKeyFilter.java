package com.icbc.reminder.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@Component
public class ApiKeyFilter extends OncePerRequestFilter {
    
    @Value("${api.key}")
    private String apiKey;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
    throws ServletException, IOException{
        String path = request.getRequestURI();

        if (path.startsWith("/swagger-ui") ||
            path.startsWith("/v3/api-docs") ||
            path.equals("/") ||
            path.equals("/index.html") ||
            path.equals("/favicon.ico")) {
            filterChain.doFilter(request, response);
            return;
        }

        String requestApiKey = request.getHeader("X-API-KEY");

        // Constant-time comparison: String.equals returns early on the first mismatched
        // character, which leaks how much of a guessed key is correct (timing attack).
        if (requestApiKey != null && MessageDigest.isEqual(
                apiKey.getBytes(StandardCharsets.UTF_8),
                requestApiKey.getBytes(StandardCharsets.UTF_8))) {
            filterChain.doFilter(request, response);
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Invalid or missing API key\"}");
        }
    }
}

