package com.github.pilovr.mintopi.codec.whatsapp;

import com.github.pilovr.mintopi.client.store.Store;
import com.github.pilovr.mintopi.domain.account.Account;
import com.github.pilovr.mintopi.client.Client;
import com.github.pilovr.mintopi.client.Platform;
import com.github.pilovr.mintopi.domain.event.MessageEvent;
import com.github.pilovr.mintopi.domain.event.StubEvent;
import com.github.pilovr.mintopi.domain.event.StubType;
import com.github.pilovr.mintopi.domain.message.builder.ExtendedMessageBuilder;
import com.github.pilovr.mintopi.domain.message.builder.MessageBuilder;
import com.github.pilovr.mintopi.domain.message.MessageType;
import com.github.pilovr.mintopi.domain.message.attachment.Attachment;
import com.github.pilovr.mintopi.domain.message.attachment.AttachmentBuilder;
import com.github.pilovr.mintopi.domain.message.attachment.AttachmentType;
import com.github.pilovr.mintopi.domain.message.builder.ReactionMessageBuilder;
import com.github.pilovr.mintopi.domain.room.Room;
import it.auties.whatsapp.model.info.*;
import com.github.pilovr.mintopi.codec.MultiCodec;
import it.auties.whatsapp.model.jid.Jid;
import it.auties.whatsapp.model.media.MutableAttachmentProvider;
import it.auties.whatsapp.model.message.model.MediaMessage;
import it.auties.whatsapp.model.message.model.Message;
import it.auties.whatsapp.model.message.model.MessageContainer;
import it.auties.whatsapp.model.message.standard.*;
import it.auties.whatsapp.model.node.Node;
import lombok.Setter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class WhatsappEventDecoder<R extends Room, A extends Account> extends MultiCodec {
    @Setter
    private Store<R, A> store;

    @Autowired
    public WhatsappEventDecoder(Store<R,A> store) {
        this.register(ChatMessageInfo.class, this::decodeChatMessageInfo);
        this.register(MessageInfo.class, this::decodeMessageInfo);
        this.register(Node.class, this::decodeStubNode);

        this.store = store;
    }

    private MessageEvent<?, R, A> decodeMessageInfo(Client client, MessageInfo messageInfo){
        if(messageInfo instanceof ChatMessageInfo chatMessageInfo){
            return decodeChatMessageInfo(client, chatMessageInfo);
        }else if(messageInfo instanceof NewsletterMessageInfo newsletterMessageInfo){
            return null;
        }else if(messageInfo instanceof MessageStatusInfo messageStatusInfo){
            return null;
        }else if(messageInfo instanceof QuotedMessageInfo quotedMessageInfo){
            return null;
        }
        return null;
    }

    private MessageEvent<?, R, A> decodeChatMessageInfo(Client client, ChatMessageInfo chatMessageInfo){
        String id = chatMessageInfo.id();
        Jid sender = chatMessageInfo.senderJid();
        Jid parent = chatMessageInfo.parentJid();

        A account = store.getOrCreateAccount(
                sender.toString(),
                Platform.Whatsapp,
                chatMessageInfo.pushName().orElse(null));
        R room = store.getOrCreateRoom(
                parent.toString(),
                Platform.Whatsapp,
                chatMessageInfo.chatName()
        );

        AttachmentType attachmentType = WhatsappMessageTypeTranslator.translateAttachment(chatMessageInfo.message().type());
        MessageType messageType = attachmentType == null ? WhatsappMessageTypeTranslator.translate(chatMessageInfo.message().type()) : MessageType.TEXT_WITH_ATTACHMENTS;

        MessageBuilder builder = switch(messageType.getMessageCategory()){
            case REACTION -> new ReactionMessageBuilder(id, chatMessageInfo).setReaction(((ReactionMessage) chatMessageInfo.message().content()).content());
            case EXTENDED -> {
                ExtendedMessageBuilder extendedMessageBuilder = decodeMessageContainer(id, chatMessageInfo, chatMessageInfo.message());

                if(chatMessageInfo.message().contentWithContext().isPresent() && chatMessageInfo.message().contentWithContext().get().contextInfo().isPresent()) {
                    if(chatMessageInfo.quotedMessage().isPresent()){
                        QuotedMessageInfo q = chatMessageInfo.quotedMessage().get();
                        extendedMessageBuilder.quoted(decodeMessageContainer(q.id(), q, q.message())
                                .quotedMessageSender(store.getOrCreateAccount(q.senderJid().toString(), Platform.Whatsapp, null))
                                .build());
                    }
                }
                yield extendedMessageBuilder;
            }
            case SPECIAL -> null;
            case NONE -> null;
        };


        if(builder == null) return null;
        return new MessageEvent<>( //members, name, pushname, client, ANIMATED
                client,
                id,
                Platform.Whatsapp,
                chatMessageInfo.timestampSeconds().isPresent() ? chatMessageInfo.timestampSeconds().getAsLong() : null,
                account,
                room,
                builder.build()
        );
    }
    private ExtendedMessageBuilder decodeMessageContainer(String id, MessageInfo payload, MessageContainer messageContainer) {
        Message base = messageContainer.content();
        AttachmentType attachmentType = WhatsappMessageTypeTranslator.translateAttachment(messageContainer.type());
        MessageType messageType = attachmentType == null ? WhatsappMessageTypeTranslator.translate(messageContainer.type()) : MessageType.TEXT_WITH_ATTACHMENTS;
        ExtendedMessageBuilder builder = new ExtendedMessageBuilder(messageType, id, payload);

        if(messageType == MessageType.TEXT_WITH_ATTACHMENTS) {
            assert attachmentType != null;
            builder.text(extractText(base, attachmentType))
                    .attachments(extractAttachments(base, attachmentType));
        } else if (messageType == MessageType.TEXT) {
            builder.text(((TextMessage) base).text());
        }
        if(messageContainer.contentWithContext().isPresent() && messageContainer.contentWithContext().get().contextInfo().isPresent()) {
            ContextInfo context = messageContainer.contentWithContext().get().contextInfo().get();
            List<Jid> mentions = context.mentions();
            List<Account> mentionsResult = new ArrayList<>();
            for(Jid jid  : mentions){
                Account account = store.getOrCreateAccount(jid.toString(), Platform.Whatsapp, null);
                mentionsResult.add(account);
            }
            builder.mentions(mentionsResult);
        }
        return builder;
    }

    private MediaMessage getMediaMessage(Message base, AttachmentType aType) {
        return switch (aType) {
            case VIDEO, GIF -> (VideoOrGifMessage) base;
            case IMAGE -> (ImageMessage) base;
            case DOCUMENT -> (DocumentMessage) base;
            case AUDIO -> (AudioMessage) base;
            case STICKER -> (StickerMessage) base;
            default -> null;
        };
    }

    private String extractText(Message base, AttachmentType aType) {
         return switch (aType) {
            case VIDEO -> ((VideoOrGifMessage) base).caption().orElse(null);
            case IMAGE -> ((ImageMessage) base).caption().orElse(null);
            case DOCUMENT -> ((DocumentMessage) base).caption().orElse(null);
            default -> null;
        };
    }

    private List<Attachment> extractAttachments(Message base, AttachmentType aType) {
        AttachmentBuilder attachmentBuilder = new AttachmentBuilder(aType);

        MutableAttachmentProvider mutableAttachmentProvider = switch (aType) {
            case VIDEO, GIF -> {
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
    }

    private StubEvent decodeStubNode(Client client, Node node){
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
        var room = store.getOrCreateRoom(fromJid.toString(), Platform.Whatsapp, null);
        return new StubEvent(
                client,
                null,
                Platform.Whatsapp,
                timestamp,
                null,
                stubType
        );
    }
}
