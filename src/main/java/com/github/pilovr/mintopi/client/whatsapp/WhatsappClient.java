package com.github.pilovr.mintopi.client.whatsapp;

import com.github.pilovr.mintopi.client.Client;
import com.github.pilovr.mintopi.codec.whatsapp.WhatsappCodec;
import com.github.pilovr.mintopi.domain.account.Account;
import com.github.pilovr.mintopi.domain.event.EventContext;
import com.github.pilovr.mintopi.domain.payload.message.MessageContext;
import com.github.pilovr.mintopi.domain.payload.message.MessagePayload;
import com.github.pilovr.mintopi.domain.payload.message.ReactionMessagePayload;
import com.github.pilovr.mintopi.domain.payload.message.TextMessagePayload;
import com.github.pilovr.mintopi.domain.payload.message.attachment.Attachment;
import com.github.pilovr.mintopi.domain.room.Room;
import com.github.pilovr.mintopi.listener.Listener;
import com.github.pilovr.mintopi.listener.WhatsappInternalListener;
import com.github.pilovr.mintopi.store.Store;
import com.github.pilovr.mintopi.tools.MediaConversionEvent;
import com.github.pilovr.mintopi.tools.MediaQueue;
import com.github.pilovr.mintopi.util.QrHandler;
import it.auties.whatsapp.api.Whatsapp;
import it.auties.whatsapp.api.WhatsappWebHistoryPolicy;
import it.auties.whatsapp.model.info.ChatMessageInfo;
import it.auties.whatsapp.model.info.ContextInfo;
import it.auties.whatsapp.model.info.ContextInfoBuilder;
import it.auties.whatsapp.model.info.MessageInfo;
import it.auties.whatsapp.model.jid.Jid;
import it.auties.whatsapp.model.message.model.MediaMessage;
import it.auties.whatsapp.model.message.model.Message;
import it.auties.whatsapp.model.message.model.MessageContainer;
import it.auties.whatsapp.model.message.standard.*;
import lombok.Getter;
import lombok.Setter;
import reactor.core.publisher.Flux;

public class WhatsappClient <R extends Room, A extends Account> implements Client<R,A> {
    @Getter
    protected String alias;
    protected Whatsapp api;

    @Getter
    protected Store<R,A> store;
    private WhatsappCodec<R,A> decoder;
    protected WhatsappInternalListener internalListener;


    @Getter @Setter
    protected boolean connected;
    private final MediaQueue mediaQueue;

    public WhatsappClient(String alias, WhatsappInternalListener internalListener, Store<R,A> store, WhatsappCodec<R,A> decoder, MediaQueue mediaQueue) {
        this.alias = alias;

        this.store = store;
        this.internalListener = internalListener;
        this.decoder = decoder;

        this.mediaQueue = mediaQueue;

        internalListener.setClient(this);

        decoder.setStore(store);
    }

    @Override
    public void connect() {
        api = Whatsapp.builder()
                .webClient()
                .newConnection(alias)
                .historySetting(WhatsappWebHistoryPolicy.discard(false))
                .unregistered(QrHandler.toTerminal());

        api.addListener(internalListener)
                .addDisconnectedListener(this::onDisconnected)
                .connect();

        new Thread(() -> api.waitForDisconnection()).start();
        this.connected = true;
    }

    private void onDisconnected(it.auties.whatsapp.api.WhatsappDisconnectReason whatsappDisconnectReason) {
        this.connected = false;
    }

    @Override
    public void disconnect() {
        api.disconnect();
    }

    @Override
    public void addListener(Listener<R,A> listener) {
        this.internalListener.registerListener(listener);
    }

    @Override
    public void removeListener(Listener<R,A> listener) {
        this.internalListener.unregisterListener(listener);
    }

    @Override
    public void sendMessage(R room, String text) {
        api.sendMessage(Jid.of(room.getPlatformId()), text);
    }

    @Override
    public void editMessage(EventContext<?, R, A> origin, String newText) {
        if(origin.getPayload() instanceof MessagePayload messagePayload) {
            if(origin.getOriginalObject() instanceof MessageInfo messageInfo){
                Message toSend = null;
                Message original = messageInfo.message().content();
                switch(messageInfo.message().type()){
                    case TEXT -> {
                        TextMessage textMessage = (TextMessage) original;
                        textMessage.setText(newText);
                        toSend = textMessage;
                    }
                    case IMAGE -> {
                        return;
                    }
                    case VIDEO -> {
                        return;
                    }
                    default -> {
                        return;
                    }
                }
                api.editMessage(messageInfo, toSend);
            }
        }
    }

    @Override
    public void sendMessage(R room, MessagePayload message) {
        Jid target = Jid.of(room.getPlatformId());
        it.auties.whatsapp.model.message.model.Message toSend = null;

        if(message instanceof TextMessagePayload<?,?>){
            TextMessagePayload<R, A> textMessagePayload = (TextMessagePayload<R,A>) message;
            MessageContext<R,A> context = textMessagePayload.getContext();
            boolean hasContext = false;
            ContextInfoBuilder contextInfoBuilder = new ContextInfoBuilder();
            if(context.getQuoted() != null){
                hasContext = true;
                MessageContainer quotedContainer = null;
                if(context.getQuoted().getOriginalObject() instanceof MessageInfo messageInfo){
                    quotedContainer = messageInfo.message();
                }else if(context.getQuoted().getOriginalObject() instanceof MessageContainer messageContainer){
                    quotedContainer = messageContainer;
                }
                contextInfoBuilder
                        .quotedMessageId(textMessagePayload.getContext().getQuoted().getId())
                        .quotedMessageChatJid(Jid.of(context.getQuoted().getRoom().getPlatformId()))
                        .quotedMessageSenderJid(Jid.of(context.getQuoted().getSender().getPlatformId()))
                        .quotedMessage(quotedContainer);
            }
            if(context.getMentions() != null && !context.getMentions().isEmpty()){
                hasContext = true;
                contextInfoBuilder.mentions(context.getMentions().stream()
                        .map(Account::getPlatformId)
                        .map(Jid::of)
                        .toList());
            }
            ContextInfo contextInfo = hasContext ? contextInfoBuilder.build() : null;

            if(textMessagePayload.getAttachments() == null || textMessagePayload.getAttachments().isEmpty()){
                toSend = new TextMessageBuilder()
                        .text(textMessagePayload.getText())
                        .contextInfo(contextInfo)
                        .build();
            }else {
                byte[] media = getAttachmentData(textMessagePayload.getAttachments().getFirst());
                toSend = switch (textMessagePayload.getAttachments().getFirst().getMimeType()) {
                    case "image/png", "image/jpeg" -> new ImageMessageSimpleBuilder()
                            .caption(textMessagePayload.getText())
                            .media(media)
                            .contextInfo(contextInfo)
                            .build();
                    case "video/mp4" -> new VideoMessageSimpleBuilder()
                            .caption(textMessagePayload.getText())
                            .media(media)
                            .contextInfo(contextInfo)
                            .build();
                    case "audio/mpeg", "audio/ogg", "audio/mp4" -> new AudioMessageSimpleBuilder()
                            .media(media)
                            .contextInfo(contextInfo)
                            .build();
                    case "application/pdf", "application/zip",
                         "application/vnd.openxmlformats-officedocument.wordprocessingml.document" ->
                            new DocumentMessageSimpleBuilder()
                                    .caption(textMessagePayload.getText())
                                    .media(media)
                                    .contextInfo(contextInfo)
                                    .build();
                    case "image/webp" -> new SimpleStickerMessageBuilder()
                            .media(media)
                            .contextInfo(contextInfo)
                            .build();
                    case "image/gif" -> new GifMessageSimpleBuilder()
                            .media(media)
                            .contextInfo(contextInfo)
                            .build();
                    default -> null;
                };
            }
        }
        if(message instanceof ReactionMessagePayload) {
            ReactionMessagePayload<R, A> reactionMessagePayload = (ReactionMessagePayload<R, A>) message;
            if (reactionMessagePayload.getQuoted().getOriginalObject() instanceof ChatMessageInfo messageInfo) {
                api.sendReaction(messageInfo, reactionMessagePayload.getReaction());
                return;
            }
        }
        if(toSend == null) return;
        api.sendMessage(target, toSend);
    }

    @Override
    public byte[] getAttachmentData(Attachment attachment) {
        byte[] data = attachment.getData();
        if(data == null){
            MediaMessage mediaMessage = (MediaMessage) attachment.getPlatformSpecificData();
            data = api.downloadMedia(mediaMessage);
            attachment.setData(data);
        }
        return data;
    }

    @Override
    public Flux<MediaConversionEvent> queueMediaConversion(Attachment attachment, String targetMimeType) {
        getAttachmentData(attachment);
        return mediaQueue.addToQueue(attachment, targetMimeType);
    }

    @Override
    public void joinRoom(String invite) {
        api.acceptGroupInvite(invite);
    }

    @Override
    public void leaveRoom(R room){
        api.leaveGroup(Jid.of(room.getPlatformId()));
    }
}
