package com.github.pilovr.mintopi.listener;

import com.github.pilovr.mintopi.domain.account.Account;
import com.github.pilovr.mintopi.domain.event.EventContext;
import com.github.pilovr.mintopi.domain.payload.message.ReactionMessagePayload;
import com.github.pilovr.mintopi.domain.payload.message.TextMessagePayload;
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

    default void onMessage(EventContext<?,R,A> messageEvent) {

    }

    default void onTextMessage(EventContext<TextMessagePayload<A>,R,A> eventContext) {
        // Default implementation can be empty or can be overridden by subclasses
        System.out.println("Received extended message: " + eventContext.getPayload().getText());
    }

    default void onReactionMessage(EventContext<ReactionMessagePayload,R,A> reactionMessageEvent) {
        // Default implementation can be empty or can be overridden by subclasses
    }

    default void onSpecialMessage(EventContext<?,R,A> specialMessageEvent) {
        // Default implementation can be empty or can be overridden by subclasses
    }

    default void onConnected() {
        // Default implementation can be empty or can be overridden by subclasses
    }

    default void onDisconnected() {
        // Default implementation can be empty or can be overridden by subclasses
    }

    default void onStubEvent(EventContext<?,R,A> stubEvent) {
        // Default implementation can be empty or can be overridden by subclasses
    }
}
