package com.github.pilovr.mintopi.starter.domain.common;


import com.github.pilovr.mintopi.starter.store.Store;
import com.github.pilovr.mintopi.starter.domain.message.Message;
import com.github.pilovr.mintopi.starter.domain.room.Room;

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
