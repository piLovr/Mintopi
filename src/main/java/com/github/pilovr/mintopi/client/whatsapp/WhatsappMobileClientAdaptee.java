package com.github.pilovr.mintopi.client.whatsapp;

import com.github.pilovr.mintopi.client.tools.MediaQueue;
import com.github.pilovr.mintopi.decoder.whatsapp.WhatsappEventDecoder;
import com.github.pilovr.mintopi.client.listener.WhatsappInternalListener;
import com.github.pilovr.mintopi.client.store.WhatsappStore;
import it.auties.whatsapp.api.Whatsapp;
import it.auties.whatsapp.api.WhatsappVerificationHandler;
import it.auties.whatsapp.model.companion.CompanionDevice;

import java.util.Scanner;

public class WhatsappMobileClientAdaptee extends WhatsappClientAdaptee {
    private long phoneNumber;
    public WhatsappMobileClientAdaptee(String alias, WhatsappInternalListener listener, WhatsappStore object, WhatsappEventDecoder decoder, MediaQueue mediaQueue) {
        super(alias, listener, wStoreOP.getObject(), decoder, mediaQueue);
    }

    @Override
    public void connect() {
        api = Whatsapp.builder()
                .mobileClient()
                .newConnection(alias)
                .device(CompanionDevice.ios(true)) // Make sure to select the correct account type(business or personal) or you'll get error 401
                .register(phoneNumber, WhatsappVerificationHandler.Mobile.sms(() -> {
                    String veriCode = "";
                    veriCode = internalListener.onInputRequired("Enter the verification code: ");
                    if(!veriCode.isEmpty()) {
                        return veriCode.trim().replace("-", "");
                    }

                    System.out.println("No Listener input received, using default scanner.");
                    return new Scanner(System.in)
                            .nextLine()
                            .trim()
                            .replace("-", "");
                }));

        api.addListener(internalListener)
                .connect();
        new Thread(() -> api.waitForDisconnection()).start();
    }
}
