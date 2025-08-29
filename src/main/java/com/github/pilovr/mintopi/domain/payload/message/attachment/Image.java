package com.github.pilovr.mintopi.domain.payload.message.attachment;

import lombok.AllArgsConstructor;

public final class Image extends Attachment{

    public Image(String mimeType, byte[] data) {
        super(mimeType, data);
    }
}
