package com.github.pilovr.mintopi.core.common;


import com.github.pilovr.mintopi.core.message.ExtendedMessage;
import com.github.pilovr.mintopi.core.room.Room;

public interface Client {
    void connect();
    void disconnect();
    ExtendedMessage sendMessage(Room room, String text);
    ExtendedMessage sendMessage(Room room, ExtendedMessage extendedMessage);

    void addListener(Listener listener);
    void removeListener(Listener listener);

    String getAlias();
    boolean isConnected();
}
