package com.github.pilovr.mintopi.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix="mintopi")
@Getter
public class MintopiProperties {
    private final String internationalizationFolderPath = "src/main/resources/i18n/";
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
    }


}
