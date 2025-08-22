package com.github.pilovr.mintopi.client;


import com.github.pilovr.mintopi.client.store.Store;
import com.github.pilovr.mintopi.domain.Listener;
import com.github.pilovr.mintopi.domain.event.ExtendedMessageEvent;
import com.github.pilovr.mintopi.domain.message.Message;
import com.github.pilovr.mintopi.domain.message.attachment.AttachmentType;
import com.github.pilovr.mintopi.domain.message.builder.ExtendedMessageBuilder;
import com.github.pilovr.mintopi.domain.room.Room;

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
}
