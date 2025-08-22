package com.github.pilovr.mintopi.domain.event;

import com.github.pilovr.mintopi.domain.account.Account;
import com.github.pilovr.mintopi.client.Client;
import com.github.pilovr.mintopi.client.Platform;
import com.github.pilovr.mintopi.domain.room.Room;
import lombok.Getter;

@Getter
public class StubEvent extends RoomEvent{
    private final StubType stubType;

    public StubEvent(Client client, String id, Platform platform, Long timestamp, Room room, StubType stubType) {
        super(client, id, platform, timestamp, room);
        this.stubType = stubType;
    }
}
