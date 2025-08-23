package com.github.pilovr.mintopi.client;


import com.github.pilovr.mintopi.client.store.Store;
import com.github.pilovr.mintopi.domain.Listener;
import com.github.pilovr.mintopi.domain.MessageRunnable;
import com.github.pilovr.mintopi.domain.event.ExtendedMessageEvent;
import com.github.pilovr.mintopi.domain.message.Message;
import com.github.pilovr.mintopi.domain.message.attachment.AttachmentType;
import com.github.pilovr.mintopi.domain.message.builder.ExtendedMessageBuilder;
import com.github.pilovr.mintopi.domain.room.Room;

import java.util.concurrent.CompletableFuture;

public interface Client {
    void setConnected(boolean value);
    void connect();
    void disconnect();
    Message sendMessage(Room room, String text);
    Message sendMessage(Room room, Message message);

    void addListener(Listener listener);
    void removeListener(Listener listener);

    String getAlias();
    boolean isConnected();

    byte[] downloadMedia(Object payload);
    Store getStore();

    CompletableFuture<Message> sendMediaConversionUsingQueue(ExtendedMessageEvent origin, AttachmentType target, int attachmentIndex, int timeout, boolean quote);

    void setTimeoutRunnable(MessageRunnable runnable);
}
