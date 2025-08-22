package com.github.pilovr.mintopi.domain.spring.config;

import com.github.pilovr.mintopi.domain.spring.properties.CacheProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(CacheProperties.class)
public class CacheConfig {
}
