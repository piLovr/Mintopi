package com.github.pilovr.mintopi.client.whatsapp;

public enum WhatsappDisconnectReason {
    /**
     * Default errorReason
     */
    DISCONNECTED,

    /**
     * Reconnect
     */
    RECONNECTING,

    /**
     * Logged out
     */
    LOGGED_OUT,

    /**
     * Ban
     */
    BANNED;

    public static WhatsappDisconnectReason from(it.auties.whatsapp.api.WhatsappDisconnectReason reason) {
        return switch (reason) {
            case DISCONNECTED -> DISCONNECTED;
            case RECONNECTING -> RECONNECTING;
            case LOGGED_OUT -> LOGGED_OUT;
            case BANNED -> BANNED;
        };
    }
}
