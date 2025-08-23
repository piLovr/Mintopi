package com.github.pilovr.mintopi.domain.message.builder;

import com.github.pilovr.mintopi.domain.account.Account;
import com.github.pilovr.mintopi.domain.message.ExtendedMessage;
import com.github.pilovr.mintopi.domain.message.Message;
import com.github.pilovr.mintopi.domain.message.MessageType;
import com.github.pilovr.mintopi.domain.message.attachment.Attachment;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ExtendedMessageBuilder implements  MessageBuilder {
    private MessageType messageType;
    private String id;
    private Object payload;

    private String text;
    private List<Account> mentions;
    private Account quotedMessageSender;
    private Message quoted;
    private List<Attachment> attachments;


    public ExtendedMessageBuilder(MessageType messageType, String id, Object payload) {
        this.messageType = messageType;
        this.id = id;
        this.payload = payload;
    }

    public ExtendedMessageBuilder(ExtendedMessage message){
        this.text = message.getText();
        this.mentions = message.getMentions();
        this.quoted = message.getQuoted();
        this.attachments = message.getAttachments();
        this.messageType = message.getType();
        this.quotedMessageSender = message.getQuotedMessageSender();
        this.id = message.getId();
        this.payload = message.getPayload();
    }

    public ExtendedMessageBuilder(){

    }

    public ExtendedMessage build() {
        return new ExtendedMessage(this);
    }

    public ExtendedMessageBuilder text(String text) {
        this.text = text;
        return this;
    }

    public ExtendedMessageBuilder mentions(List<Account> mentions) {
        this.mentions = mentions;
        return this;
    }

    public ExtendedMessageBuilder quoted(Message quoted) {
        this.quoted = quoted;
        return this;
    }

    public ExtendedMessageBuilder attachments(List<Attachment> attachments) {
        this.attachments = attachments;
        return this;
    }

    public ExtendedMessageBuilder type(MessageType type) {
        this.messageType = type;
        return this;
    }

    public ExtendedMessageBuilder addAttachment(Attachment attachment) {
        if(attachments == null) {
            attachments = new ArrayList<>();
        }
        attachments.add(attachment);
        return this;
    }

    public ExtendedMessageBuilder quotedMessageSender(Account quotedMessageSender) {
        this.quotedMessageSender = quotedMessageSender;
        return this;
    }
}