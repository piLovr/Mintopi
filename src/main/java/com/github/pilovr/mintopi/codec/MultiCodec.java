package com.github.pilovr.mintopi.codec;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

import com.github.pilovr.mintopi.client.Client;
import org.jetbrains.annotations.NotNull;

public abstract class MultiCodec {
    private final Map<Class<?>, Function<?,  ? extends Event>> registry = new HashMap<>();

    public <T> void register(@NotNull Class<T> inputType, @NotNull BiFunction<Client, T, ? extends Event> decoder) {
        registry.put(inputType, decoder);
    }

    @SuppressWarnings("unchecked")
    public <T> Event decode(T input) {
        if(input == null) {
            return null;
        }
        BiFunction<Client, T, ? extends Event> decoder = (BiFunction<Client, T, ? extends Event>) registry.get(input.getClass());
        if (decoder == null) {
            throw new IllegalArgumentException("No decoder for input type " + input.getClass());
        }
        return decoder.apply(client, input);
    }

    public <T> Event encode(T input) {
        if(input == null) {
            return null;
        }
        BiFunction<Client, T, ? extends Event> decoder = (BiFunction<Client, T, ? extends Event>) registry.get(input.getClass());
        if (decoder == null) {
            throw new IllegalArgumentException("No decoder for input type " + input.getClass());
        }
        return decoder.apply(client, input);
    }
}
