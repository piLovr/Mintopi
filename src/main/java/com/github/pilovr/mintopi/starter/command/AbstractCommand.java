package com.github.pilovr.mintopi.starter.command;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An abstract base class for {@link Command} implementations.
 * It provides default implementations for aliases, parent, and sub-command management.
 */
public abstract class AbstractCommand implements Command {

    private final Map<String, Command> subCommands = new HashMap<>();

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
    public void addSubCommand(Command subCommand) {
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
    public Command getSubCommand(String name) {
        return subCommands.get(name);
    }

    @Override
    public Map<String, Command> getSubCommands() {
        return Collections.unmodifiableMap(subCommands);
    }
}