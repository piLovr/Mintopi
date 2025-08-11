package com.piLovr.messenger_interop_starter.domain.message;


import com.piLovr.messenger_interop_starter.domain.account.Account;
import com.piLovr.messenger_interop_starter.domain.message.attachment.Attachment;
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

    ExtendedMessage(MessageBuilder builder) {
        this.text = builder.getText();
        this.mentions = builder.getMentions();
        this.quoted = builder.getQuoted();
        this.attachments = builder.getAttachments();
    }
}