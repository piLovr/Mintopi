package com.github.pilovr.mintopi.subscriber;

import com.github.pilovr.mintopi.subscriber.command.CommandResponse;

import java.util.EnumMap;
import java.util.Map;

public class Response {
    public static enum CommandResponseType {
        SHORT,
        LONG;
    }

    Map<CommandResponse.CommandResponseType, String> responses;

    public Response(String shortVariant, String longVariant){
        this.responses = new EnumMap<CommandResponse.CommandResponseType, String>(CommandResponse.CommandResponseType.class);
        this.responses.put(CommandResponse.CommandResponseType.SHORT, shortVariant);
        this.responses.put(CommandResponse.CommandResponseType.LONG, longVariant);
    }

    public String get(CommandResponse.CommandResponseType type){
        return this.responses.get(type);
    }
}
