package com.github.pilovr.mintopi.domain.payload.message.attachment;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public sealed abstract class Attachment permits Image {
    byte[] data;
}
