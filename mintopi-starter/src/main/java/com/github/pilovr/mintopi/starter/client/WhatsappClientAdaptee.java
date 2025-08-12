package com.github.pilovr.mintopi.starter.client;

import com.github.pilovr.mintopi.core.common.Client;
import com.github.pilovr.mintopi.core.message.ExtendedMessage;
import com.github.pilovr.mintopi.core.common.Listener;
import com.github.pilovr.mintopi.starter.listener.WhatsappInternalListener;
import com.github.pilovr.mintopi.core.room.Room;
import com.github.pilovr.mintopi.starter.util.QrHandler;
import it.auties.whatsapp.api.Whatsapp;
import it.auties.whatsapp.api.WhatsappWebHistoryPolicy;


import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class WhatsappClientAdaptee implements Client {
    protected final List<Listener> listeners = new CopyOnWriteArrayList<>();
    protected String alias;
    protected Whatsapp client;
    protected WhatsappInternalListener internalListener;
    public WhatsappClientAdaptee(String alias) {
        this.alias = alias;
        internalListener = new WhatsappInternalListener(listeners);
        // Initialize the client, e.g., set up connections, listeners, etc.
    }
    @Override
    public void connect() {
        client = Whatsapp.builder()
                .webClient()
                .newConnection(alias)
                .historySetting(WhatsappWebHistoryPolicy.discard(false))
                .unregistered(QrHandler.toTerminal());

        client.addListener(internalListener)
                .connect();
        new Thread(() -> client.waitForDisconnection()).start();
    }

    @Override
    public void disconnect() {

    }

    @Override
    public ExtendedMessage sendMessage(Room room, String text) {
        return null;
    }

    @Override
    public ExtendedMessage sendMessage(Room room, ExtendedMessage extendedMessage) {
        return null;
    }

    @Override
    public void addListener(Listener listener) {

    }

    @Override
    public void removeListener(Listener listener) {

    }

    @Override
    public String getAlias() {
        return "";
    }

    @Override
    public boolean isConnected() {
        return false;
    }
}
