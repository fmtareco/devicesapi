package com.example.devicesapi.config;

import com.example.devicesapi.security.ApiKeyAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    AuthenticationManager authenticationManager(
            AuthenticationConfiguration config
    ) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    @Order(1)
    SecurityFilterChain apiKeyChain(
            HttpSecurity http,
            AuthenticationManager authenticationManager
    ) throws Exception {

        return http
                .securityMatcher("/api/devices/**")
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(
                        new ApiKeyAuthFilter(authenticationManager),
                        UsernamePasswordAuthenticationFilter.class
                )
                .authorizeHttpRequests(auth ->
                        auth.anyRequest().authenticated()
                )
                .exceptionHandling(ex ->
                        ex.authenticationEntryPoint(
                                (req, res, e) -> res.sendError(402, "Auth Exception")
                        )
                )
                .build();
    }
    @Bean
    @Order(2)
    SecurityFilterChain basicAuthChain(HttpSecurity http) throws Exception {
        return http
                .securityMatcher("/**")
                .httpBasic(Customizer.withDefaults())
                .authorizeHttpRequests(auth ->
                        auth.anyRequest().authenticated()
                )
                .build();
    }
}

