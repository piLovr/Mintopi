package com.github.pilovr.mintopi.command;

import com.github.pilovr.mintopi.client.tools.CommandResultBuilder;
import com.github.pilovr.mintopi.domain.event.MessageEvent;
import com.github.pilovr.mintopi.domain.message.Message;
import lombok.Getter;

@Getter
public class CommandScope<T extends MessageEvent<M, ?, ?>, M extends  Message> { //todo nochmal überdenken
    private final T event;
    private String language;
    private final CommandResultPublisher publisher;
    private final CommandResultBuilder resultBuilder;

    public CommandScope(T event) {
        this.event = event;
        this.publisher = new CommandResultPublisher();
        this.resultBuilder = new CommandResultBuilder(this, "", false);
    }
}
