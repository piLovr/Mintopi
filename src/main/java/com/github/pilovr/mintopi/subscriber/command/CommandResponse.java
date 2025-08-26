package com.github.pilovr.mintopi.subscriber.command;

import java.util.EnumMap;
import java.util.Map;

public class CommandResponse {
    public static enum CommandResponseType {
        REACTION,
        SHORT,
        LONG;
    }

    Map<CommandResponseType, String> responses;

    public CommandResponse(String reaction, String shortVariant, String longVariant){
        this.responses = new EnumMap<CommandResponseType, String>(CommandResponseType.class);
        this.responses.put(CommandResponseType.REACTION, reaction);
        this.responses.put(CommandResponseType.SHORT, shortVariant);
        this.responses.put(CommandResponseType.LONG, longVariant);
    }

    public String get(CommandResponseType type){
        return this.responses.get(type);
    }
}

