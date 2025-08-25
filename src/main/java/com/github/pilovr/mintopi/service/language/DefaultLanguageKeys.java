package com.github.pilovr.mintopi.service.language;

import lombok.Getter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnMissingBean(LanguageKeys.class)
public class DefaultLanguageKeys implements LanguageKeys {
    private final CommandHandlerImpl commandHandler = new CommandHandlerImpl();
    @Override
    public CommandHandler getCommandHandler() {
        return null;
    }

    @Override
    public ExceptionMessages getExceptionMessages() {
        return null;
    }

    @Getter
    public static class ExceptionMessagesImpl implements ExceptionMessages {
        private final CommandExceptionsImpl commandErrors = new CommandExceptionsImpl();
        @Getter
        private static final String languageLoadException = "language.loadException";

        @Override
        public CommandExceptions getCommandExceptions() {
            return commandErrors;
        }

        @Getter
        public class CommandExceptionsImpl implements CommandExceptions {
            @Getter public static final String commandNotFound = "command.notFound";
            public static final String SUBCOMMAND_NOT_FOUND = "command.subcommandNotFound";
            public static final String NO_PERMISSION = "command.noPermission";
            public static final String EXECUTION_ERROR = "command.executionError";
            public static final String RATE_LIMITED = "command.rateLimited";
            public static final String TIMEOUTED = "command.timeouted";
        }
    }
    @Getter
    public static class CommandHandlerImpl implements CommandHandler {
        public static final String UNKNOWN_COMMAND = "command.unknown";
        public static final String UNKNOWN_SUBCOMMAND = "command.unknownSubcommand";
        public static final String CLOSEST_COMMAND = "command.closest";
        public static final String CLOSEST_SUBCOMMAND = "command.closestSubcommand";
        public static final String NO_PERMISSION = "command.noPermission";
        public static final String EXECUTION_ERROR = "command.executionError";
        public static final String RATE_LIMITED = "command.rateLimited";
        public static final String TIMEOUTED = "command.timeouted";
    }

    // You can access constants either way:
    // DefaultLanguageKeys.CommonImpl.ERROR (static access)
    // languageKeysInstance.getCommon().ERROR (instance access)
}