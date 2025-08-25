package com.github.pilovr.mintopi.domain.event;

import com.github.pilovr.mintopi.domain.account.Account;
import com.github.pilovr.mintopi.client.Client;
import com.github.pilovr.mintopi.client.Platform;
import com.github.pilovr.mintopi.domain.message.ReactionMessage;
import com.github.pilovr.mintopi.domain.room.Room;
import lombok.Getter;

@Getter
public class ReactionMessageEvent<R extends Room, A extends Account> extends MessageEvent<ReactionMessage, R, A> {

    public ReactionMessageEvent(Client client, String id, Platform platform, Long timestamp, A sender, R room, ReactionMessage message) {
        super(client, id, platform, timestamp, sender, room, message);
    }

    public ReactionMessageEvent(MessageEvent<ReactionMessage, R, A> messageEvent){
        super(messageEvent.getClient(), messageEvent.getId(), messageEvent.getPlatform(), messageEvent.getTimestamp(), messageEvent.getSender(), messageEvent.getRoom(), messageEvent.getMessage());
    }
}
