package com.example.devicesapi.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "spring.security.api-keys")
public record ApiKeyProperties(List<Client> clients) {

    public record Client(String key, String secretHash) {}
}
