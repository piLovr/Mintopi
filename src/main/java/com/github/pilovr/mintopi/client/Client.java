package com.github.pilovr.mintopi.client;


import com.github.pilovr.mintopi.client.store.Store;
import com.github.pilovr.mintopi.client.tools.TextFromCollectionProvider;
import com.github.pilovr.mintopi.client.tools.MediaConversionEvent;
import com.github.pilovr.mintopi.domain.Listener;
import com.github.pilovr.mintopi.domain.account.Account;
import com.github.pilovr.mintopi.domain.event.EventContext;
import com.github.pilovr.mintopi.domain.payload.message.MessagePayload;
import com.github.pilovr.mintopi.domain.room.Room;
import reactor.core.publisher.Flux;

public sealed interface Client<R extends Room, A extends Account> permits WhatsappClientAdaptee {
    void setConnected(boolean value);
    void connect();
    void disconnect();
    EventContext<R, A, ?> sendMessageAndDecode(R room, String text);
    void sendMessage(R room, String text);
    EventContext<R, A, ?> editMessageAndDecode(EventContext<R, A, ?> origin, String newText);
    void editMessage(EventContext<R, A, ?> origin, String newText);
    EventContext<R, A, ?> sendMessageAndDecode(R room, MessagePayload message);
    void sendMessage(R room, MessagePayload message);

    void addListener(Listener listener);
    void removeListener(Listener listener);

    String getAlias();
    boolean isConnected();

    byte[] downloadMedia(Object payload);

    R updateRoomMetadata(R room);
    Store<R, A> getStore();

    Flux<MessagePayload> executeMediaConversion(ExtendedMessageEvent<R, A> origin, AttachmentType target, int attachmentIndex, TextFromCollectionProvider b);

    Flux<MediaConversionEvent> queueMediaConversion(ExtendedMessageEvent<R,A> origin, AttachmentType target, int attachmentIndex);
}
