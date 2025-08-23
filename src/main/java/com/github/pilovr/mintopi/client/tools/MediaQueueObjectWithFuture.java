package com.github.pilovr.mintopi.client.tools;

import com.github.pilovr.mintopi.domain.event.ExtendedMessageEvent;
import com.github.pilovr.mintopi.domain.message.attachment.AttachmentType;

import java.util.concurrent.CompletableFuture;

public record MediaQueueObjectWithFuture(AttachmentType targetType, ExtendedMessageEvent messageEvent, int attachmentIndex, int additionalTimeout, CompletableFuture<byte[]> future){
    public AttachmentType originType(){
        return messageEvent.getMessage().getAttachments().get(attachmentIndex).getType();
    }
}
