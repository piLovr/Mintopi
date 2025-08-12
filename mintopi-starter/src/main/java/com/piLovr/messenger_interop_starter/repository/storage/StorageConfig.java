package com.piLovr.messenger_interop_starter.repository.storage;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
@ConfigurationProperties(prefix = "messenger.storage")
public class StorageConfig {
    private Boolean messageLoggingInDB;
    private Boolean roomEventLoggingInDB;
    private Boolean roomDataCaching;
    private Long cacheClearAfterMS;

}