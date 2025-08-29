package com.github.pilovr.mintopi.subscriber;

import com.github.pilovr.mintopi.domain.account.Account;
import com.github.pilovr.mintopi.domain.event.EventContext;
import com.github.pilovr.mintopi.domain.payload.Payload;
import com.github.pilovr.mintopi.domain.room.Room;

public interface EventSubscriber<R extends Room, A extends Account> {
    void onEvent(EventContext<Payload,R, A> context);
}
