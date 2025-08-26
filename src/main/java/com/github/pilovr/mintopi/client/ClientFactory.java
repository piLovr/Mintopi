package com.github.pilovr.mintopi.client;

import com.github.pilovr.mintopi.client.whatsapp.WhatsappClientAdaptee;
import com.github.pilovr.mintopi.client.whatsapp.WhatsappMobileClientAdaptee;
import com.github.pilovr.mintopi.store.Store;
import com.github.pilovr.mintopi.tools.MediaQueue;
import com.github.pilovr.mintopi.codec.whatsapp.WhatsappCodec;
import com.github.pilovr.mintopi.listener.Listener;
import com.github.pilovr.mintopi.listener.WhatsappInternalListener;
import com.github.pilovr.mintopi.domain.account.Account;
import com.github.pilovr.mintopi.domain.room.Room;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ClientFactory<R extends Room, A extends Account> {
    private final ObjectProvider<WhatsappCodec<R,A>> decoderOP;
    private final ObjectProvider<WhatsappInternalListener> wIlOP;
    private final Store<R, A> store;
    private final Set<Client<R,A>> clients = ConcurrentHashMap.newKeySet();
    private final MediaQueue mediaQueue;

    @Autowired
    public ClientFactory(
            ObjectProvider<WhatsappCodec<R,A>> decoder,
            ObjectProvider<WhatsappInternalListener> wIlOP,
            Store<R, A> store,
            MediaQueue mediaQueue
    ) {
        this.decoderOP = decoder;
        this.wIlOP = wIlOP;
        this.store = store;
        this.mediaQueue = mediaQueue;
    }

    public Client<R,A> createClient(Platform platform, String alias){
        Client<R,A> c =  switch(platform){
            case WHATSAPP, WhatsappMobile ->
                    platform == Platform.WHATSAPP ? new WhatsappClientAdaptee<>(alias, wIlOP.getObject(), store, decoderOP.getObject(), mediaQueue) : new WhatsappMobileClientAdaptee<>(alias, wIlOP.getObject(),store, decoderOP.getObject(), mediaQueue);
            default -> throw new IllegalArgumentException("Unsupported platform: " + platform);
        };
        c.addListener(new Listener() {
            @Override
            public void onConnected() {
                clients.add(c);
                c.setConnected(true);
            }

            @Override
            public void onDisconnected() {
                clients.remove(c);
                c.setConnected(false);
            }
        });
        return c;
    }
    public void waitForAllDisconnects() {
        while (!clients.isEmpty()) {
            try {
                clients.wait(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // Restore interrupted status
                break; // Exit the loop if interrupted
            }
        }
    }
}
