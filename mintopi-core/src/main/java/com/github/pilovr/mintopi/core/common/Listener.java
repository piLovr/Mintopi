package com.github.pilovr.mintopi.core.common;

import com.github.pilovr.mintopi.core.event.MessageEvent;

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

}
