package com.github.pilovr.mintopi.starter.domain.message;


import com.github.pilovr.mintopi.starter.domain.account.Account;
import com.github.pilovr.mintopi.starter.domain.message.attachment.Attachment;
import lombok.Getter;

import java.util.List;

@Getter
public non-sealed class ExtendedMessage extends Message {
    private final String text;
    private final List<Account> mentions;
    private final ExtendedMessage quoted;
    private final List<Attachment> attachments;

    private int currentAttachment = 0;

    public ExtendedMessage(MessageType type, String id, Object payload, String text, List<Account> mentions, ExtendedMessage quoted, List<Attachment> attachments) {
        this.type = type;
        this.id = id;
        this.payload = payload;

        this.text = text;
        this.mentions = mentions;
        this.quoted = quoted;
        this.attachments = attachments;
    }

    public ExtendedMessage(ExtendedMessageBuilder builder) {
        this.type = builder.getMessageType();
        this.id = builder.getId();
        this.payload = builder.getPayload();

        this.text = builder.getText();
        this.mentions = builder.getMentions();
        this.quoted = builder.getQuoted();
        this.attachments = builder.getAttachments();
    }

    public Attachment cycleAttachments() {
        if(attachments == null || attachments.isEmpty()) {
            return null;
        }
        currentAttachment++;
        if(currentAttachment >= attachments.size()) {
            currentAttachment = 0;
        }
        return attachments.get(currentAttachment);
    }
}