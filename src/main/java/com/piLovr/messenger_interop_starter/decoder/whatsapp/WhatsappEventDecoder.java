package com.piLovr.messenger_interop_starter.decoder.whatsapp;

import com.piLovr.messenger_interop_starter.domain.event.MessageEvent;
import com.piLovr.messenger_interop_starter.domain.message.MessageBuilder;
import com.piLovr.messenger_interop_starter.domain.message.attachment.AttachmentType;
import com.piLovr.messenger_interop_starter.domain.message.types.MessageType;
import it.auties.whatsapp.model.info.MessageInfo;
import com.piLovr.messenger_interop_starter.decoder.MultiEventDecoder;

public class WhatsappEventDecoder extends MultiEventDecoder {
    public WhatsappEventDecoder() {
        this.register(MessageInfo.class, this::decodeMessage);
    }

    private MessageEvent decodeMessage(MessageInfo messageInfo) {
        AttachmentType attachmentType = WhatsappMessageTypeTranslator.translateAttachment(messageInfo.message().type());
        MessageType messageType = attachmentType == null ? WhatsappMessageTypeTranslator.translate(messageInfo.message().type()) : MessageType.TEXT_WITH_ATTACHMENTS;
        MessageBuilder builder = new MessageBuilder(messageType);

        return new MessageEvent(
                messageInfo.id(),
                null,
                null,
                null,
                messageInfo,
                builder.build()
        );
    }
}
