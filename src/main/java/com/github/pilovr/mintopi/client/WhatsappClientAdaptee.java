package com.github.pilovr.mintopi.client;

import com.github.pilovr.mintopi.client.store.Store;
import com.github.pilovr.mintopi.client.tools.CommandResultBuilder;
import com.github.pilovr.mintopi.client.tools.MediaConversionEvent;
import com.github.pilovr.mintopi.client.tools.MediaQueue;
import com.github.pilovr.mintopi.codec.whatsapp.WhatsappEventDecoder;
import com.github.pilovr.mintopi.domain.account.Account;
import com.github.pilovr.mintopi.domain.event.ExtendedMessageEvent;
import com.github.pilovr.mintopi.domain.message.ExtendedMessage;
import com.github.pilovr.mintopi.domain.message.Message;
import com.github.pilovr.mintopi.domain.message.ReactionMessage;
import com.github.pilovr.mintopi.domain.Listener;
import com.github.pilovr.mintopi.domain.event.MessageEvent;
import com.github.pilovr.mintopi.domain.message.attachment.AttachmentBuilder;
import com.github.pilovr.mintopi.domain.message.attachment.AttachmentType;
import com.github.pilovr.mintopi.domain.message.builder.ExtendedMessageBuilder;
import com.github.pilovr.mintopi.domain.room.Room;
import com.github.pilovr.mintopi.client.listener.WhatsappInternalListener;
import com.github.pilovr.mintopi.util.QrHandler;
import it.auties.whatsapp.api.Whatsapp;
import it.auties.whatsapp.api.WhatsappWebHistoryPolicy;
import it.auties.whatsapp.model.chat.ChatMetadata;
import it.auties.whatsapp.model.chat.ChatRole;
import it.auties.whatsapp.model.info.ChatMessageInfo;
import it.auties.whatsapp.model.info.ContextInfo;
import it.auties.whatsapp.model.info.ContextInfoBuilder;
import it.auties.whatsapp.model.info.MessageInfo;
import it.auties.whatsapp.model.jid.Jid;
import it.auties.whatsapp.model.message.model.MediaMessage;
import it.auties.whatsapp.model.message.model.MessageContainerBuilder;
import it.auties.whatsapp.model.message.standard.*;
import lombok.Getter;
import lombok.Setter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;


public sealed class WhatsappClientAdaptee <R extends Room, A extends Account> implements Client<R,A> permits WhatsappMobileClientAdaptee {
    @Getter
    protected String alias;
    protected Whatsapp api;
    private final WhatsappEventDecoder<R,A> decoder;
    protected WhatsappInternalListener internalListener;
    @Getter
    protected Store<R,A> store;

    @Getter @Setter
    protected boolean connected;
    private final MediaQueue mediaQueue;

    public WhatsappClientAdaptee(String alias, WhatsappInternalListener internalListener, Store<R,A> store, WhatsappEventDecoder<R,A> decoder, MediaQueue mediaQueue) {
        this.alias = alias;

        this.store = store;
        this.internalListener = internalListener;
        this.decoder = decoder;

        this.mediaQueue = mediaQueue;

        internalListener.setClient(this);

        decoder.setStore(store);

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
    public MessageEvent<? ,R, A> sendMessage(Room room, String text) {
        return (MessageEvent<? ,R, A>) decoder.decode(this,api.sendMessage(Jid.of(room.getId()), text));
    }

    @Override
    public MessageEvent<? ,R, A> editMessage(MessageEvent<?, R, A> origin, String newText) {
        it.auties.whatsapp.model.message.model.Message message = new MessageContainerBuilder()
                .audioMessage()
                .build().content();
            MessageInfo messageInfo = (MessageInfo) origin.getMessage().getPayload();
            MessageInfo edited = api.editMessage(messageInfo, message);
            return ((MessageEvent) decoder.decode(this, edited)).getMessage();

        return null;
    }

    public it.auties.whatsapp.model.message.model.Message buildInternalMessage(Message message){
        return switch(message.getType().getMessageCategory()){
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
                MessageContainerBuilder messageContainerBuilder = new MessageContainerBuilder()
                        .audioMessage(
                                new AudioMessageBuilder()
                                        .
                        )
            }
            case REACTION -> {
                yield null;
            }
            case SPECIAL -> {
                yield null;
            }
            case NONE -> null;
        };
    }

    @Override
    public MessageEvent<? ,R, A> sendMessage(Room room, Message message) {
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

        return ((MessageEvent) decoder.decode(this, res));
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

    @Override
    public R updateRoomMetadata(R room) {
        ChatMetadata meta = api.queryGroupMetadata(Jid.of(room.getId()));
        Map<Account, Set<String>> members = new HashMap<>();
        meta.participants().forEach(participant -> {
            Set<String> roles = new HashSet<>(Collections.singleton(participant.role().data()));
            if(participant.role() == ChatRole.FOUNDER) {
                roles.add("admin");
            }
            members.put(store.getOrCreateAccount(participant.jid().toString(), Platform.Whatsapp, null), roles);
        });
        room.setMembers(members);
        room.setFounder(store.getOrCreateAccount(meta.founder().toString(), Platform.Whatsapp, null));
        room.setDescription(meta.description().toString());
        room.setEphemeralExpiration(meta.ephemeralExpirationSeconds());
        return room;

    }

    @Override
    public Flux<Message> executeMediaConversion(ExtendedMessageEvent<R, A> origin, AttachmentType target, int attachmentIndex, CommandResultBuilder b) {
        AtomicReference<MessageEvent> sentMessageEvent = new AtomicReference<>();

        return mediaQueue.addToQueue(origin, target, attachmentIndex)
                .flatMap(event -> {
                    switch (event.eventType()) {
                        case CONVERSION_STARTED -> {
                            Message message = b.setKey("conversion_started")
                                    .addPlaceholder("position", String.valueOf(event.pos()))
                                    .buildAndSend();
                            sentMessage.set(message);
                            return Mono.just(message);
                        }
                        case POS_UPDATED -> {
                            if(sentMessage.get() != null){
                                editMessage()
                            }
                            // TODO: implement position update logic
                            return Mono.empty();
                        }
                        case CONVERSION_SUCCEEDED -> {
                            Message message = b.setMessageBuilder(
                                    new ExtendedMessageBuilder().addAttachment(
                                            new AttachmentBuilder(target)
                                                    .downloadedMedia(event.result())
                                                    .build()
                                    )
                            ).buildAndSend();
                            return Mono.just(message);
                        }
                        case CONVERSION_FAILED -> {
                            return Mono.error(new RuntimeException("Conversion failed"));
                        }
                        default -> {
                            return Mono.empty();
                        }
                    }
                });
    }

    @Override
    public Flux<MediaConversionEvent> queueMediaConversion(ExtendedMessageEvent<R,A> origin, AttachmentType target, int attachmentIndex) {
        return mediaQueue.addToQueue(origin, target, attachmentIndex);
    }
}
