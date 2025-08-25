package com.github.pilovr.mintopi.client;


import com.github.pilovr.mintopi.client.store.Store;
import com.github.pilovr.mintopi.client.tools.CommandResultBuilder;
import com.github.pilovr.mintopi.client.tools.MediaConversionEvent;
import com.github.pilovr.mintopi.domain.Listener;
import com.github.pilovr.mintopi.domain.account.Account;
import com.github.pilovr.mintopi.domain.event.ExtendedMessageEvent;
import com.github.pilovr.mintopi.domain.event.MessageEvent;
import com.github.pilovr.mintopi.domain.message.Message;
import com.github.pilovr.mintopi.domain.message.attachment.AttachmentType;
import com.github.pilovr.mintopi.domain.room.Room;
import reactor.core.publisher.Flux;

import java.util.concurrent.CompletableFuture;

public sealed interface Client<R extends Room, A extends Account> permits WhatsappClientAdaptee {
    void setConnected(boolean value);
    void connect();
    void disconnect();
    MessageEvent<? ,R, A> sendMessage(R room, String text);

    MessageEvent<? ,R, A> editMessage(MessageEvent<?, R, A> origin, String newText);

    MessageEvent<? ,R, A> sendMessage(R room, Message message);

    void addListener(Listener listener);
    void removeListener(Listener listener);

    String getAlias();
    boolean isConnected();

    byte[] downloadMedia(Object payload);

    R updateRoomMetadata(R room);
    Store<R, A> getStore();

    Flux<Message> executeMediaConversion(ExtendedMessageEvent<R, A> origin, AttachmentType target, int attachmentIndex, CommandResultBuilder b);

    Flux<MediaConversionEvent> queueMediaConversion(ExtendedMessageEvent<R,A> origin, AttachmentType target, int attachmentIndex);
}
