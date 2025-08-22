package com.github.pilovr.mintopi.domain.event;

import com.github.pilovr.mintopi.domain.account.Account;
import com.github.pilovr.mintopi.domain.common.Client;
import com.github.pilovr.mintopi.domain.common.Platform;
import com.github.pilovr.mintopi.domain.message.ReactionMessage;
import com.github.pilovr.mintopi.domain.room.Room;
import lombok.Getter;

@Getter
public class ReactionMessageEvent extends RoomEvent {
    private ReactionMessage reactionMessage;

    public ReactionMessageEvent(Client client, String id, Platform platform, Long timestamp, Account sender, Room room, ReactionMessage message) {
        super(client, id, platform, timestamp, sender, room);
        this.reactionMessage = message;
    }
}
