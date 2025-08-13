package com.github.pilovr.mintbot;


import com.github.pilovr.mintopi.starter.client.WhatsappClientAdaptee;
import com.github.pilovr.mintopi.starter.domain.common.Client;

public class Test {
    public static void main(String[] args) {
        Client test = new WhatsappClientAdaptee("test");
        test.addListener(new TestListener());
        test.connect();
    }
}
