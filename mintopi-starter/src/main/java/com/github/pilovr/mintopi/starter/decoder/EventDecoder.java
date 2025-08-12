package com.github.pilovr.mintopi.starter.decoder;

import com.github.pilovr.mintopi.core.event.Event;

public interface EventDecoder {
    <T> boolean supports(Class<T> inputType);
    <T> Event decode(T source);
}
