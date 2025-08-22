package com.github.pilovr.mintopi.command;

import com.github.pilovr.mintopi.domain.event.ExtendedMessageEvent;
import com.github.pilovr.mintopi.domain.event.ReactionMessageEvent;
import com.github.pilovr.mintopi.domain.message.builder.MessageBuilder;

import java.util.List;
import java.util.Map;

/**
 * Represents a command that can be executed.
 * <p>
 * Commands are structured in a tree. A command can have a parent and sub-commands.
 * If a command has no parent, it is a root command.
 */
public interface Command {

    /**
     * @return The primary name of the command. Must be unique among its siblings.
     */
    String getName();

    /**
     * @return A list of alternative names for the command. Must be unique among its siblings.
     */
    List<String> getAliases();

    List<String> getEmojiAliases();

    /**
     * @return The name of the parent command. If null, this is a root command.
     */
    String getParentName();

    /**
     * Executes the command's logic.
     *
     * @param extendedMessageEvent The message event passed to the command.
     */
    MessageBuilder execute(ExtendedMessageEvent extendedMessageEvent);
    MessageBuilder execute(ReactionMessageEvent reactionMessageEvent);

    // --- Methods for managing the command tree ---

    void addSubCommand(Command subCommand);
    Command getSubCommand(String name);
    Map<String, Command> getSubCommands();
}