package com.github.pilovr.mintopi.domain.event;

import com.github.pilovr.mintopi.client.Client;
import com.github.pilovr.mintopi.tools.JsonTextProvider;
import com.github.pilovr.mintopi.subscriber.command.CommandReultBuilder;
import com.github.pilovr.mintopi.domain.account.Account;
import com.github.pilovr.mintopi.domain.payload.Payload;
import com.github.pilovr.mintopi.domain.payload.message.MessagePayload;
import com.github.pilovr.mintopi.domain.room.Room;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.Set;

@AllArgsConstructor
@RequiredArgsConstructor
public class EventContext<R extends Room, A extends Account, P extends Payload> {
    protected final P payload;

    protected final String eventId;
    protected final Instant timestamp;
    protected final R room;     // generic, vom User erweiterbar
    protected final A sender;  // generic
    protected final Client<R,A> client;

    private final boolean collectSends = false;
    private Set<String> messagesToSend;

    protected final CommandReultBuilder textBuilder;

    public void sendMessage(MessagePayload messagePayload){
        client.sendMessage(room, messagePayload);
    }

    public void sendTextFromCollection(String key){
        String text = JsonTextProvider.getText(textCollectionName, getJsonKey(), key);
    }

    protected String getJsonKey(){
        return "subscriber";
    }
}
