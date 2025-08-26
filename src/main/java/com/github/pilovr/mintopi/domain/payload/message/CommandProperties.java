package com.github.pilovr.mintopi.domain.payload.message;

import com.github.pilovr.mintopi.domain.account.Account;
import com.github.pilovr.mintopi.domain.room.Room;
import lombok.Getter;

import java.util.Set;
import java.util.stream.Collectors;

@Getter
public class CommandProperties<R extends Room, A extends Account> {
    private final String command;
    private final Set<String> args; //HashSet
    private final Set<Character> flags; //HashSet
    private final Set<String> argsLowerCase; //HashSet
    private final String prefix; //All special characters at the start of the first word

    public CommandProperties(String text){
        var commandWithPrefix = text.split(" ", 2)[0];
        prefix = commandWithPrefix.split("[A-Za-z0-9]", 2)[0];
        command = commandWithPrefix.substring(prefix.length());
        args = Set.of(text.split(" ")).stream().filter(s -> !s.startsWith("-") && !s.equals(commandWithPrefix)).collect(Collectors.toSet());
        argsLowerCase = args.stream().map(String::toLowerCase).collect(Collectors.toSet());
        flags = text.chars().mapToObj(c -> (char) c).filter(c -> c == '-').collect(Collectors.toSet());
    }
}
