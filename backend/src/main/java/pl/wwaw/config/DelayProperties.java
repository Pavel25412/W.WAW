package pl.wwaw.config;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "wwaw.delays")
public record DelayProperties(int retentionDays) {

    public DelayProperties {
        if (retentionDays < 1) {
            throw new IllegalArgumentException("wwaw.delays.retention-days must be >= 1");
        }
    }

    public Duration retention() {
        return Duration.ofDays(retentionDays);
    }
}
