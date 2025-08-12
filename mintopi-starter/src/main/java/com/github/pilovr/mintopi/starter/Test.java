package com.github.pilovr.mintopi.starter;

import com.github.pilovr.mintopi.core.common.Client;
import com.github.pilovr.mintopi.starter.client.WhatsappClientAdaptee;

public class Test {
    public static void main(String[] args) {
        Client test = new WhatsappClientAdaptee("test");
        test.addListener(new TestListener());
        test.connect();


    }
}
