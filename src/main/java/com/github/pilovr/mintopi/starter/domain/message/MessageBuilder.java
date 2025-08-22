package com.github.pilovr.mintopi.starter.domain.message;

public interface MessageBuilder {
    Message build();
    Object getPayload();
    String getId();
    MessageType getMessageType();
}
