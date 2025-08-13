package com.github.pilovr.mintopi.starter.domain.common;

import com.github.pilovr.mintopi.starter.domain.event.ExtendedMessageEvent;
import com.github.pilovr.mintopi.starter.domain.event.MessageEvent;

import java.util.Scanner;

public interface Listener {
    default String onInputRequired(String message) {
        System.out.println("Input required: " + message);
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        System.out.println("Input received: " + input);
        return input;
    }

    default void onMessage(MessageEvent messageEvent) {

    }

    default void onExtendedMessage(ExtendedMessageEvent extendedMessageEvent) {
        // Default implementation can be empty or can be overridden by subclasses
    }

    default void onSpecialMessage(MessageEvent specialMessageEvent) {
        // Default implementation can be empty or can be overridden by subclasses
    }

}
