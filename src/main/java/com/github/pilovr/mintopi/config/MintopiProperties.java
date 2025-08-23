package com.github.pilovr.mintopi.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix="mintopi")
@Getter
public class MintopiProperties {
    private final boolean restController = true;
    private final SpamHandler spamHandler =  new SpamHandler();
    private final Store store = new Store();
    private final CommandHandler  commandHandler = new CommandHandler();

    @Getter
    public static class SpamHandler {
        private final int messageCooldown = 5; //seconds
        private final String messageCooldownParam = "perRoom"; //perMember or perRoom
        private final int spamThreshold = 10; //triggered at x messages in the last minute
        private final int timeoutDuration = 15; //timeout in minutes after threshold is hit

        private final boolean decodeAnyways = false;
    }

    @Getter
    public static class Store {
        private final boolean persistent = false;
    }


    @Getter
    public static class CommandHandler {

        private final ErrorMessages errorMessages = new ErrorMessages();
        private final boolean autoRegisterCommands = true;
        private final boolean autoExecuteCommands = true;
        private final String defaultPrefix = ".";
        //private final boolean generateCommandProperties = true;
        private final boolean splitCommandByLines = true;

        private final boolean enableEmojiCommands = true;
        private final int levenshteinThreshold = 2;
        private final int levenshteinSuggestionThreshold = 2;
        private final boolean generateCommandListCommand = true;
        private final boolean generateHelpCommand = true;

        @Getter
        public static class ErrorMessages {
            private final String unknownCommand = "Unknown command. Type {prefix}help for a list of commands.";
            private final String unknownSubcommand = "Unknown subcommand. Type {prefix}help {command} for a list of subcommands.";
            private final String closestCommand = "Unknown command. Did you mean one of [{suggestions}]?";
            private final String closestSubcommand = "Unknown subcommand in {prefix}{path}. Did you mean one of [{suggestions}]?";
            private final String noPermission = "You do not have permission to execute this command.";
            private final String invalidContext = "This command cannot be used in this context.";
            private final String executionError = "An error occurred while executing the command.";
            private final String rateLimited = "You are being rate limited. Please wait before trying again.";
            private final String timeouted = "You are currently timed out from using commands. Please wait before trying again.";
        }
    }


}
