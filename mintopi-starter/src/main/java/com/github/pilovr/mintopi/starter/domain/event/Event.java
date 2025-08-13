package com.github.pilovr.mintopi.starter.domain.event;

import com.github.pilovr.mintopi.starter.domain.common.Platform;
import lombok.Getter;

import java.sql.Timestamp;

@Getter
public abstract class Event {
    private String id;
    private Platform platform;
    private Object payload;
    private Timestamp timestamp; //TODO
    public Event(String id, Platform platform, Object payload) {
        this.id = id;
        this.platform = platform;
        this.payload = payload;
    }

    public Event(String id, Platform platform) {
        // Default constructor
    }
}
