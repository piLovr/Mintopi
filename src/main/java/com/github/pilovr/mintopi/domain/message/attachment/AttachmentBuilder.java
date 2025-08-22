package com.github.pilovr.mintopi.domain.message.attachment;

import lombok.Getter;

@Getter
public class AttachmentBuilder {
    private AttachmentType type;
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

    public AttachmentBuilder(AttachmentType type) {
        this.type = type;
    }

    public AttachmentBuilder mediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
        return this;
    }

    public AttachmentBuilder duration(int duration) {
        this.duration = duration;
        return this;
    }

    public AttachmentBuilder width(int width) {
        this.width = width;
        return this;
    }

    public AttachmentBuilder height(int height) {
        this.height = height;
        return this;
    }

    public AttachmentBuilder thumbnail(byte[] thumbnail) {
        this.thumbnail = thumbnail;
        return this;
    }

    public AttachmentBuilder mimeType(String mimeType) {
        this.mimeType = mimeType;
        return this;
    }

    public AttachmentBuilder mediaKey(byte[] mediaKey) {
        this.mediaKey = mediaKey;
        return this;
    }

    public AttachmentBuilder mediaEncryptedSha256(byte[] mediaEncryptedSha256) {
        this.mediaEncryptedSha256 = mediaEncryptedSha256;
        return this;
    }

    public AttachmentBuilder mediaSha256(byte[] mediaSha256) {
        this.mediaSha256 = mediaSha256;
        return this;
    }

    public AttachmentBuilder streamingSidecar(byte[] streamingSidecar) {
        this.streamingSidecar = streamingSidecar;
        return this;
    }

    public AttachmentBuilder mediaSize(int mediaSize) {
        this.mediaSize = mediaSize;
        return this;
    }

    public AttachmentBuilder mediaDirectPath(String mediaDirectPath) {
        this.mediaDirectPath = mediaDirectPath;
        return this;
    }

    public AttachmentBuilder downloadableMedia(Object downloadableMedia) {
        this.downloadableMedia = downloadableMedia;
        return this;
    }

    public Attachment build() {
        //Generate all values by byte[]
        if(downloadedMedia != null && downloadableMedia == null) {
            //todo???
        }
        return new Attachment(this);
    }

    public AttachmentBuilder downloadedMedia(byte[] downloadedMedia) {
        this.downloadedMedia = downloadedMedia;
        return this;
    }
}
