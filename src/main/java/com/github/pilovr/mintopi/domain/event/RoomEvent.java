package com.github.pilovr.mintopi.domain.event;

import com.github.pilovr.mintopi.client.Client;
import com.github.pilovr.mintopi.client.Platform;
import com.github.pilovr.mintopi.domain.room.Room;
import lombok.Getter;

@Getter
public abstract class RoomEvent<R extends Room> extends Event {
    private final R room;

    public RoomEvent(Client client, String id, Platform platform, Long timestamp, R room) {
        super(client, id, platform, timestamp);
        this.room = room;
    }
}
