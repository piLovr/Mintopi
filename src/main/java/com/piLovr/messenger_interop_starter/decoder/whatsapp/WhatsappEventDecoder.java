package com.piLovr.messenger_interop_starter.decoder.whatsapp;

import com.piLovr.messenger_interop_starter.domain.account.Account;
import com.piLovr.messenger_interop_starter.domain.common.Platform;
import com.piLovr.messenger_interop_starter.domain.event.MessageEvent;
import com.piLovr.messenger_interop_starter.domain.message.MessageBuilder;
import com.piLovr.messenger_interop_starter.domain.message.attachment.AttachmentType;
import com.piLovr.messenger_interop_starter.domain.message.types.MessageType;
import com.piLovr.messenger_interop_starter.domain.room.Room;
import com.piLovr.messenger_interop_starter.repository.storage.CacheManager;
import it.auties.whatsapp.model.info.ChatMessageInfo;
import it.auties.whatsapp.model.info.MessageInfo;
import com.piLovr.messenger_interop_starter.decoder.MultiEventDecoder;
import it.auties.whatsapp.model.jid.Jid;
import it.auties.whatsapp.model.message.model.Message;
import it.auties.whatsapp.model.message.model.MessageContainer;
import it.auties.whatsapp.model.message.standard.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
public class WhatsappEventDecoder extends MultiEventDecoder {
    private CacheManager cacheManager;
    public WhatsappEventDecoder() {
        this.register(MessageInfo.class, this::decodeMessage);
    }

    @Autowired
    public WhatsappEventDecoder(CacheManager cacheManager) {
        super();
        this.cacheManager = cacheManager;
    }

    private MessageEvent decodeMessage(MessageInfo messageInfo) {
        Message base = messageInfo.message().content();
        ChatMessageInfo chatMessageInfo = (ChatMessageInfo) messageInfo;
        AttachmentType attachmentType = WhatsappMessageTypeTranslator.translateAttachment(messageInfo.message().type());
        MessageType messageType = attachmentType == null ? WhatsappMessageTypeTranslator.translate(messageInfo.message().type()) : MessageType.TEXT_WITH_ATTACHMENTS;
        MessageBuilder builder = new MessageBuilder(messageType);

        if(messageType == MessageType.TEXT_WITH_ATTACHMENTS) {
            assert attachmentType != null;
            extractText(base, attachmentType, builder);
            extractAttachments(base, attachmentType, builder);
        } else if (messageType == MessageType.TEXT) {
            builder.setText(((TextMessage) base).text());
        }

        Jid sender = messageInfo.senderJid();
        Account account = cacheManager.getOrCreateAccount(sender.toString(), Platform.Whatsapp, chatMessageInfo.pushName().orElse(null));

        Jid room = messageInfo.parentJid();


        return new MessageEvent(
                messageInfo.id(),
                null,
                null,
                Platform.Whatsapp,
                messageInfo,
                builder.build()
        );
    }

    private void extractText(Message base, AttachmentType aType, MessageBuilder builder) {
        builder.setText(switch (aType) {
            case VIDEO -> ((VideoOrGifMessage) base).caption().orElse(null);
            case IMAGE -> ((ImageMessage) base).caption().orElse(null);
            case DOCUMENT -> ((DocumentMessage) base).caption().orElse(null);
            default -> null;
        });
    }

    private void extractAttachments(Message base, AttachmentType aType, MessageBuilder builder) {
        switch (aType) {
            case AUDIO -> builder.setAudio(((AudioMessage) base).audio());
            case VIDEO -> builder.setVideo(((VideoOrGifMessage) base).med());
            case IMAGE -> builder.setImage(((ImageMessage) base).image());
            case DOCUMENT -> builder.setDocument(((DocumentMessage) base).document());
            default -> throw new IllegalArgumentException("Unsupported attachment type: " + aType);
        }
    }

    private Map<Account, Set<String>> extractMembers(MessageInfo messageInfo) {
        return new HashMap<>();
    }
}
