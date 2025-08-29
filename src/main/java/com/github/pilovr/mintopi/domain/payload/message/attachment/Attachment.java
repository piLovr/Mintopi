package com.github.pilovr.mintopi.domain.payload.message.attachment;

import com.github.pilovr.mintopi.client.Client;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
public sealed class Attachment permits Image {
    protected String mimeType;
    @Setter
    protected byte[] data;
    protected Object platformSpecificData;
}
