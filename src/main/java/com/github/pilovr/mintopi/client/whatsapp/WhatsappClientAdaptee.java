package com.github.pilovr.mintopi.client.whatsapp;

import com.github.pilovr.mintopi.decoder.whatsapp.WhatsappEventDecoder;
import com.github.pilovr.mintopi.domain.account.Account;
import com.github.pilovr.mintopi.domain.message.ExtendedMessage;
import com.github.pilovr.mintopi.domain.message.Message;
import com.github.pilovr.mintopi.domain.message.ReactionMessage;
import com.github.pilovr.mintopi.client.store.Store;
import com.github.pilovr.mintopi.client.store.WhatsappStore;
import com.github.pilovr.mintopi.client.Client;
import com.github.pilovr.mintopi.domain.Listener;
import com.github.pilovr.mintopi.domain.event.MessageEvent;
import com.github.pilovr.mintopi.domain.room.Room;
import com.github.pilovr.mintopi.client.listener.WhatsappInternalListener;
import com.github.pilovr.mintopi.util.QrHandler;
import it.auties.whatsapp.api.Whatsapp;
import it.auties.whatsapp.api.WhatsappWebHistoryPolicy;
import it.auties.whatsapp.model.info.ChatMessageInfo;
import it.auties.whatsapp.model.info.ContextInfo;
import it.auties.whatsapp.model.info.ContextInfoBuilder;
import it.auties.whatsapp.model.info.MessageInfo;
import it.auties.whatsapp.model.jid.Jid;
import it.auties.whatsapp.model.message.model.MediaMessage;
import it.auties.whatsapp.model.message.standard.*;
import lombok.Getter;
import lombok.Setter;


public class WhatsappClientAdaptee implements Client {
    @Getter
    protected String alias;
    protected Whatsapp api;
    private final WhatsappEventDecoder decoder;
    protected WhatsappInternalListener internalListener;
    @Getter
    protected Store store;

    @Getter @Setter
    protected boolean connected;
    public WhatsappClientAdaptee(String alias, WhatsappInternalListener internalListener, WhatsappStore store, WhatsappEventDecoder decoder) {
        this.alias = alias;

        this.store = store;
        this.internalListener = internalListener;
        this.decoder = decoder;

        store.setClient(this);
        store.setApi(api);
        store.setInternalListener(internalListener);

        internalListener.setClient(this);

        decoder.setWhatsappStore(store);

        this.internalListener.setClient(this);
    }
    @Override
    public void connect() {
        api = Whatsapp.builder()
                .webClient()
                .newConnection(alias)
                .historySetting(WhatsappWebHistoryPolicy.discard(false))
                .unregistered(QrHandler.toTerminal());

        api.addListener(internalListener)
                .connect();
        new Thread(() -> api.waitForDisconnection()).start();
    }

    @Override
    public void disconnect() {

    }

    @Override
    public void addListener(Listener listener) {
        internalListener.registerListener(listener);
    }

    @Override
    public void removeListener(Listener listener) {
        internalListener.unregisterListener(listener);
    }

    @Override
    public Message sendMessage(Room room, String text) {
        return null;
    }

    @Override
    public Message sendMessage(Room room, Message message) {
        Jid target = Jid.of(room.getId());
        MessageInfo res = switch(message.getType().getMessageCategory()){
            case EXTENDED -> {
                ExtendedMessage extendedMessage = (ExtendedMessage) message;
                boolean hasContext = false;
                ContextInfoBuilder contextInfoBuilder = new ContextInfoBuilder();
                if(extendedMessage.getQuoted() != null){
                    hasContext = true;
                    contextInfoBuilder.quotedMessageId(extendedMessage.getQuoted().getId());
                }
                if(extendedMessage.getMentions() != null && !extendedMessage.getMentions().isEmpty()){
                    hasContext = true;
                    contextInfoBuilder.mentions(extendedMessage.getMentions().stream()
                            .map(Account::getId)
                            .map(Jid::of)
                            .toList());
                }
                byte[] media = extendedMessage.getAttachments().getFirst().getDownloadedMedia();
                ContextInfo contextInfo = hasContext ? contextInfoBuilder.build() : null;
                it.auties.whatsapp.model.message.model.Message toSend = switch (extendedMessage.getAttachments().getFirst().getType()){
                    case IMAGE -> new ImageMessageSimpleBuilder()
                            .media(media)
                            .contextInfo(contextInfo)
                            .build();
                    case STICKER -> new SimpleStickerMessageBuilder()
                            .media(media)
                            .contextInfo(contextInfo)
                            .build();
                    case DOCUMENT -> new DocumentMessageSimpleBuilder()
                            .media(media)
                            .contextInfo(contextInfo)
                            .build();
                    case VIDEO -> new VideoMessageSimpleBuilder()
                            .media(media)
                            .contextInfo(contextInfo)
                            .build();
                    case AUDIO -> new AudioMessageSimpleBuilder()
                            .media(media)
                            .contextInfo(contextInfo)
                            .build();
                    case GIF -> new GifMessageSimpleBuilder()
                            .media(media)
                            .contextInfo(contextInfo)
                            .build();
                };
                yield api.sendMessage(target, toSend);
            }
            case REACTION -> {
                ReactionMessage reactionMessage = (ReactionMessage) message;
                yield  api.sendReaction((MessageInfo) message.getPayload(), reactionMessage.getReaction());
            }
            case SPECIAL -> {
                yield null;
            }
            case NONE -> null;
        };
        return ((MessageEvent) decoder.decode(this, res)).getMessage();
    }

    @Override
    public byte[] downloadMedia(Object payload){
        if(payload instanceof ChatMessageInfo chatMessageInfo){
            return api.downloadMedia(chatMessageInfo);
        } else if (payload instanceof MediaMessage mediaMessage) {
            return api.downloadMedia(mediaMessage);
        }
        throw new RuntimeException("unknown media payload type: " + payload.getClass());
    }
}
