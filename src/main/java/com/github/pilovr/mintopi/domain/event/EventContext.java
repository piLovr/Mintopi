package com.github.pilovr.mintopi.domain.event;

import com.github.pilovr.mintopi.client.Client;
import com.github.pilovr.mintopi.client.tools.I18nProvider;
import com.github.pilovr.mintopi.domain.account.Account;
import com.github.pilovr.mintopi.domain.payload.Payload;
import com.github.pilovr.mintopi.domain.payload.message.MessagePayload;
import com.github.pilovr.mintopi.domain.room.Room;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.HashSet;
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
    private Set<MessagePayload> messagesToSend;

    protected final String textBuilder;

    public void sendMessage(MessagePayload messagePayload){
        client.sendMessage(room, messagePayload);
    }

    public void sendTextFromCollection(String key){
        String text = I18nProvider.getText(textCollectionName, getJsonKey(), key);
    }

    protected String getJsonKey(){
        return "subscriber";
    }

    public ExtendedMessageBuilder generateMessageBuilderWithMentions(String text){
        //extract all strings like @1234567890 -> 1234567890 without replacing them
        //find accounts with these numbers and add them to mentions
        Set<Account> mentions = new HashSet<>();
        for(String word : text.split(" ")){
            if(word.startsWith("@") && word.length() > 1){
                String number = word.substring(1).replaceAll("[^0-9]", "");
                Account account = commandContext.getEvent().getClient().getStore().getOrCreateAccount(number, commandContext.getEvent().getPlatform(), null);
                if(account != null){
                    mentions.add(account);
                }
            }
        }
        return new ExtendedMessageBuilder()
                .text(text)
                .mentions(mentions.isEmpty() ? null : mentions.stream().toList())
                .quoted(responseType.isQuoted() ? commandContext.getEvent().getMessage() : null);
    }
}
