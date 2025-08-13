package com.github.pilovr.mintopi.starter.listener;

import com.github.pilovr.mintopi.starter.decoder.whatsapp.WhatsappEventDecoder;
import com.github.pilovr.mintopi.starter.domain.common.Listener;
import com.github.pilovr.mintopi.starter.domain.event.MessageEvent;
import com.github.pilovr.mintopi.starter.domain.message.ExtendedMessage;
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
                MessageEvent e = (MessageEvent) decoder.decode(info);
                listener.onMessage(e);
                if(e.getMessage() instanceof ExtendedMessage){

                }
            } catch (Exception e) {
                //TODO: Handle exception properly
            }
        }
    }
}
