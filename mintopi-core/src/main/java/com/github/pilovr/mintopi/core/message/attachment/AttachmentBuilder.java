package com.github.pilovr.mintopi.core.message.attachment;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AttachmentBuilder {
    private AttachmentType type;

    public AttachmentBuilder(AttachmentType type) {
        this.type = type;
    }
}
