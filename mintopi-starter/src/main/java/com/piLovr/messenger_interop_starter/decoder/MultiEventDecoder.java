package com.piLovr.messenger_interop_starter.decoder;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import com.piLovr.messenger_interop_starter.domain.event.Event;
import org.jetbrains.annotations.NotNull;

public class MultiEventDecoder {
    private final Map<Class<?>, Function<?, ? extends Event>> registry = new HashMap<>();

    public <T> void register(@NotNull Class<T> inputType, @NotNull Function<T, ? extends Event> decoder) {
        registry.put(inputType, decoder);
    }

    @SuppressWarnings("unchecked")
    public <T> Event decode(@NotNull T input) {
        Function<T, ? extends Event> decoder = (Function<T, ? extends Event>) registry.get(input.getClass());
        if (decoder == null) {
            throw new IllegalArgumentException("No decoder for input type " + input.getClass());
        }
        return decoder.apply(input);
    }
}
