package com.github.pilovr.mintopi.starter.domain.event;

import com.github.pilovr.mintopi.starter.domain.account.Account;
import com.github.pilovr.mintopi.starter.domain.common.Platform;
import com.github.pilovr.mintopi.starter.domain.message.Message;
import com.github.pilovr.mintopi.starter.domain.room.Room;
import lombok.Getter;

@Getter
public class MessageEvent extends RoomEvent {
    private final Message message;

    public MessageEvent(String id, Account sender, Room room, Platform platform, Object payload, Message message) {
        super(id, sender, room, platform, payload);
        this.message = message;
    }
}
