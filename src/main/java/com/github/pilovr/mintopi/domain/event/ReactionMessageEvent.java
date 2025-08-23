package com.github.pilovr.mintopi.domain.event;

import com.github.pilovr.mintopi.domain.account.Account;
import com.github.pilovr.mintopi.client.Client;
import com.github.pilovr.mintopi.client.Platform;
import com.github.pilovr.mintopi.domain.message.ExtendedMessage;
import com.github.pilovr.mintopi.domain.message.ReactionMessage;
import com.github.pilovr.mintopi.domain.room.Room;
import lombok.Getter;

@Getter
public class ReactionMessageEvent extends RoomEvent {
    private final ReactionMessage reactionMessage;
    private final Account sender;

    public ReactionMessageEvent(Client client, String id, Platform platform, Long timestamp, Account sender, Room room, ReactionMessage message) {
        super(client, id, platform, timestamp, room);
        this.sender = sender;
        this.reactionMessage = message;
    }

    public ReactionMessageEvent(MessageEvent messageEvent){
        super(messageEvent.getClient(), messageEvent.getId(), messageEvent.getPlatform(), messageEvent.getTimestamp(), messageEvent.getRoom());
        if(!(messageEvent.getMessage() instanceof ReactionMessage)) {
            throw new IllegalArgumentException("Cannot convert ExtendedMessageEvent to ExtendedMessageEvent");
        }
        this.reactionMessage = (ReactionMessage) (messageEvent.getMessage());
        this.sender = messageEvent.getSender();
    }
}
