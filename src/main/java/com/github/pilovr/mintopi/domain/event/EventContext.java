package com.github.pilovr.mintopi.domain.event;

import com.github.pilovr.mintopi.client.Client;
import com.github.pilovr.mintopi.domain.account.Account;
import com.github.pilovr.mintopi.domain.room.Room;
import lombok.AllArgsConstructor;

import java.time.Instant;

@AllArgsConstructor
public class EventContext<R extends Room, A extends Account> {
    private final String eventId;
    private final Instant timestamp;
    private final R room;     // generic, vom User erweiterbar
    private final A account;  // generic
    private final Client client;
    private final String textCollection;

    //private final CommandResultBuilder resultBuilder;
}
