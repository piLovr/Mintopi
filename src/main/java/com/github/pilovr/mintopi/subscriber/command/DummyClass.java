package com.github.pilovr.mintopi.subscriber.command;

import com.github.pilovr.mintopi.domain.account.Account;
import com.github.pilovr.mintopi.domain.event.CommandContext;
import com.github.pilovr.mintopi.domain.event.EventContext;
import com.github.pilovr.mintopi.domain.payload.message.TextMessagePayload;
import com.github.pilovr.mintopi.domain.room.Room;
import com.github.pilovr.mintopi.subscriber.CommandSubscriber;

@Command(name = "credits")
public class DummyClass implements CommandSubscriber<Room, Account> {
    @Override
    public void onEvent(CommandContext<Room, Account, TextMessagePayload> context) {

    }
}
