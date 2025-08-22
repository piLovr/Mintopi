package com.github.pilovr.mintopi.client.tools;

import com.github.pilovr.mintopi.domain.event.ExtendedMessageEvent;
import com.github.pilovr.mintopi.domain.message.attachment.AttachmentType;
public record MediaQueueObject(AttachmentType targetType, ExtendedMessageEvent messageEvent, int attachmentIndex, int additionalTimeout){
    public AttachmentType originType(){
        return messageEvent.getMessage().getAttachments().get(attachmentIndex).getType();
    }
}
