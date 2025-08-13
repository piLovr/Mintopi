package com.github.pilovr.mintbot;

import com.github.pilovr.mintopi.starter.domain.common.Listener;
import com.github.pilovr.mintopi.starter.domain.event.ExtendedMessageEvent;
import com.github.pilovr.mintopi.starter.domain.event.MessageEvent;

public class TestListener implements Listener {
    @Override
    public void onExtendedMessage(ExtendedMessageEvent extendedMessageEvent) {
        // Handle the extended message event
        System.out.println("Extended message received: " + extendedMessageEvent.getMessage().getText());
    }
}
