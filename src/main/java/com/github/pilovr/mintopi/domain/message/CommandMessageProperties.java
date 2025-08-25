package com.github.pilovr.mintopi.domain.message;

import com.github.pilovr.mintopi.client.Platform;
import com.github.pilovr.mintopi.domain.account.Account;
import com.github.pilovr.mintopi.domain.event.ExtendedMessageEvent;
import com.github.pilovr.mintopi.domain.message.builder.ExtendedMessageBuilder;
import com.github.pilovr.mintopi.domain.room.Room;
import lombok.AccessLevel;
import lombok.Getter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
public class CommandMessageProperties<R extends Room, A extends Account> {
    private ExtendedMessageEvent<R,A> originalMessageEvent;
    private final String originalText;

    private final String command;
    private final Set<String> args;
    private final Set<Character> flags;
    private final Set<String> argsLowerCase;

    private final Set<CommandMessageProperties<R,A>> childs = new HashSet<>();
    private final String prefix; //All special characters at the start of the first word
    private final Set<Account> who;

    private boolean usesHideTags;
    @Getter(AccessLevel.NONE)
    private final List<Account> mentionsOfLine;

    public static CommandMessageProperties of(ExtendedMessageEvent event) {
        CommandMessageProperties c = of((ExtendedMessage) event.getMessage(), event.getPlatform());
        c.originalMessageEvent = event;
        return c;
    }

    private static CommandMessageProperties of(ExtendedMessage message, Platform platform) {
        String text = message.getText();
        CommandMessageProperties res = new CommandMessageProperties(text, message.getMentions(), message.getQuotedMessageSender(), platform);
        res.usesHideTags = message.getMentions() != null && switch (platform) {
            case Whatsapp, WhatsappMobile -> {
                for (Account account : message.getMentions()) {
                    if (!text.contains("@" + account.getId().split("@")[0])) {
                        yield true;
                    }
                }
                yield false;
            }
            case Discord -> false;
            case Telegram -> false;
            case Matrix -> false;
        };
        return res;
    }
    public static CommandMessageProperties ofLines(ExtendedMessageEvent event) {
        CommandMessageProperties c = ofLines((ExtendedMessage) event.getMessage(), event.getPlatform());
        c.originalMessageEvent = event;
        return c;
    }

    private static CommandMessageProperties ofLines(ExtendedMessage message, Platform platform) {
        String text = message.getText();
        Set<String> lines = text.lines().map(String::strip).filter(s -> !s.isEmpty()).collect(Collectors.toSet());
        CommandMessageProperties res = new CommandMessageProperties(lines.iterator().next(), message.getMentions(), message.getQuotedMessageSender(), platform);
        while(lines.iterator().hasNext()){
            String line = lines.iterator().next();
            CommandMessageProperties child = new CommandMessageProperties(line, message.getMentions(), message.getQuotedMessageSender(), platform);
            child.usesHideTags = message.getMentions() != null && switch (platform) {
                case Whatsapp, WhatsappMobile -> {
                    for (Account account : message.getMentions()) {
                        if (!text.contains("@" + account.getId().split("@")[0])) {
                            yield true;
                        }
                    }
                    yield false;
                }
                case Discord -> false;
                case Telegram -> false;
                case Matrix -> false;
            };
            res.childs.add(child);
        }
        return res;
    }

    public CommandMessageProperties(String line, List<Account> mentions, Account quotedMessageAccount, Platform platform){
        var commandWithPrefix = line.split(" ", 2)[0];
        originalText = line;
        prefix = commandWithPrefix.split("[A-Za-z0-9]", 2)[0];
        command = commandWithPrefix.substring(prefix.length());
        args = Set.of(line.split(" ")).stream().filter(s -> !s.startsWith("-") && !s.equals(commandWithPrefix)).collect(Collectors.toSet());
        argsLowerCase = args.stream().map(String::toLowerCase).collect(Collectors.toSet());
        flags = line.chars().mapToObj(c -> (char) c).filter(c -> c == '-').collect(Collectors.toSet());
        who = mentions == null ? (quotedMessageAccount != null ? Set.of(quotedMessageAccount) : null) : Set.copyOf(mentions);
        mentionsOfLine = mentions == null ? List.of() : mentions.stream().filter(account -> line.contains("@" + account.getId().split("@")[0])).collect(Collectors.toList());
    }

    public Set<ExtendedMessageEvent> refactorToMessageEvents(){
        Set<ExtendedMessageEvent> res = new HashSet<>();
        if(childs.isEmpty()){
            res.add(refactorToMessageEvent(originalMessageEvent, this));
        } else {
            for(CommandMessageProperties child : childs){
                res.add(refactorToMessageEvent(originalMessageEvent, child));
            }
        }
        return res;
    }

    public static ExtendedMessageEvent refactorToMessageEvent(ExtendedMessageEvent originalEvent, CommandMessageProperties props){
        ExtendedMessageBuilder emb = new ExtendedMessageBuilder((ExtendedMessage) originalEvent.getMessage())
                .text(props.originalText)
                .mentions(props.mentionsOfLine);
        return new ExtendedMessageEvent(originalEvent.getClient(), originalEvent.getId(), originalEvent.getSender(), originalEvent.getRoom(), originalEvent.getPlatform(), originalEvent.getTimestamp(), emb.build());
    }
}
