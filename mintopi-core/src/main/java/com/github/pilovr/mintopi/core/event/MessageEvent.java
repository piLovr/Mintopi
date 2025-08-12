package com.github.pilovr.mintopi.core.event;

import com.github.pilovr.mintopi.core.common.Platform;
import com.github.pilovr.mintopi.core.account.Account;
import com.github.pilovr.mintopi.core.message.Message;
import com.github.pilovr.mintopi.core.room.Room;

public class MessageEvent extends RoomEvent {
    private Message message;

    public MessageEvent(String id, Account sender, Room room, Platform platform, Object payload, Message message) {
        super(id, sender, room, platform, payload);

    }
}
