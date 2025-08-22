package com.github.pilovr.mintopi.starter.domain.event;

import com.github.pilovr.mintopi.starter.domain.account.Account;
import com.github.pilovr.mintopi.starter.domain.common.Client;
import com.github.pilovr.mintopi.starter.domain.common.Platform;
import com.github.pilovr.mintopi.starter.domain.room.Room;
import lombok.Getter;

@Getter
public class StubEvent extends RoomEvent{
    private final StubType stubType;

    public StubEvent(Client client, String id, Platform platform, Long timestamp, Account sender, Room room, StubType stubType) {
        super(client, id, platform, timestamp, sender, room);
        this.stubType = stubType;
    }
}
