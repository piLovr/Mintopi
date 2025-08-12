package com.piLovr.messenger_interop_starter.domain.message.attachment;

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
