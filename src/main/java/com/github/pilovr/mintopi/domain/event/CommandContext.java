package com.github.pilovr.mintopi.domain.event;

import com.github.pilovr.mintopi.client.Client;
import com.github.pilovr.mintopi.client.Platform;
import com.github.pilovr.mintopi.domain.account.Account;
import com.github.pilovr.mintopi.domain.payload.Payload;
import com.github.pilovr.mintopi.domain.payload.message.MessageContext;
import com.github.pilovr.mintopi.domain.payload.message.MessagePayload;
import com.github.pilovr.mintopi.domain.payload.message.ReactionMessagePayload;
import com.github.pilovr.mintopi.domain.payload.message.TextMessagePayload;
import com.github.pilovr.mintopi.domain.room.Room;

import java.time.Instant;

public class CommandContext<P extends MessagePayload, R extends Room, A extends Account> extends EventContext<P, R, A> {

    public CommandContext(Client<R, A> client, P payload, String id, R room, A sender, Platform platform, Instant timestamp, boolean fromMe, Object originalObject) {
        super(client, payload, id, room, sender, platform, timestamp, fromMe, originalObject);
    }

    private void react(String emoji) {
        client.sendMessage(room, new ReactionMessagePayload<>(emoji, this));
    }

    private void replyWithQuote(TextMessagePayload<R, A> textMessagePayload) {
        TextMessagePayload<R, A> toSend = textMessagePayload;
        if (textMessagePayload.getContext() == null) {
            toSend = textMessagePayload.toBuilder()
                    .context(MessageContext.<R, A>builder()
                            .quoted(this)
                            .build())
                    .build();
        } else if (textMessagePayload.getContext().getQuoted() == null) {
            toSend = textMessagePayload.toBuilder()
                    .context(textMessagePayload.getContext().toBuilder()
                            .quoted(this)
                            .build())
                    .build();
        }
        client.sendMessage(room, toSend);
    }
}