package com.github.pilovr.mintopi.domain.payload.message;

import com.github.pilovr.mintopi.domain.payload.Payload;

public sealed abstract class MessagePayload extends Payload permits ReactionMessagePayload, TextMessagePayload {

}
