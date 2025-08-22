package com.github.pilovr.mintopi.domain.message;

import lombok.Getter;

@Getter
public abstract sealed class Message permits ExtendedMessage, ReactionMessage, SpecialMessage {
    protected String id;
    protected MessageType type;
    protected Object payload;
}
