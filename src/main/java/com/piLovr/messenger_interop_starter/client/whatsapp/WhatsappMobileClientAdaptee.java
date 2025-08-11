package com.piLovr.messenger_interop_starter.client.whatsapp;

import com.piLovr.messenger_interop_starter.listener.Listener;
import it.auties.whatsapp.api.Whatsapp;
import it.auties.whatsapp.api.WhatsappVerificationHandler;
import it.auties.whatsapp.model.companion.CompanionDevice;

import java.util.Scanner;

public class WhatsappMobileClientAdaptee extends WhatsappClientAdaptee {
    private long phoneNumber;
    public WhatsappMobileClientAdaptee(String alias) {
        super(alias);
    }

    @Override
    public void connect() {
        client = Whatsapp.builder()
                .mobileClient()
                .newConnection(alias)
                .device(CompanionDevice.ios(true)) // Make sure to select the correct account type(business or personal) or you'll get error 401
                .register(phoneNumber, WhatsappVerificationHandler.Mobile.sms(() -> {
                    String veriCode = "";
                    for(Listener listener : listeners) {
                        veriCode = listener.onInputRequired("Enter the verification code: ");
                        if(!veriCode.isEmpty()) {
                            return veriCode.trim().replace("-", "");
                        }
                    }
                    System.out.println("No Listener input received, using default scanner.");
                    return new Scanner(System.in)
                            .nextLine()
                            .trim()
                            .replace("-", "");
                }));

        client.addListener(internalListener)
                .connect();
        new Thread(() -> client.waitForDisconnection()).start();
    }
}
