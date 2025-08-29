package com.github.pilovr.mintopi.domain.payload.message;

import com.github.pilovr.mintopi.domain.account.Account;
import com.github.pilovr.mintopi.domain.event.CommandContext;
import com.github.pilovr.mintopi.domain.event.EventContext;
import com.github.pilovr.mintopi.domain.payload.Payload;
import com.github.pilovr.mintopi.domain.room.Room;
import lombok.Builder;
import lombok.Getter;

@Getter
public non-sealed class ReactionMessagePayload<R extends Room, A extends Account> extends MessagePayload {
    private final String reaction;
    private final EventContext<? extends Payload,R,A> quoted;

    public ReactionMessagePayload(String reaction, EventContext<? extends Payload,R,A> quoted) {
        this.reaction = reaction;
        this.quoted = quoted;
    }
}
