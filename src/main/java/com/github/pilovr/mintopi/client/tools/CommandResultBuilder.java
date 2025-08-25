package com.github.pilovr.mintopi.client.tools;

import com.github.pilovr.mintopi.command.CommandScope;
import com.github.pilovr.mintopi.domain.account.Account;
import com.github.pilovr.mintopi.domain.message.Message;
import com.github.pilovr.mintopi.domain.message.builder.ExtendedMessageBuilder;
import com.github.pilovr.mintopi.domain.message.builder.MessageBuilder;
import com.github.pilovr.mintopi.domain.message.builder.ReactionMessageBuilder;
import lombok.Getter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CommandResultBuilder {
    private CommandScope commandContext;
    private String commandName;
    private String language;
    private ResponseType responseType = ResponseType.SHORT;
    private Map<String,String> placeholders = new HashMap<>();

    private String key;
    private boolean castToOneLiner = false;

    private MessageBuilder messageBuilder;


    public static enum ResponseType {
        REACTION("emoji", false),
        SHORT("short", false),
        LONG("long", false),
        SHORT_QUOTED("long", true),
        LONG_QUOTED("long", true),
        NON_TEXT("", false);

        @Getter
        private final String jsonAlias;
        @Getter
        private final boolean quoted;

        public ResponseType castToOneLiner() {
            return switch (this) {
                case REACTION, SHORT, SHORT_QUOTED -> SHORT;
                case LONG, LONG_QUOTED -> LONG;
                default -> NON_TEXT;
            };
        }

        ResponseType(String jsonAlias, boolean quoted) {
            this.jsonAlias = jsonAlias;
            this.quoted = quoted;
        }


    }

    public CommandResultBuilder(CommandScope commandContext, String commandName, boolean oneLiner) {
        this.commandContext = commandContext;
        this.commandName = commandName;
        this.language = commandContext.getLanguage();
    }
    private CommandResultBuilder addContext(CommandScope context){
        this.commandContext = context;
        return this;
    }
    private CommandResultBuilder castToOneLiner(){
        this.responseType = this.responseType.castToOneLiner();
        this.castToOneLiner = true;
        return this;
    }

    public CommandResultBuilder setLanguage(String language){
        this.language = language;
        return this;
    }

    public CommandResultBuilder setResponseType(ResponseType responseType){
        if(this.responseType == ResponseType.NON_TEXT){
            return this;
        }
        this.responseType = responseType;
        return this;
    }

    public CommandResultBuilder addPlaceholder(String key, String value){
        this.placeholders.put(key, value);
        return this;
    }

    public CommandResultBuilder setKey(String key){
        this.key = key;
        return this;
    }

    public CommandResultBuilder setMessageBuilder(MessageBuilder messageBuilder){
        this.messageBuilder = messageBuilder;
        this.responseType = ResponseType.NON_TEXT;
        return this;
    }

    public Message buildAndSend(){
        return commandContext.getEvent().getClient().sendMessage(commandContext.getEvent().getRoom(), generateBuilder().build());
    }

    public MessageBuilder generateBuilder(){
        if(responseType == ResponseType.NON_TEXT && messageBuilder != null){
            return messageBuilder;
        }
        this.responseType = castToOneLiner ? responseType.castToOneLiner() : responseType;
        String text = I18nProvider.getText(language, "commands", commandName, "response", key, responseType.getJsonAlias());
        if(text == null) return null;

        if(responseType == ResponseType.REACTION){
            return new ReactionMessageBuilder().setReaction(text).setMessage(commandContext.getEvent().getMessage());
        }

        for(Map.Entry<String,String> entry : placeholders.entrySet()){
            text = text.replace("<" + entry.getKey() + ">", entry.getValue());
        }



        return generateMessageBuilderWithMentions(text);
    }

    public String replaceBasicPlaceholders(String text){
        if(text == null) return null;
        return text.replace("<user>", commandContext.getEvent().getSender().getPushName());
        //todo...
    }

    public ExtendedMessageBuilder generateMessageBuilderWithMentions(String text){
        //extract all strings like @1234567890 -> 1234567890 without replacing them
        //find accounts with these numbers and add them to mentions
        Set<Account> mentions = new HashSet<>();
        for(String word : text.split(" ")){
            if(word.startsWith("@") && word.length() > 1){
                String number = word.substring(1).replaceAll("[^0-9]", "");
                Account account = commandContext.getEvent().getClient().getStore().getOrCreateAccount(number, commandContext.getEvent().getPlatform(), null);
                if(account != null){
                    mentions.add(account);
                }
            }
        }
        return new ExtendedMessageBuilder()
                .text(text)
                .mentions(mentions.isEmpty() ? null : mentions.stream().toList())
                .quoted(responseType.isQuoted() ? commandContext.getEvent().getMessage() : null);
    }
}
