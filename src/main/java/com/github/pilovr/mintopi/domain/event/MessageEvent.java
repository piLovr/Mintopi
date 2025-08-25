package com.github.pilovr.mintopi.domain.event;

import com.github.pilovr.mintopi.client.Client;
import com.github.pilovr.mintopi.client.Platform;
import com.github.pilovr.mintopi.domain.account.Account;
import com.github.pilovr.mintopi.domain.message.CommandMessageProperties;
import com.github.pilovr.mintopi.domain.message.Message;
import com.github.pilovr.mintopi.domain.room.Room;
import lombok.Getter;

@Getter
public class MessageEvent<M extends Message, R extends Room, A extends Account> extends RoomEvent<R> {
    private final M message;
    private final A sender;

    public MessageEvent(Client<R,A> client, String id, Platform platform, Long timestamp, A sender, R room, M message) {
        super(client, id, platform, timestamp, room);
        this.message = message;
        this.sender = sender;
    }
}
