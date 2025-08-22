package com.github.pilovr.mintopi.starter.domain.message;

import lombok.Getter;

@Getter
public class ReactionMessageBuilder implements MessageBuilder {
    private String id;
    private Object payload;

    private String reaction;
    private Message message;

    public ReactionMessageBuilder(String id, Object payload){
        this.id = id;
        this.payload = payload;
    }

    public ReactionMessageBuilder setReaction(String reaction) {
        this.reaction = reaction;
        return this;
    }

    public ReactionMessageBuilder setMessage(Message message){
        this.message = message;
        return this;
    }

    @Override
    public ReactionMessage build() {
        if (reaction == null || reaction.isEmpty()) {
            throw new IllegalArgumentException("Reaction cannot be null or empty");
        }
        return new ReactionMessage(id, payload, reaction, message);
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.REACTION;
    }
}
