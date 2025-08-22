package com.github.pilovr.mintopi.starter.domain.message.attachment;

import lombok.Getter;
import lombok.Setter;

@Getter
public abstract class Attachment {
    private final String mediaUrl;
    private final int duration;
    private final int width;
    private final int height;
    private final byte[] thumbnail;
    private final String mimeType;
    private final byte[] mediaKey;
    private final byte[] mediaEncryptedSha256;
    private final byte[] mediaSha256;
    private final byte[] streamingSidecar;
    private final int mediaSize;
    private final String mediaDirectPath;

    private final Object downloadableMedia;
    @Setter
    private byte[] downloadedMedia;
    Attachment(AttachmentBuilder builder) {
        this.mediaUrl = builder.getMediaUrl();
        this.duration = builder.getDuration();
        this.width = builder.getWidth();
        this.height = builder.getHeight();
        this.thumbnail = builder.getThumbnail();
        this.mimeType = builder.getMimeType();
        this.mediaKey = builder.getMediaKey();
        this.mediaEncryptedSha256 = builder.getMediaEncryptedSha256();
        this.mediaSha256 = builder.getMediaSha256();
        this.streamingSidecar = builder.getStreamingSidecar();
        this.mediaSize = builder.getMediaSize();
        this.mediaDirectPath = builder.getMediaDirectPath();
        this.downloadableMedia = builder.getDownloadableMedia();
        this.downloadedMedia = null; // Initially null, can be set later after download
    }
}
