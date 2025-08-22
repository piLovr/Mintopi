package com.github.pilovr.mintopi.starter.decoder;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

import com.github.pilovr.mintopi.starter.domain.common.Client;
import com.github.pilovr.mintopi.starter.domain.event.Event;
import org.jetbrains.annotations.NotNull;

public class MultiEventDecoder {
    private final Map<Class<?>, BiFunction<Client, ?,  ? extends Event>> registry = new HashMap<>();

    public <T> void register(@NotNull Class<T> inputType, @NotNull BiFunction<Client, T, ? extends Event> decoder) {
        registry.put(inputType, decoder);
    }

    @SuppressWarnings("unchecked")
    public <T> Event decode(Client client, @NotNull T input) {
        BiFunction<Client, T, ? extends Event> decoder = (BiFunction<Client, T, ? extends Event>) registry.get(input.getClass());
        if (decoder == null) {
            throw new IllegalArgumentException("No decoder for input type " + input.getClass());
        }
        return decoder.apply(client, input);
    }
}
