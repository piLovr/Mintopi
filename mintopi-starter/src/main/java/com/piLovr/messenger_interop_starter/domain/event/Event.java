package com.piLovr.messenger_interop_starter.domain.event;

import com.piLovr.messenger_interop_starter.domain.common.Platform;
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
