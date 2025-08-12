package com.piLovr.messenger_interop_starter.listener.whatsapp;

import com.piLovr.messenger_interop_starter.domain.event.MessageEvent;
import com.piLovr.messenger_interop_starter.decoder.whatsapp.WhatsappEventDecoder;
import com.piLovr.messenger_interop_starter.listener.Listener;
import it.auties.whatsapp.api.WhatsappListener;
import it.auties.whatsapp.model.info.MessageInfo;


import java.util.List;

public class WhatsappInternalListener implements WhatsappListener {
    private final List<Listener> listeners;
    private WhatsappEventDecoder decoder;

    public WhatsappInternalListener(List<Listener> listeners){
        this.listeners = listeners;
        decoder = new WhatsappEventDecoder();
    }

    @Override
    public void onNewMessage(MessageInfo info) {
        for (Listener listener : listeners) {
            try {
                listener.onMessage((MessageEvent) decoder.decode(info));
            } catch (Exception e) {
                //TODO: Handle exception properly
            }
        }
    }
}
