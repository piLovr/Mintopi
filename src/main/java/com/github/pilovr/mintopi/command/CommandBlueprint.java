package com.github.pilovr.mintopi.command;

import com.github.pilovr.mintopi.domain.event.MessageEvent;
import com.github.pilovr.mintopi.domain.message.Message;
import org.reactivestreams.Publisher;

import java.util.List;
import java.util.Map;

/**
 * Represents a command that can be executed.
 * <p>
 * Commands are structured in a tree. A command can have a parent and sub-commands.
 * If a command has no parent, it is a root command.
 */
public interface CommandBlueprint<T extends MessageEvent<M, ?, ?>, M extends Message> {
    String getName();

    List<String> getAliases();
    List<String> getEmojiAliases();


    String getParentName();
    void addSubCommand(CommandBlueprint<T,M> subCommand);
    CommandBlueprint<T,M> getSubCommand(String name);
    Map<String, CommandBlueprint<T,M>> getSubCommands();


    Publisher<Message> execute(CommandScope<T, M> context);
}