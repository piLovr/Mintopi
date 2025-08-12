package com.piLovr.messenger_interop_starter.domain.event;

import com.piLovr.messenger_interop_starter.domain.account.Account;
import com.piLovr.messenger_interop_starter.domain.common.Platform;
import com.piLovr.messenger_interop_starter.domain.room.Room;

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
