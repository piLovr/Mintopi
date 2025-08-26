package com.github.pilovr.mintopi.subscriber.command;

import com.github.pilovr.mintopi.domain.event.CommandContext;
import com.github.pilovr.mintopi.domain.payload.message.TextMessagePayload;
import com.github.pilovr.mintopi.subscriber.CommandScope;
import com.github.pilovr.mintopi.tools.JsonTextProvider;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor
public class CommandReultBuilder {
    private final String commandOrSubscriberName;
    private final String preKey;
    private final String language;
    private final ResponseType responseType;
    private Map<String,String> placeholders = new HashMap<>();

    private Set<TextMessagePayload> toSend;



    public static enum ResponseType {
        REACTION("emoji", false),
        SHORT("short", false),
        LONG("long", false);

        @Getter
        private final String jsonAlias;
        @Getter
        private final boolean quoted;

        public ResponseType castToOneLiner() {
            return this == REACTION ? SHORT : this;
        }

        ResponseType(String jsonAlias, boolean quoted) {
            this.jsonAlias = jsonAlias;
            this.quoted = quoted;
        }
    }

    public String getReaction(String key){
        return JsonTextProvider.getText(language, preKey, commandOrSubscriberName, key, "emoji");
    }

    public String getText(String key){
        String text = JsonTextProvider.getText(language, "commands", commandOrSubscriberName, "response", key, responseType.getJsonAlias());
        if(text == null) return null;

        if(responseType == ResponseType.REACTION){
            return new ReactionMessageBuilder().setReaction(text).setMessage(commandContext.getEvent().getMessage());
        }

        for(Map.Entry<String,String> entry : placeholders.entrySet()){
            text = text.replace("<" + entry.getKey() + ">", entry.getValue());
        }
    }

    public String replaceAdvancedPlacehodlers(CommandContext commandContext){
        if(text == null) return null;
        return text.replace("<user>", commandContext.getEvent().getSender().getPushName());
        //todo...
    }




}
