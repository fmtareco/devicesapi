package com.example.devicesapi.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class ApiKeyAuthFilter extends OncePerRequestFilter {

    private final AuthenticationManager authenticationManager;

    public ApiKeyAuthFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain
    ) throws IOException, ServletException {

        String apiKey = request.getHeader("API-Key");
        String apiSecret = request.getHeader("API-Secret");

        if (apiKey != null && apiSecret != null &&
                SecurityContextHolder.getContext().getAuthentication() == null) {

            try {
                Authentication auth = authenticationManager.authenticate(
                        new ApiKeyAuthenticationToken(apiKey, apiSecret)
                );

                SecurityContextHolder.getContext().setAuthentication(auth);

            } catch (AuthenticationException ex) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, ex.getMessage());
                return;
            }
        }

        chain.doFilter(request, response);
    }
}
