package com.piLovr.messenger_interop_starter.decoder;

import com.piLovr.messenger_interop_starter.domain.event.Event;

public interface EventDecoder {
    <T> boolean supports(Class<T> inputType);
    <T> Event decode(T source);
}
