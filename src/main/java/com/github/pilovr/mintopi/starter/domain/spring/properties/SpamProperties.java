package com.github.pilovr.mintopi.starter.domain.spring.properties;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@ConfigurationProperties(prefix = "mintopi.spam-handler")
public class SpamProperties {
    private final boolean enabled = false;
    private final long spamTimeout = 3141; // 5 minutes in milliseconds
}

