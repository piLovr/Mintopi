package com.github.pilovr.mintopi.domain;

import com.github.pilovr.mintopi.domain.account.Account;
import com.github.pilovr.mintopi.domain.event.ExtendedMessageEvent;
import com.github.pilovr.mintopi.domain.event.MessageEvent;
import com.github.pilovr.mintopi.domain.event.ReactionMessageEvent;
import com.github.pilovr.mintopi.domain.event.StubEvent;
import com.github.pilovr.mintopi.domain.room.Room;

import java.util.Scanner;

public interface Listener<R extends Room, A extends Account> {
    default String onInputRequired(String message) {
        System.out.println("Input required: " + message);
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        System.out.println("Input received: " + input);
        return input;
    }

    default void onMessage(MessageEvent<?, R, A> messageEvent) {

    }

    default void onExtendedMessage(ExtendedMessageEvent<R, A> extendedMessageEvent) {
        // Default implementation can be empty or can be overridden by subclasses
        System.out.println("Received extended message: " + extendedMessageEvent.getMessage().getText());
    }

    default void onReactionMessage(ReactionMessageEvent<R,A> reactionMessageEvent) {
        // Default implementation can be empty or can be overridden by subclasses
    }

    default void onSpecialMessage(MessageEvent<?, R, A> specialMessageEvent) {
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
