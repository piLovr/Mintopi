package com.github.pilovr.mintopi.decoder;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

import com.github.pilovr.mintopi.client.Client;
import com.github.pilovr.mintopi.domain.event.Event;
import org.jetbrains.annotations.NotNull;

public abstract class MultiEventDecoder {
    private final Map<Class<?>, BiFunction<Client, ?,  ? extends Event>> registry = new HashMap<>();

    public <T> void register(@NotNull Class<T> inputType, @NotNull BiFunction<Client, T, ? extends Event> decoder) {
        registry.put(inputType, decoder);
    }

    @SuppressWarnings("unchecked")
    public <T> Event decode(Client client, T input) {
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
