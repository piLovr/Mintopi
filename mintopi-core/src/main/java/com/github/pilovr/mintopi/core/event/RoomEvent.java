package com.github.pilovr.mintopi.core.event;

import com.github.pilovr.mintopi.core.common.Platform;
import com.github.pilovr.mintopi.core.account.Account;
import com.github.pilovr.mintopi.core.room.Room;

public abstract class RoomEvent extends Event {
    private Account sender;
    private Room room;

    public RoomEvent(String id, Account sender, Room room) {
        super(id, room.getPlatform());
        this.sender = sender;
        this.room = room;
    }

    public RoomEvent(String id, Account sender, Room room, Platform platform, Object payload) {
        super(id, platform, payload);
        this.sender = sender;
        this.room = room;
    }
}
