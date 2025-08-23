package com.github.pilovr.mintopi.domain.message;


import com.github.pilovr.mintopi.domain.account.Account;
import com.github.pilovr.mintopi.domain.message.attachment.Attachment;
import com.github.pilovr.mintopi.domain.message.builder.ExtendedMessageBuilder;
import lombok.Getter;

import java.util.List;

@Getter
public non-sealed class ExtendedMessage extends Message {
    private final String text;
    private final List<Account> mentions;
    private final Account quotedMessageSender;
    private final Message quoted;
    private final List<Attachment> attachments;

    private int currentAttachment = 0;

    public ExtendedMessage(MessageType type, String id, Object payload, String text, List<Account> mentions, Account quotedMessageSender, ExtendedMessage quoted, List<Attachment> attachments) {
        this.quotedMessageSender = quotedMessageSender;
        this.type = type;
        this.id = id;
        this.payload = payload;

        this.text = text;
        this.mentions = mentions;
        this.quoted = quoted;
        this.attachments = attachments;
    }

    public ExtendedMessage(ExtendedMessageBuilder builder) {
        this.quotedMessageSender = builder.getQuotedMessageSender();
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