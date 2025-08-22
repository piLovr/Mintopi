package com.github.pilovr.mintopi.domain.common;


import com.github.pilovr.mintopi.store.Store;
import com.github.pilovr.mintopi.domain.message.Message;
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
