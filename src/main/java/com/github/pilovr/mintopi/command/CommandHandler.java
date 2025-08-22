package com.github.pilovr.mintopi.command;

import com.github.pilovr.mintopi.domain.event.ExtendedMessageEvent;
import com.github.pilovr.mintopi.domain.event.ReactionMessageEvent;
import com.github.pilovr.mintopi.domain.message.CommandResultBuilder;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Discovers, registers, and processes all {@link Command} beans in the application context.
 */
@Service
public class CommandHandler {

    private static final Logger log = LoggerFactory.getLogger(CommandHandler.class);

    private static final int LEVENSHTEIN_THRESHOLD = 2;

    private final Map<String, Command> rootCommands = new HashMap<>();
    private final List<Command> allCommands;

    private final Map<String, Command> emojiCommands = new HashMap<>();

    @Autowired
    public CommandHandler(List<Command> allCommands) {
        this.allCommands = allCommands;
    }

    /**
     * Initializes the command handler by registering all command beans.
     * This method is called automatically by Spring after the bean is created.
     */
    @PostConstruct
    public void registerCommands() {
        Map<String, Command> commandMap = new HashMap<>();
        // First pass: register all commands by their primary name for lookup.
        for (Command command : allCommands) {
            if (command.getName() == null || command.getName().isBlank()) {
                log.warn("Command bean of class {} has a null or empty name, skipping.", command.getClass().getName());
                continue;
            }
            if (commandMap.containsKey(command.getName())) {
                log.error("Duplicate command name detected: {}", command.getName());
                throw new IllegalStateException("Duplicate command name detected: " + command.getName());
            }
            commandMap.put(command.getName(), command);
        }

        // Second pass: build the tree structure.
        for (Command command : allCommands) {
            if (command.getName() == null || command.getName().isBlank()) {
                continue; // Skip already warned-about commands
            }
            for(String emoji : command.getEmojiAliases()){
                if(emojiCommands.containsKey(emoji)){
                    log.error("Duplicate emoji command detected: {}", emoji);
                    throw new IllegalStateException("Duplicate emoji command detected: " + emoji);
                }
                emojiCommands.put(emoji, command);
            }
            if (command.getParentName() != null) {
                Command parent = commandMap.get(command.getParentName());
                if (parent != null) {
                    parent.addSubCommand(command);
                    log.info("Registered subcommand '{}' for command '{}'", command.getName(), parent.getName());
                } else {
                    log.warn("Parent command '{}' not found for command '{}'. Registering as a root command.",
                            command.getParentName(), command.getName());
                    registerRootCommand(command);
                }
            } else {
                registerRootCommand(command);
            }
        }
    }

    private void registerRootCommand(Command command) {
        if (rootCommands.containsKey(command.getName())) {
            throw new IllegalStateException("Duplicate root command or alias detected: " + command.getName());
        }
        rootCommands.put(command.getName(), command);
        log.info("Registered root command: {}", command.getName());

        for (String alias : command.getAliases()) {
            if (rootCommands.containsKey(alias)) {
                throw new IllegalStateException("Duplicate root command or alias detected: " + alias);
            }
            rootCommands.put(alias, command);
            log.info("Registered alias '{}' for root command: {}", alias, command.getName());
        }
    }

    /**
     * Handles a raw input string, parses it, finds the corresponding command, and executes it.
     *
     * @param extendedMessageEvent The extendedMessageEvent to handle.
     */
    public CommandResultBuilder handle(ExtendedMessageEvent extendedMessageEvent) {
        String input = extendedMessageEvent.getMessage().getText();
        if (input == null || input.trim().isEmpty()) {
            return null;
        }

        String commandLine = input.startsWith("#") ? input.substring(1) : input;
        String[] parts = commandLine.trim().split("\\s+");
        if (parts.length == 0 || parts[0].isBlank()) {
            return null;
        }

        String rootCommandName = parts[0];
        Command currentCommand = rootCommands.get(rootCommandName);

        if (currentCommand == null) {
            Set<String> suggestions = LevenshteinUtil.getClosestStrings(rootCommands.keySet(), rootCommandName, LEVENSHTEIN_THRESHOLD);
            if (!suggestions.isEmpty()) {
                String matchedName = suggestions.iterator().next();
                log.info("Assuming root command '{}' for input '{}'", matchedName, rootCommandName);
                currentCommand = rootCommands.get(matchedName);
            } else {
                log.warn("Unknown command: {}", rootCommandName);
                return null; //new CommandResultBuilder().withText("Unknown command: " + rootCommandName);
            }
        }

        int currentIndex = 1;
        while (currentIndex < parts.length) {
            String subCommandName = parts[currentIndex];
            Map<String, Command> subCommands = currentCommand.getSubCommands();
            Command subCommand = subCommands.get(subCommandName);

            if (subCommand != null) {
                currentCommand = subCommand;
                currentIndex++;
                continue;
            }

            // If no direct match, try fuzzy matching
            Set<String> suggestions = LevenshteinUtil.getClosestStrings(subCommands.keySet(), subCommandName, LEVENSHTEIN_THRESHOLD);
            if (!suggestions.isEmpty()) {
                String matchedName = suggestions.iterator().next();
                log.info("Assuming subcommand '{}' for input '{}' under parent '{}'", matchedName, subCommandName, currentCommand.getName());
                currentCommand = subCommands.get(matchedName);
                currentIndex++;
            } else {
                log.info("No matching subcommand found for input '{}'", subCommandName);
                break;
            }
        }

        return currentCommand.execute(extendedMessageEvent);
    }

    public CommandResultBuilder handle(ReactionMessageEvent reactionMessageEvent) {
        String emoji = reactionMessageEvent.getReactionMessage().getReaction();
        Command command = emojiCommands.get(emoji);
        if (command != null) {
            return command.execute(reactionMessageEvent);
        }
        return null;
    }
}