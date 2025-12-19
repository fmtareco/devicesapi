package com.example.devicesapi.security;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ApiKeyAuthenticationProvider implements AuthenticationProvider {

    private final ApiKeyProperties props;
    private final PasswordEncoder encoder = new BCryptPasswordEncoder();

    public ApiKeyAuthenticationProvider(ApiKeyProperties props) {
        this.props = props;
    }

//
//    @Override
//    public Authentication authenticate(Authentication authentication)
//            throws AuthenticationException {
//        ApiKeyAuthenticationToken token =
//                (ApiKeyAuthenticationToken) authentication;
//        String apiKey = token.getKey();
//        String apiSecret = token.getSecret();
//        if (!isValid(apiKey, apiSecret)) {
//            throw new BadCredentialsException("Invalid API key");
//        }
//        return new ApiKeyAuthenticationToken(
//                apiKey,
//                apiSecret,
//                List.of(new SimpleGrantedAuthority("ROLE_API"))
//        );
//    }

    @Override
    public Authentication authenticate(Authentication auth) {
        var token = (ApiKeyAuthenticationToken) auth;
        String tokenKey = token.getKey();
        String tokenSecret = token.getSecret();
        var client = props.clients().stream()
                .filter(c -> c.key().equals(tokenKey))
                .findFirst()
                .orElseThrow(() -> new BadCredentialsException("Invalid API key"));
        var clientKey = client.key();
        var clientHash = client.secretHash();
        if (!encoder.matches(tokenSecret, clientHash)) {
            throw new BadCredentialsException("Invalid API secret");
        }
        return new ApiKeyAuthenticationToken(
                tokenKey,
                tokenSecret,
                List.of(new SimpleGrantedAuthority("ROLE_API"))
        );
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return ApiKeyAuthenticationToken.class.isAssignableFrom(authentication);
    }

//    private boolean isValid(String key, String secret) {
//        return "valid-api-key".equals(key) && "valid-api-secret".equals(secret);
//    }
}
