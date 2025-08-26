package com.github.pilovr.mintopi.domain.payload.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor @Builder @Getter
public non-sealed class ReactionMessagePayload extends MessagePayload {
    private final String reaction;
    private final MessagePayload message;
}
