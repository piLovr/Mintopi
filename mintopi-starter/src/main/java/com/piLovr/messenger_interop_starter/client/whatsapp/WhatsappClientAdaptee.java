package com.piLovr.messenger_interop_starter.client.whatsapp;

import com.piLovr.messenger_interop_starter.client.Client;
import com.piLovr.messenger_interop_starter.domain.message.ExtendedMessage;
import com.piLovr.messenger_interop_starter.listener.Listener;
import com.piLovr.messenger_interop_starter.listener.whatsapp.WhatsappInternalListener;
import com.piLovr.messenger_interop_starter.domain.room.Room;
import com.piLovr.messenger_interop_starter.util.QrHandler;
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
