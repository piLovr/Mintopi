package com.github.pilovr.mintopi.starter.domain.event;

import com.github.pilovr.mintopi.starter.domain.account.Account;
import com.github.pilovr.mintopi.starter.domain.common.Client;
import com.github.pilovr.mintopi.starter.domain.common.Platform;
import com.github.pilovr.mintopi.starter.domain.message.ReactionMessage;
import com.github.pilovr.mintopi.starter.domain.room.Room;
import lombok.Getter;

@Getter
public class ReactionMessageEvent extends RoomEvent {
    private ReactionMessage reactionMessage;

    public ReactionMessageEvent(Client client, String id, Platform platform, Long timestamp, Account sender, Room room, ReactionMessage message) {
        super(client, id, platform, timestamp, sender, room);
        this.reactionMessage = message;
    }
}
