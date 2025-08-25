package com.github.pilovr.mintopi.client;

import lombok.Getter;

@Getter
public enum Platform {
    Whatsapp("w"),
    WhatsappMobile("w"),
    Discord("d"),
    Telegram("t"),
    Matrix("m");


    private final String idPrefix;
    Platform(String idPrefix) {
        this.idPrefix = idPrefix;
    }

}
