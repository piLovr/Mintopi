package com.piLovr.messenger_interop_starter.domain.message.attachment;

import lombok.Getter;

@Getter
public abstract class Attachment {
    private String mediaUrl;
    private int duration;
    private int width;
    private int height;
    private byte[] thumbnail;
    private String mimeType;
    private byte[] mediaKey;
    private byte[] mediaEncryptedSha256;
    private byte[] mediaSha256;
    private byte[] streamingSidecar;
    private int mediaSize;
    private String mediaDirectPath;

    private Object downloadableMedia;

    private byte[] downloadedMedia;
    Attachment(AttachmentBuilder builder) {
        // Initialize common properties if any
    }
}
