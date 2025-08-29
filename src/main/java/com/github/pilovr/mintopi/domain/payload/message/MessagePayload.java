package com.github.pilovr.mintopi.domain.payload.message;

import com.github.pilovr.mintopi.domain.account.Account;
import com.github.pilovr.mintopi.domain.payload.Payload;
import com.github.pilovr.mintopi.domain.room.Room;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public sealed abstract class MessagePayload extends Payload permits ReactionMessagePayload, TextMessagePayload {

}
