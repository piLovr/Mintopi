package com.piLovr.messenger_interop_starter.repository.storage;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "messenger.storage")
public class StorageConfig {
    private StorageType type = StorageType.DISCARD;
    private Boolean storeRooms = false;
    private Boolean storeParticipants = false;
    private Boolean storeAccounts = false;
    private Long cacheExpiryMinutes = 0; //0 means no expiry

    // Getters and setters
    public void loadDefaults() {
        // Set default values if not already set
        switch (type) {
            case DISCARD:
                if(storeAccounts || storeParticipants || storeRooms) {
                    throw new IllegalStateException("Cannot cache data when storage type is DISCARD");
                }
                break;
            case DATABASE:
                if(storeAccounts == null) storeAccounts = false;
                if(storeParticipants == null) storeParticipants = false;
                if(storeRooms == null) storeRooms = true;

                break;
            case HYBRID:
                cacheRooms = true; // Cache in memory
                cacheParticipants = true;
                cacheAccounts = true;
                cacheExpiryMinutes = 60; // Default expiry of 60 minutes
                break;
        }
    }
    // Other getters/setters...
}