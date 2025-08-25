package com.github.pilovr.mintopi.service.language;

public interface LanguageKeys {
    // Constants that must be implemented
    String COMMAND_NOT_FOUND = "command.notFound";

    // Access to required sections
    CommandHandler getCommandHandler();

    ExceptionMessages getExceptionMessages();

    interface ExceptionMessages {
        CommandExceptions getCommandExceptions();
        String getLanguageLoadException();

        interface CommandExceptions {
            String getCommandNotFound();
        }
    }

    // Define nested interfaces for sections

    interface CommandHandler {

    }
}