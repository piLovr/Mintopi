package com.github.pilovr.mintopi.codec.whatsapp;

import com.github.pilovr.mintopi.domain.event.EventContext;
import com.github.pilovr.mintopi.domain.payload.Payload;
import com.github.pilovr.mintopi.domain.payload.message.*;
import com.github.pilovr.mintopi.domain.payload.message.attachment.Attachment;
import com.github.pilovr.mintopi.store.Store;
import com.github.pilovr.mintopi.domain.account.Account;
import com.github.pilovr.mintopi.client.Platform;
import com.github.pilovr.mintopi.domain.event.StubType;
import com.github.pilovr.mintopi.domain.room.Room;
import it.auties.whatsapp.model.info.*;
import com.github.pilovr.mintopi.codec.MultiCodec;
import it.auties.whatsapp.model.jid.Jid;
import it.auties.whatsapp.model.message.model.*;
import it.auties.whatsapp.model.message.standard.*;
import it.auties.whatsapp.model.node.Node;
import lombok.Setter;

import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@Service
public class WhatsappCodec<R extends Room, A extends Account> extends MultiCodec {
    @Setter
    private Store<R, A> store;

    @Autowired
    public WhatsappCodec(Store<R, A> store) {
        this.registerDecode(ChatMessageInfo.class, this::decodeChatMessageInfo);
        this.registerDecode(MessageInfo.class, this::decodeMessageInfo);
        this.registerDecode(Node.class, this::decodeStubNode);

        this.store = store;
    }

    @Nullable
    private EventContext<Payload, R, A> decodeMessageInfo(MessageInfo messageInfo) {
        if (messageInfo instanceof ChatMessageInfo chatMessageInfo) {
            return decodeChatMessageInfo(chatMessageInfo);
        } else if (messageInfo instanceof NewsletterMessageInfo newsletterMessageInfo) {
            return null;
        } else if (messageInfo instanceof MessageStatusInfo messageStatusInfo) {
            return null;
        } else if (messageInfo instanceof QuotedMessageInfo quotedMessageInfo) {
            return null;
        }
        return null;
    }

    private EventContext<Payload,R,A> decodeChatMessageInfo(ChatMessageInfo chatMessageInfo) {
        MessageContainer container = chatMessageInfo.message();

        ChatMessageKey key = chatMessageInfo.key();
        EventContext.EventContextBuilder<Payload, R, A> builder = EventContext.<Payload, R, A>builder()
                .originalObject(chatMessageInfo)
                .platform(Platform.WHATSAPP)
                .fromMe(key.fromMe())
                .id(key.id())
                .timestamp(Instant.ofEpochSecond(chatMessageInfo.timestampSeconds().isPresent() ? chatMessageInfo.timestampSeconds().getAsLong() : null))
                .room(
                    store.getOrCreateRoom(
                        key.chatJid().toString(),
                        Platform.WHATSAPP,
                        chatMessageInfo.chatName())
                )
                .sender(
                    store.getOrCreateAccount(
                        key.senderJid().toString(),
                        Platform.WHATSAPP,
                        chatMessageInfo.pushName().orElse(null))
                );

        MessageContext<R,A> context = null;
        if(container.contentWithContext().isPresent()){
            ContextualMessage originContext = container.contentWithContext().get();
            if(originContext.contextInfo().isPresent()) {
                ContextInfo contextInfo = originContext.contextInfo().get();
                List<A> mentions = extractMentions(contextInfo.mentions());

                EventContext.EventContextBuilder<Payload,R,A> quotedEventBuilder = null;

                if (contextInfo.quotedMessage().isPresent()) {
                    Jid quotedSender = contextInfo.quotedMessageSenderJid().orElse(null);
                    quotedEventBuilder = EventContext.<Payload, R, A>builder()
                            .platform(Platform.WHATSAPP)
                            .fromMe(false)
                            .id(contextInfo.quotedMessageId().orElse(null))
                            .room(
                                store.getOrCreateRoom(
                                    Objects.requireNonNull(contextInfo.quotedMessageChatJid().orElse(null)).toString(),
                                    Platform.WHATSAPP,
                                    null)
                            )
                            .sender(
                                store.getOrCreateAccount(
                                    quotedSender != null ? quotedSender.toString() : null,
                                    Platform.WHATSAPP,
                                    null)
                            )
                            .originalObject(contextInfo.quotedMessage().get());
                    MessageContainer q = contextInfo.quotedMessage().get();
                    quotedEventBuilder.payload(decodeMessageContainer(contextInfo.quotedMessage().get(), null));
                }
                context = new MessageContext<R,A>(
                    mentions,
                    quotedEventBuilder != null ? quotedEventBuilder.build() : null
                );
            }
        }

        MessagePayload messagePayload = decodeMessageContainer(container, context);
        return builder.payload(messagePayload).build();
    }

    private EventContext<?,R,A> decodeStubNode(Node node){
        var child = node.findChild();
        if (child.isEmpty()) {
            return null;
        }

        StubType stubType = StubType.of(child.get().description()).orElse(StubType.UNKNOWN);
        if (stubType != StubType.UNKNOWN) {
            return null;
        }

        var timestamp = node.attributes().getLong("t");

        var fromJid = node.attributes()
                .getRequiredJid("from");
        var room = store.getOrCreateRoom(fromJid.toString(), Platform.WHATSAPP, null);
        /*
        return new StubEvent(
                client,
                null,
                Platform.WHATSAPP,
                timestamp,
                null,
                stubType
        );
         */
        return null;
    }


    private MessagePayload decodeMessageContainer(MessageContainer container, MessageContext<R,A> context) {
        Message message = container.content();
        switch(container.type()) {
            case TEXT-> {
                TextMessage textMessage = (TextMessage) message;
                return TextMessagePayload.<R, A>builder()
                        .text(textMessage.text())
                        .context(context)
                        .build();
            }
            case VIDEO -> {
                VideoOrGifMessage videoMessage = (VideoOrGifMessage) message;
                return TextMessagePayload.<R, A>builder()
                        .text(videoMessage.caption().orElse(null))
                        .attachments(Collections.singletonList(new Attachment(videoMessage.mimetype().get(), null, videoMessage)))
                        .context(context)
                        .build();
            }
            case IMAGE -> {
                ImageMessage imageMessage = (ImageMessage) message;
                return TextMessagePayload.<R, A>builder()
                        .text(imageMessage.caption().orElse(null))
                        .attachments(Collections.singletonList(new Attachment(imageMessage.mimetype().get(), null, imageMessage)))
                        .context(context)
                        .build();
            }
            case AUDIO -> {
                AudioMessage audioMessage = (AudioMessage) message;
                return TextMessagePayload.<R, A>builder()
                        .attachments(Collections.singletonList(new Attachment(audioMessage.mimetype().get(), null, audioMessage)))
                        .context(context)
                        .build();
            }
            case STICKER -> {
                StickerMessage stickerMessage = (StickerMessage) message;
                return TextMessagePayload.<R, A>builder()
                        .attachments(Collections.singletonList(new Attachment(stickerMessage.mimetype().get(), null, stickerMessage)))
                        .context(context)
                        .build();
            }
            case DOCUMENT -> {
                DocumentMessage documentMessage = (DocumentMessage) message;
                return TextMessagePayload.<R, A>builder()
                        .text(documentMessage.caption().orElse(null))
                        .attachments(Collections.singletonList(new Attachment(documentMessage.mimetype().get(), null, documentMessage)))
                        .context(context)
                        .build();
            }
            case REACTION -> {
                ReactionMessage reactionMessage = (ReactionMessage) message;
                return new ReactionMessagePayload(
                        reactionMessage.content(),
                        null
                );
            }
            case EDITED -> {
                //FutureMessageContainer
            }
            case EPHEMERAL -> {
                //FutureMessageContainer
            }
            case VIEW_ONCE -> {
                //FutureMessageContainer
            }
            case GROUP_INVITE -> {
                GroupInviteMessage groupInviteMessage = (GroupInviteMessage) message;
            }
            case POLL_CREATION -> {
                PollCreationMessage pollCreationMessage = (PollCreationMessage) message;
            }
            case POLL_UPDATE -> {
                PollUpdateMessage pollUpdateMessage = (PollUpdateMessage) message;
            }
        }
        return null;
    }

    private MediaMessage getMediaMessage(Message base, Message.Type messageType) {
        return switch (messageType) {
            case VIDEO -> (VideoOrGifMessage) base;
            case IMAGE -> (ImageMessage) base;
            case DOCUMENT -> (DocumentMessage) base;
            case AUDIO -> (AudioMessage) base;
            case STICKER -> (StickerMessage) base;
            default -> null;
        };
    }

    private List<A> extractMentions(List<Jid> originMentions) {
        List<A> mentions = new LinkedList<>();
        for(Jid jid : originMentions){
            A a = store.getOrCreateAccount(jid.toString(), Platform.WHATSAPP, null);
            mentions.add(a);
        }
        return mentions;
    }

    private String extractText(Message base, Message.Type messageType) {
        return switch (messageType) {
            case TEXT -> ((TextMessage) base).text();
            case VIDEO -> ((VideoOrGifMessage) base).caption().orElse(null);
            case IMAGE -> ((ImageMessage) base).caption().orElse(null);
            case DOCUMENT -> ((DocumentMessage) base).caption().orElse(null);
            default -> null;
        };
    }

    private List<Attachment> extractAttachments(Message base, Message.Type messageType) {
        /*
        MutableAttachmentProvider mutableAttachmentProvider = switch (messageType) {
            case VIDEO -> {
                VideoOrGifMessage m = ((VideoOrGifMessage) base);
                attachmentBuilder
                        .duration(m.duration().orElse(0))
                        .width(m.width().orElse(0))
                        .height(m.height().orElse(0))
                        .thumbnail(m.thumbnail().orElse(null))
                        .mimeType(m.mimetype().orElse(null))
                        .streamingSidecar(m.streamingSidecar().orElse(null))
                        .downloadableMedia(m);
                yield m;
            }
            case IMAGE -> {
                ImageMessage m = ((ImageMessage) base);
                attachmentBuilder
                        .width(m.width().orElse(0))
                        .height(m.height().orElse(0))
                        .thumbnail(m.thumbnail().orElse(null))
                        .mimeType(m.mimetype().orElse(null))
                        .downloadableMedia(m);
                yield m;
            }
            case DOCUMENT -> {
                DocumentMessage m = ((DocumentMessage) base);
                attachmentBuilder
                        .mimeType(m.mimetype().orElse(null))
                        .downloadableMedia(m);
                yield m;
            }
            case AUDIO -> {
                AudioMessage m = ((AudioMessage) base);
                attachmentBuilder
                        .duration(m.duration().orElse(0))
                        .mimeType(m.mimetype().orElse(null))
                        .downloadableMedia(m);
                yield m;
            }
            case STICKER -> { //check mimetype for animated
                StickerMessage m = ((StickerMessage) base);
                attachmentBuilder
                        .mimeType(m.mimetype().orElse(null))
                        .downloadableMedia(m);
                yield m;
            }
        };
        attachmentBuilder
                .mediaUrl(mutableAttachmentProvider.mediaUrl().orElse(null))
                .mediaKey(mutableAttachmentProvider.mediaKey().orElse(null))
                .mediaEncryptedSha256(mutableAttachmentProvider.mediaEncryptedSha256().orElse(null))
                .mediaSha256(mutableAttachmentProvider.mediaSha256().orElse(null))
                .mediaSize((int) mutableAttachmentProvider.mediaSize().orElse(0))
                .mediaDirectPath(mutableAttachmentProvider.mediaDirectPath().orElse(null));

        return List.of(attachmentBuilder.build());

         */
        return null;
    }
}
