package com.github.pilovr.mintopi.listener;

import com.github.pilovr.mintopi.decoder.whatsapp.WhatsappEventDecoder;
import com.github.pilovr.mintopi.domain.common.Client;
import com.github.pilovr.mintopi.domain.common.InternalListener;
import com.github.pilovr.mintopi.domain.common.Listener;
import com.github.pilovr.mintopi.domain.event.ExtendedMessageEvent;
import com.github.pilovr.mintopi.domain.event.MessageEvent;
import com.github.pilovr.mintopi.domain.event.StubEvent;
import com.github.pilovr.mintopi.domain.message.ExtendedMessage;
import it.auties.whatsapp.api.Whatsapp;
import it.auties.whatsapp.api.WhatsappDisconnectReason;
import it.auties.whatsapp.api.WhatsappListener;
import it.auties.whatsapp.model.chat.Chat;
import it.auties.whatsapp.model.info.ChatMessageInfo;
import it.auties.whatsapp.model.info.MessageInfo;
import it.auties.whatsapp.model.info.QuotedMessageInfo;
import it.auties.whatsapp.model.node.Node;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
@Scope("prototype")
public class WhatsappInternalListener implements WhatsappListener, InternalListener {
    private final List<Listener> listeners = new CopyOnWriteArrayList<>();
    private final WhatsappEventDecoder decoder;

    @Setter
    private Client client;

    @Autowired
    public WhatsappInternalListener(WhatsappEventDecoder decoder){
        this.decoder = decoder;
    }

    public void registerListener(Listener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void unregisterListener(Listener listener) {
        listeners.remove(listener);
    }

    public String onInputRequired(String message) {
        for (Listener listener : listeners) {
            try {
                return listener.onInputRequired(message);
            } catch (Exception e) {
                System.out.println("Error in onInputRequired: " + e.getMessage());
            }
        }
        return null;
    }

    @Override
    public void onNewMessage(MessageInfo info) {
        if(!(info instanceof ChatMessageInfo)){
            System.out.println("Received non-chat message: " + info);
        }
        for (Listener listener : listeners) {
            try {
                MessageEvent e = (MessageEvent) decoder.decode(client, info);
                listener.onMessage(e);
                if(e.getMessage() instanceof ExtendedMessage){
                    listener.onExtendedMessage(new ExtendedMessageEvent(e));
                }else{
                    listener.onSpecialMessage(e);
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    @Override
    public void onLoggedIn(Whatsapp whatsapp) {
        WhatsappListener.super.onLoggedIn(whatsapp);
    }

    @Override
    public void onDisconnected(Whatsapp whatsapp, WhatsappDisconnectReason reason) {
        WhatsappListener.super.onDisconnected(whatsapp, reason);
    }

    @Override
    public void onMessageDeleted(Whatsapp whatsapp, MessageInfo info, boolean everyone) {
        WhatsappListener.super.onMessageDeleted(whatsapp, info, everyone);
    }

    @Override
    public void onMessageReply(Whatsapp whatsapp, MessageInfo response, QuotedMessageInfo quoted) {
        WhatsappListener.super.onMessageReply(whatsapp, response, quoted);
    }

    @Override
    public void onGroupPictureChanged(Whatsapp whatsapp, Chat group) {
        WhatsappListener.super.onGroupPictureChanged(whatsapp, group);
    }

    @Override
    public void onNodeReceived(Whatsapp whatsapp, Node node) {
        var type = node.attributes().getString("type", null);
        if(Objects.equals(type, "w:gp2")){
            StubEvent event = (StubEvent) decoder.decode(client, node);
            if(event != null) {
                for (Listener listener : listeners) {
                    listener.onStubEvent(event);
                }
            }
        }
        /*
        switch (type) {
            case "w:gp2" -> decoder.decode(client,node);
            case "server_sync" -> handleServerSyncNotification(node);
            case "account_sync" -> handleAccountSyncNotification(node);
            case "encrypt" -> handleEncryptNotification(node);
            case "picture" -> handlePictureNotification(node);
            case "registration" -> handleRegistrationNotification(node);
            case "link_code_companion_reg" -> handleCompanionRegistration(node);
            case "newsletter" -> handleNewsletter(from, node);
            case "mex" -> handleMexNamespace(node);
         */

    }
}
