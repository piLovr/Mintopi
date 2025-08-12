package com.piLovr.messenger_interop_starter.domain.event;

import com.piLovr.messenger_interop_starter.domain.account.Account;
import com.piLovr.messenger_interop_starter.domain.common.Platform;
import com.piLovr.messenger_interop_starter.domain.message.Message;
import com.piLovr.messenger_interop_starter.domain.room.Room;

public class MessageEvent extends RoomEvent {
    private Message message;

    public MessageEvent(String id, Account sender, Room room, Platform platform, Object payload, Message message) {
        super(id, sender, room, platform, payload);

    }
}
