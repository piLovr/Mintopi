package com.github.pilovr.mintopi.starter.domain.message;

import com.github.pilovr.mintopi.starter.domain.account.Account;
import lombok.Getter;

@Getter
public abstract sealed class Message permits ExtendedMessage, ReactionMessage, SpecialMessage {
    protected String id;
    protected MessageType type;
    protected Object payload;
}
