package com.github.pilovr.mintopi.decoder;


import com.github.pilovr.mintopi.domain.event.Event;

public interface EventDecoder {
    <T> boolean supports(Class<T> inputType);
    <T> Event decode(T source);
}
