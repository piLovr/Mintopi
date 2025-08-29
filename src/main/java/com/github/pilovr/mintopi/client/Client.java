package com.github.pilovr.mintopi.client;


import com.github.pilovr.mintopi.domain.payload.message.attachment.Attachment;
import com.github.pilovr.mintopi.store.Store;
import com.github.pilovr.mintopi.tools.MediaConversionEvent;
import com.github.pilovr.mintopi.listener.Listener;
import com.github.pilovr.mintopi.domain.account.Account;
import com.github.pilovr.mintopi.domain.event.EventContext;
import com.github.pilovr.mintopi.domain.payload.message.MessagePayload;
import com.github.pilovr.mintopi.domain.room.Room;
import reactor.core.publisher.Flux;

public interface Client<R extends Room, A extends Account> {
    void connect();
    void disconnect();

    // Add Listeners
    void addListener(Listener<R,A> listener);
    void removeListener(Listener<R,A> listener);

    // Basic message operations
    void sendMessage(R room, String text);
    void editMessage(EventContext<?, R, A> origin, String newText);
    void sendMessage(R room, MessagePayload message);

    /*
    EventContext<?, R, A> sendMessageAndDecode(R room, String text);
    EventContext<?, R, A> editMessageAndDecode(EventContext<?, R, A> origin, String newText);
    EventContext<?, R, A> sendMessageAndDecode(R room, MessagePayload message);
     */


    String getAlias();
    boolean isConnected();

    byte[] getAttachmentData(Attachment attachment);

    Store<R, A> getStore();

    Flux<MediaConversionEvent> queueMediaConversion(Attachment attachment, String targetMimeType);

    void joinRoom(String invite);

    void leaveRoom(R room);
}
