package com.github.pilovr.mintopi.starter.domain.event;

import com.github.pilovr.mintopi.starter.domain.common.Client;
import com.github.pilovr.mintopi.starter.domain.common.Platform;
import lombok.Getter;

@Getter
public abstract class Event {
    private final Client client;
    private final String id;
    private final Platform platform;
    private final Long timestamp;

    public Event(Client client, String id, Platform platform, Long timestamp) {
        this.client = client;
        this.id = id;
        this.platform = platform;
        this.timestamp = timestamp;
    }
}
