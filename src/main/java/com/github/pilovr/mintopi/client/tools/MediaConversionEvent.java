package com.github.pilovr.mintopi.client.tools;

import lombok.Getter;

public record MediaConversionEvent(EventType eventType, int pos, byte[] result) {
    public enum EventType {
        POS_UPDATED,
        FIRST_POS,
        CONVERSION_STARTED,
        CONVERSION_FAILED,
        CONVERSION_SUCCEEDED,
    }
}
