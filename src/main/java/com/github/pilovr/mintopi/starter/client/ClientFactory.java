package com.github.pilovr.mintopi.starter.client;

import com.github.pilovr.mintopi.starter.client.whatsapp.WhatsappClientAdaptee;
import com.github.pilovr.mintopi.starter.client.whatsapp.WhatsappMobileClientAdaptee;
import com.github.pilovr.mintopi.starter.decoder.whatsapp.WhatsappEventDecoder;
import com.github.pilovr.mintopi.starter.domain.common.Client;
import com.github.pilovr.mintopi.starter.domain.common.Listener;
import com.github.pilovr.mintopi.starter.domain.common.Platform;
import com.github.pilovr.mintopi.starter.domain.event.StubEvent;
import com.github.pilovr.mintopi.starter.listener.WhatsappInternalListener;
import com.github.pilovr.mintopi.starter.store.Store;
import com.github.pilovr.mintopi.starter.store.WhatsappStore;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ClientFactory {
    private final ObjectProvider<WhatsappEventDecoder> decoderOP;
    private final ObjectProvider<WhatsappInternalListener> wIlOP;
    private final ObjectProvider<WhatsappStore> wStoreOP;
    private final Set<Client> clients = ConcurrentHashMap.newKeySet();

    @Autowired
    public ClientFactory(
            ObjectProvider<WhatsappEventDecoder> decoder,
            ObjectProvider<WhatsappInternalListener> wIlOP,
            ObjectProvider<WhatsappStore> wStoreOP
    ) {
        this.decoderOP = decoder;
        this.wIlOP = wIlOP;
        this.wStoreOP = wStoreOP;
    }

    public Client createClient(Platform platform, String alias){
        Client c =  switch(platform){
            case Whatsapp, WhatsappMobile ->
                    platform == Platform.Whatsapp ? new WhatsappClientAdaptee(alias, wIlOP.getObject(), wStoreOP.getObject(), decoderOP.getObject()) : new WhatsappMobileClientAdaptee(alias, wIlOP.getObject(), wStoreOP.getObject(), decoderOP.getObject());
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
