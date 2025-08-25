package com.github.pilovr.mintopi.command;

import com.github.pilovr.mintopi.domain.event.MessageEvent;
import com.github.pilovr.mintopi.domain.message.Message;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Flow;

/**
 * An abstract base class for {@link CommandBlueprint} implementations.
 * It provides default implementations for aliases, parent, and sub-command management.
 */
public abstract class Command<T extends MessageEvent<M, ?, ?>, M extends  Message> implements CommandBlueprint<T,M> {
    private final Map<String, CommandBlueprint<T,M>> subCommands = new HashMap<>();

    @Override
    public List<String> getAliases() {
        return Collections.emptyList(); // Default to no aliases
    }

    @Override
    public List<String> getEmojiAliases() {
        return Collections.emptyList(); // Default to no emoji-aliases
    }

    @Override
    public String getParentName() {
        return null; // Default to a root command
    }

    @Override
    public void addSubCommand(CommandBlueprint<T,M> subCommand) {
        if (subCommands.containsKey(subCommand.getName())) {
            throw new IllegalStateException("Duplicate subcommand name '" + subCommand.getName() + "' for command '" + getName() + "'");
        }
        subCommands.put(subCommand.getName(), subCommand);

        for (String alias : subCommand.getAliases()) {
            if (subCommands.containsKey(alias)) {
                throw new IllegalStateException("Duplicate subcommand alias '" + alias + "' for command '" + getName() + "'");
            }
            subCommands.put(alias, subCommand);
        }
    }

    @Override
    public CommandBlueprint<T,M> getSubCommand(String name) {
        return subCommands.get(name);
    }

    @Override
    public Map<String, CommandBlueprint<T,M>> getSubCommands() {
        return Collections.unmodifiableMap(subCommands);
    }
}