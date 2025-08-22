package com.github.pilovr.mintopi.domain.message;

public interface MessageBuilder {
    Message build();
    Object getPayload();
    String getId();
    MessageType getMessageType();
}
