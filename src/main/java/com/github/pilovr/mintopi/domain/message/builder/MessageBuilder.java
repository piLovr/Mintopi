package com.github.pilovr.mintopi.domain.message.builder;

import com.github.pilovr.mintopi.domain.message.Message;
import com.github.pilovr.mintopi.domain.message.MessageType;

public interface MessageBuilder {
    Message build();
    Object getPayload();
    String getId();
    MessageType getMessageType();
}
