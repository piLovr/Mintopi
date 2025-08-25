package com.github.pilovr.mintopi.command;

import com.github.pilovr.mintopi.domain.event.MessageEvent;
import com.github.pilovr.mintopi.domain.message.Message;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class CreditsCommand<T extends MessageEvent<M, ?, ?>, M extends Message> extends Command<T,M> {
    @Override
    public String getName() {
        return "";
    }

    @Override
    public Flux<Message> execute(CommandScope<T,M> context) {
        return Flux.just(context.getEvent().getClient().sendMessage(context.getEvent().getRoom(), "Hi"));

        return Flux.concat(
                Mono.just(() -> new CommandEvent.Info(
                        "Deine Position: " + queue.addTask(ctx)
                )),
                Mono.fromSupplier(() -> new CommandEvent.Info("⏳ Konvertiere...")),
                Mono.fromSupplier(() -> new CommandEvent.Sticker(
                        doStickerConversion(ctx) // dein fertiges Objekt
                ))
        );
    }
}
