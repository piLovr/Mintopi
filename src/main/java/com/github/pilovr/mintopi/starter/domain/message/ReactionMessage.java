package com.github.pilovr.mintopi.starter.domain.message;

import lombok.Getter;

@Getter
public non-sealed class ReactionMessage extends Message {
    private String reaction;
    private Message message;


    public ReactionMessage(String id, Object payload, String reaction, Message message) {
        this.type = MessageType.REACTION;
        this.id = id;
        this.payload = payload;

        this.reaction = reaction;
        this.message = message;
    }
}
