package com.github.pilovr.mintopi.subscriber.command;

import com.github.pilovr.mintopi.client.Client;
import com.github.pilovr.mintopi.config.MintopiProperties;
import com.github.pilovr.mintopi.domain.payload.message.CommandProperties;
import com.github.pilovr.mintopi.domain.room.Room;
import com.github.pilovr.mintopi.util.LevenshteinUtil;
import jakarta.annotation.PostConstruct;
import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Discovers, registers, and processes all {@link CommandBlueprint} beans in the application context.
 */
@Service
public class CommandHandler { //todo fixxen/überarbeiten

    private static final Logger log = LoggerFactory.getLogger(CommandHandler.class);


    private final Map<String, CommandBlueprint> rootCommands = new HashMap<>();
    private final List<CommandBlueprint> allCommands;

    private final Map<String, CommandBlueprint> emojiCommands = new HashMap<>();
    private final MintopiProperties.CommandHandler properties;
    private final MintopiProperties.CommandHandler.ErrorMessages errorMessages;

    @Autowired
    public CommandHandler(List<CommandBlueprint> allCommands, MintopiProperties properties) {
        this.allCommands = allCommands;
        this.properties = properties.getCommandHandler();
        this.errorMessages = this.properties.getErrorMessages();
    }


    /**
     * Initializes the command handler by registering all command beans.
     * This method is called automatically by Spring after the bean is created.
     */
    @PostConstruct
    public void registerCommands() {
        Map<String, CommandBlueprint> commandMap = new HashMap<>();
        // First pass: register all commands by their primary name for lookup.
        for (CommandBlueprint command : allCommands) {
            if (command.getName() == null || command.getName().isBlank()) {
                log.warn("Command bean of class {} has a null or empty name, skipping.", command.getClass().getName()); //todo this for name??
                continue;
            }
            if (commandMap.containsKey(command.getName())) {
                log.error("Duplicate command name detected: {}", command.getName());
                throw new IllegalStateException("Duplicate command name detected: " + command.getName());
            }
            commandMap.put(command.getName(), command);
        }

        // Second pass: build the tree structure.
        for (CommandBlueprint command : allCommands) {
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
                CommandBlueprint parent = commandMap.get(command.getParentName());
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

    private void registerRootCommand(CommandBlueprint command) {
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
    public void handle(ExtendedMessageEvent extendedMessageEvent) { //todo line-by-line produces infinite messages!!!
        String input = extendedMessageEvent.getMessage().getText();
        if (input == null || input.trim().isEmpty()) {
            return;
        }

        CommandProperties commandProperties;

        if (extendedMessageEvent.getCommandMessageProperties() == null) {
            if (properties.isSplitCommandByLines()) {
                commandProperties = extendedMessageEvent.getCommandMessageProperties(true);
                for (ExtendedMessageEvent event : commandProperties.refactorToMessageEvents()) {
                    handle(event);
                }
                return;
            } else {
                commandProperties = extendedMessageEvent.getCommandMessageProperties(false);
            }
        } else {
            commandProperties = extendedMessageEvent.getCommandMessageProperties();
        }

        Client client = extendedMessageEvent.getClient();
        client.getStore().getMessageDoorman().register(extendedMessageEvent.getSender().getId(), extendedMessageEvent.getRoom().getId()); //register user in doorman
        Room room = extendedMessageEvent.getRoom();

        String rootCommandName = commandProperties.getCommand();
        CommandBlueprint currentCommand = rootCommands.get(rootCommandName);
        int levenshteinThreshold = properties.getLevenshteinThreshold();
        int levenshteinSuggestionThreshold = properties.getLevenshteinSuggestionThreshold() + levenshteinThreshold;

        if (currentCommand == null) {
            if (levenshteinThreshold > 0) {
                Pair<Set<String>, Integer> suggestions = LevenshteinUtil.getClosestStrings(rootCommands.keySet(), rootCommandName, levenshteinSuggestionThreshold);
                if (!suggestions.getValue0().isEmpty()) {
                    String matchedName = suggestions.getValue0().iterator().next();
                    if (suggestions.getValue1() <= levenshteinThreshold) {
                        log.info("Assuming root command '{}' for input '{}'", matchedName, rootCommandName);
                        currentCommand = rootCommands.get(matchedName);
                    } else {
                        String suggestionText = String.join(", ", suggestions.getValue0());
                        String message = errorMessages.getClosestCommand().replace("{suggestions}", suggestionText).replace("{suggestion}", matchedName);
                        client.sendMessage(room, message);
                        return;
                    }
                } else {
                    log.warn("Unknown command: {}", rootCommandName);
                    client.sendMessage(room, errorMessages.getUnknownCommand().replace("{prefix}", commandProperties.getPrefix()));
                    return; //new CommandResultBuilder().withText("Unknown command: " + rootCommandName);
                }
            }
        }
        StringBuilder commandPath = new StringBuilder(currentCommand != null ? currentCommand.getName() : rootCommandName);
        while (commandProperties.getArgs().iterator().hasNext()) {
            String subCommandName = commandProperties.getArgs().iterator().next();
            assert currentCommand != null;
            Map<String, CommandBlueprint> subCommands = currentCommand.getSubCommands();
            CommandBlueprint subCommand = subCommands.get(subCommandName);

            if (subCommand != null) {
                currentCommand = subCommand;
                commandPath.append(" ").append(subCommandName);
                continue;
            }

            Pair<Set<String>, Integer> suggestions = LevenshteinUtil.getClosestStrings(subCommands.keySet(), subCommandName, levenshteinSuggestionThreshold);
            if (!suggestions.getValue0().isEmpty()) {
                String matchedName = suggestions.getValue0().iterator().next();
                if (suggestions.getValue1() <= levenshteinThreshold) {
                    log.info("Assuming subcommand '{}' for input '{}' under parent '{}'", matchedName, subCommandName, currentCommand.getName());
                    currentCommand = subCommands.get(matchedName);
                } else {
                    String suggestionText = String.join(", ", suggestions.getValue0());
                    String message = errorMessages.getClosestCommand().replace("{prefix}", commandProperties.getPrefix()).replace("{path}", commandPath).replace("{suggestions}", suggestionText).replace("{suggestion}", matchedName);
                    client.sendMessage(room, message);
                    return;
                }
            } else {
                log.warn("Unknown subcommand '{}' under path '{}'", subCommandName, commandPath);
                client.sendMessage(room, errorMessages.getUnknownCommand().replace("{prefix}", commandProperties.getPrefix()));
                return; //new CommandResultBuilder().withText("Unknown command: " + rootCommandName);
            }
        }

        assert currentCommand != null;
        MessageBuilder executed;
        try {
            executed = currentCommand.execute(extendedMessageEvent);
        } catch (RuntimeException e) {
            log.error("Error executing command '{}': {}", currentCommand.getName(), e.getMessage(), e);
            client.sendMessage(room, errorMessages.getExecutionError());
            return;
        }
        if (executed == null) {
            log.error("Command '{}' returned null result, this is a bug.", currentCommand.getName());
            client.sendMessage(room, errorMessages.getExecutionError());
            return;
        }

        client.sendMessage(room, executed.build());
    }

    public void handle(ReactionMessageEvent reactionMessageEvent) {
        String emoji = reactionMessageEvent.getReactionMessage().getReaction();
        CommandBlueprint command = emojiCommands.get(emoji);
        if (command != null) {
            Client client = reactionMessageEvent.getClient();
            client.getStore().getMessageDoorman().register(reactionMessageEvent.getSender().getId(), reactionMessageEvent.getRoom().getId()); //register user in doorman
            MessageBuilder executed;
            try {
                executed = command.execute(reactionMessageEvent);
            } catch (RuntimeException e) {
                log.error("Error executing command '{}': {}", command.getName(), e.getMessage(), e);
                client.sendMessage(reactionMessageEvent.getRoom(), errorMessages.getExecutionError());
                return;
            }
            if (executed == null) {
                log.error("Command '{}' returned null result, this is a bug.", command.getName());
                client.sendMessage(reactionMessageEvent.getRoom(), errorMessages.getExecutionError());
                return;
            }
            client.sendMessage(reactionMessageEvent.getRoom(), executed.build());
        }
    }
}