package com.github.pilovr.mintopi.domain.event;

import com.github.pilovr.mintopi.domain.account.Account;
import com.github.pilovr.mintopi.domain.payload.message.Context;
import com.github.pilovr.mintopi.domain.payload.message.MessagePayload;
import com.github.pilovr.mintopi.domain.payload.message.ReactionMessagePayload;
import com.github.pilovr.mintopi.domain.payload.message.TextMessagePayload;
import com.github.pilovr.mintopi.domain.room.Room;

public class CommandContext<R extends Room, A extends Account, P extends MessagePayload> extends EventContext<R, A, P> {

    public void react(String emoji){
        client.sendMessage(room, ReactionMessagePayload.builder().message(payload).reaction(emoji).build());
    }

    public void replyWithQuote(TextMessagePayload textMessagePayload){
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
