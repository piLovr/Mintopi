package com.github.pilovr.mintopi.starter.domain.event;

import com.github.pilovr.mintopi.starter.domain.account.Account;
import com.github.pilovr.mintopi.starter.domain.common.Platform;
import com.github.pilovr.mintopi.starter.domain.message.ExtendedMessage;
import com.github.pilovr.mintopi.starter.domain.message.Message;
import com.github.pilovr.mintopi.starter.domain.room.Room;
import lombok.Getter;

@Getter
public class ExtendedMessageEvent extends RoomEvent {
    private final ExtendedMessage message;

    public ExtendedMessageEvent(String id, Account sender, Room room, Platform platform, Object payload, ExtendedMessage message) {
        super(id, sender, room, platform, payload);
        this.message = message;
    }
}
