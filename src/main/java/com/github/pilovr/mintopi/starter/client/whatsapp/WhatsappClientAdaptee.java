package com.github.pilovr.mintopi.starter.client.whatsapp;

import com.github.pilovr.mintopi.starter.decoder.whatsapp.WhatsappEventDecoder;
import com.github.pilovr.mintopi.starter.store.Store;
import com.github.pilovr.mintopi.starter.store.WhatsappStore;
import com.github.pilovr.mintopi.starter.domain.common.Client;
import com.github.pilovr.mintopi.starter.domain.common.Listener;
import com.github.pilovr.mintopi.starter.domain.event.MessageEvent;
import com.github.pilovr.mintopi.starter.domain.message.*;
import com.github.pilovr.mintopi.starter.domain.room.Room;
import com.github.pilovr.mintopi.starter.listener.WhatsappInternalListener;
import com.github.pilovr.mintopi.starter.util.QrHandler;
import it.auties.whatsapp.api.Whatsapp;
import it.auties.whatsapp.api.WhatsappWebHistoryPolicy;
import it.auties.whatsapp.model.info.ChatMessageInfo;
import it.auties.whatsapp.model.info.ContextInfoBuilder;
import it.auties.whatsapp.model.info.MessageInfo;
import it.auties.whatsapp.model.jid.Jid;
import it.auties.whatsapp.model.message.model.MediaMessage;
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
        MessageInfo res = switch(message.getType().getMessageCategory()){
            case EXTENDED -> {
                ExtendedMessage extendedMessage = (ExtendedMessage) message;
                ContextInfoBuilder b = new ContextInfoBuilder().
                if(!extendedMessage.getMentions().isEmpty()){
                    api.sendMessage()
                }
                if(extendedMessage.getQuoted() != null){

                }
                if(extendedMessage.getAttachments().isEmpty()){
                    if(extendedMessage.getMentions().isEmpty() ){}
                    yield ((MessageEvent) (decoder.decode(this, api.sendMessage(Jid.of(room.getId()), extendedMessage.getText())))).getMessage();
                }

                /*

                 */
            }
            case REACTION -> {
                ReactionMessage reactionMessage = (ReactionMessage) message;
                yield ((MessageEvent) (decoder.decode(this, api.sendReaction((MessageInfo) message.getPayload(), reactionMessage.getReaction())))).getMessage();
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
