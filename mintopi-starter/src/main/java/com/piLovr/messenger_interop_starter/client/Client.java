package com.piLovr.messenger_interop_starter.client;


import com.piLovr.messenger_interop_starter.domain.message.ExtendedMessage;
import com.piLovr.messenger_interop_starter.listener.Listener;
import com.piLovr.messenger_interop_starter.domain.room.Room;

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
