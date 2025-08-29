package com.github.pilovr.mintopi.codec;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import com.github.pilovr.mintopi.domain.event.EventContext;
import com.github.pilovr.mintopi.domain.payload.Payload;
import org.jetbrains.annotations.NotNull;

public abstract class MultiCodec implements Codec{
    private final Map<Class<?>, Function<?,  EventContext>> decodeRegistry = new HashMap<>();
    private final Map<Class<?>, Function<?,  EventContext>> encodeRegistry = new HashMap<>();

    public <T> void registerDecode(@NotNull Class<T> inputType, @NotNull Function<T, EventContext> decoder) {
        decodeRegistry.put(inputType, decoder);
    }

    public <T> void registerEncode(@NotNull Class<T> inputType, @NotNull Function<T, EventContext> encoder) {
        encodeRegistry.put(inputType, encoder);
    }

    @Override
    public <T> EventContext decode(T input) {
        if(input == null) {
            return null;
        }
        Function<T, EventContext> decoder = (Function<T, EventContext>) decodeRegistry.get(input.getClass());
        if (decoder == null) {
            throw new IllegalArgumentException("No decoder for input type " + input.getClass());
        }
        return decoder.apply(input);
    }

    @Override
    public <T> EventContext encode(T input) {
        if(input == null) {
            return null;
        }
        Function<T, EventContext> encoder = (Function<T, EventContext>) encodeRegistry.get(input.getClass());
        if (encoder == null) {
            throw new IllegalArgumentException("No decoder for input type " + input.getClass());
        }
        return encoder.apply(input);
    }

    @Override
    public <T> boolean supportsDecoding(Class<T> inputType) {
        return decodeRegistry.get(inputType) != null;
    }

    @Override
    public <T> boolean supportsEncoding(Class<T> outputType) {
        return encodeRegistry.get(outputType) != null;
    }
}
