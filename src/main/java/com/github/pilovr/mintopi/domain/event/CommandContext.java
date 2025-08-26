package com.github.pilovr.mintopi.domain.event;

import com.github.pilovr.mintopi.client.Client;
import com.github.pilovr.mintopi.subscriber.command.CommandReultBuilder;
import com.github.pilovr.mintopi.domain.account.Account;
import com.github.pilovr.mintopi.domain.payload.message.Context;
import com.github.pilovr.mintopi.domain.payload.message.MessagePayload;
import com.github.pilovr.mintopi.domain.payload.message.ReactionMessagePayload;
import com.github.pilovr.mintopi.domain.payload.message.TextMessagePayload;
import com.github.pilovr.mintopi.domain.room.Room;

import java.time.Instant;

public class CommandContext<R extends Room, A extends Account, P extends MessagePayload> extends EventContext<R, A, P> {

    public CommandContext(P payload, String eventId, Instant timestamp, R room, A sender, Client<R, A> client, CommandReultBuilder textBuilder) {
        super(payload, eventId, timestamp, room, sender, client, textBuilder);
    }

    private void react(String emoji){
        client.sendMessage(room, ReactionMessagePayload.builder().message(payload).reaction(emoji).build());
    }

    private void replyWithQuote(TextMessagePayload textMessagePayload){
        TextMessagePayload toSend = textMessagePayload;
        if(textMessagePayload.getContext() == null){
            toSend = textMessagePayload.toBuilder().context(Context.builder().quoted(payload).build()).build();
        }else if(textMessagePayload.getContext().getQuoted() == null){
            toSend = textMessagePayload.toBuilder().context(textMessagePayload.getContext().toBuilder().quoted(payload).build()).build();
        }
        client.sendMessage(room, toSend);
    }

    @Override
    protected String getJsonKey(){
        return "command";
    }
}
