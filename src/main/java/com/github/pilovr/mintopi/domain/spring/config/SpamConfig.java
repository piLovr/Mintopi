package com.github.pilovr.mintopi.domain.spring.config;

import com.github.pilovr.mintopi.domain.spring.properties.SpamProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(SpamProperties.class)
public class SpamConfig {}
