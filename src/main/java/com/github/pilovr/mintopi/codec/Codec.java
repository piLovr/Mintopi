package com.github.pilovr.mintopi.codec;


import com.github.pilovr.mintopi.domain.event.EventContext;
import com.github.pilovr.mintopi.domain.payload.Payload;

public interface Codec {
    // Decode methods
    <T> boolean supportsDecoding(Class<T> inputType);

    // Encode methods
    <T> boolean supportsEncoding(Class<T> outputType);

    <T> EventContext decode(T input);

    <T> EventContext encode(T input);
}
