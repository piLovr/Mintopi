package com.github.pilovr.mintopi.starter.domain.common;

import com.github.pilovr.mintopi.starter.domain.event.ExtendedMessageEvent;
import com.github.pilovr.mintopi.starter.domain.event.MessageEvent;
import com.github.pilovr.mintopi.starter.domain.event.StubEvent;

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
        System.out.println("Received extended message: " + extendedMessageEvent.getMessage().getText());
    }

    default void onSpecialMessage(MessageEvent specialMessageEvent) {
        // Default implementation can be empty or can be overridden by subclasses
    }

    default void onConnected() {
        // Default implementation can be empty or can be overridden by subclasses
    }

    default void onDisconnected() {
        // Default implementation can be empty or can be overridden by subclasses
    }

    default void onStubEvent(StubEvent stubEvent) {
        // Default implementation can be empty or can be overridden by subclasses
    }
}
