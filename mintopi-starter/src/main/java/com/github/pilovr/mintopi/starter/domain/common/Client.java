package com.github.pilovr.mintopi.starter.domain.common;


import com.github.pilovr.mintopi.starter.domain.message.ExtendedMessage;
import com.github.pilovr.mintopi.starter.domain.room.Room;

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
