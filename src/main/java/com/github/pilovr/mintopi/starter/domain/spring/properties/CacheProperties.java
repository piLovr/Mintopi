package com.github.pilovr.mintopi.starter.domain.spring.properties;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@ConfigurationProperties(prefix = "mintopi.cache")
public class CacheProperties {
    private final boolean enabled = false;
    private final long ttl = 60 * 60 * 1000; // 1 hour in milliseconds
    private final int maxSize = 1000; // Maximum number of entries in the cache
}
