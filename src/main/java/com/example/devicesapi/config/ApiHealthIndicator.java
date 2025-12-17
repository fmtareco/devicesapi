package com.example.devicesapi.config;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class ApiHealthIndicator implements HealthIndicator {
    @Override
    public Health health() {
        boolean isServiceUp = checkComponents();
        if (isServiceUp) {
            return Health.up()
                    .withDetail("message", "My fabulous device API is alive and kicking!")
                    .withDetail("Devices Checked", "...a bunch !!")
                    .build();
        }
        return Health.down()
                .withDetail("error", "Something is rotten!")
                .withDetail("Devices Checked", "not many")
                .withDetail("Sugestion", "do something else")
                .build();
    }

    private boolean checkComponents() {
        return Math.random() < 0.8;
    }
}
