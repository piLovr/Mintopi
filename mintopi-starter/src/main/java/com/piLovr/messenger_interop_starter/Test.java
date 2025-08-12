package com.piLovr.messenger_interop_starter;

import com.piLovr.messenger_interop_starter.client.Client;
import com.piLovr.messenger_interop_starter.client.whatsapp.WhatsappClientAdaptee;

public class Test {
    public static void main(String[] args) {
        Client test = new WhatsappClientAdaptee("test");
        test.addListener(new TestListener());
        test.connect();


    }
}
