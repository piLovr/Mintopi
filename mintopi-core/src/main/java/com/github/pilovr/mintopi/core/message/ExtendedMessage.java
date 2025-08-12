package com.github.pilovr.mintopi.core.message;


import com.github.pilovr.mintopi.core.message.attachment.Attachment;
import com.github.pilovr.mintopi.core.account.Account;
import lombok.Getter;

import java.util.List;

@Getter
public class ExtendedMessage extends Message {
    private String text;
    private List<Account> mentions;
    private ExtendedMessage quoted;
    private List<Attachment> attachments;

    public ExtendedMessage(String text, List<Account> mentions, ExtendedMessage quoted, List<Attachment> attachments) {
        this.text = text;
        this.mentions = mentions;
        this.quoted = quoted;
        this.attachments = attachments;
    }

    public ExtendedMessage(MessageBuilder builder) {
        this.text = builder.getText();
        this.mentions = builder.getMentions();
        this.quoted = builder.getQuoted();
        this.attachments = builder.getAttachments();
    }
}