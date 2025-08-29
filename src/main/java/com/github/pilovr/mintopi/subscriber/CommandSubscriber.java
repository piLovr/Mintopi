package com.github.pilovr.mintopi.subscriber;

import com.github.pilovr.mintopi.domain.account.Account;
import com.github.pilovr.mintopi.domain.event.CommandContext;
import com.github.pilovr.mintopi.domain.event.EventContext;
import com.github.pilovr.mintopi.domain.payload.message.TextMessagePayload;
import com.github.pilovr.mintopi.domain.room.Room;

public interface CommandSubscriber<R extends Room, A extends Account> {
    void onEvent(CommandContext<TextMessagePayload<R,A>,R, A> context);
}
