package com.github.pilovr.mintopi.codec;


import com.github.pilovr.mintopi.domain.event.Event;

public interface Codec {
    // Decode methods
    <T> boolean supportsDecoding(Class<T> inputType);
    <T> Event decode(T source);

    // Encode methods
    <T> boolean supportsEncoding(Class<T> outputType);
    <T> T encode(Event source);

}
