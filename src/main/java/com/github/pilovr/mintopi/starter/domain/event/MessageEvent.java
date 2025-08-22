package com.github.pilovr.mintopi.starter.domain.event;

import com.github.pilovr.mintopi.starter.domain.account.Account;
import com.github.pilovr.mintopi.starter.domain.common.Client;
import com.github.pilovr.mintopi.starter.domain.common.Platform;
import com.github.pilovr.mintopi.starter.domain.message.Message;
import com.github.pilovr.mintopi.starter.domain.room.Room;
import lombok.Getter;

@Getter
public class MessageEvent extends RoomEvent {
    private final Message message;
    private final Account sender;

    public MessageEvent(Client client, String id, Platform platform, Long timestamp, Account sender, Room room, Message message) {
        super(client, id, platform, timestamp, room);
        this.message = message;
        this.sender = sender;
    }
}
